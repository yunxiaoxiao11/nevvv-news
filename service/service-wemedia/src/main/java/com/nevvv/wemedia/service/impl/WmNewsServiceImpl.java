package com.nevvv.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.baidu.aip.ocr.AipOcr;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hankcs.algorithm.AhoCorasickDoubleArrayTrie;
import com.nevvv.common.constant.BaseEnum;
import com.heima.green.scan.GreenImageScan;
import com.heima.green.scan.GreenTextScan;
import com.nevvv.model.common.dtos.PageResponseResult;
import com.nevvv.model.common.dtos.ResponseResult;
import com.nevvv.model.common.enums.HttpCodeEnum;
import com.nevvv.model.common.wemedia.dto.PageWeNewsDto;
import com.nevvv.model.common.wemedia.dto.WeDownUpDto;
import com.nevvv.model.common.wemedia.dto.WeNews2ArticleDto;
import com.nevvv.model.common.wemedia.dto.WeNewsDto;
import com.nevvv.model.common.wemedia.pojo.*;
import com.nevvv.utils.shareData.BaseContext;
import com.nevvv.wemedia.mapper.WmNewsMapper;
import com.nevvv.wemedia.mapper.WmSensitiveMapper;
import com.nevvv.wemedia.service.WmChannelService;
import com.nevvv.wemedia.service.WmNewsMaterialService;
import com.nevvv.wemedia.service.WmNewsService;
import com.nevvv.wemedia.service.WmUserService;
import com.jlefebure.spring.boot.minio.MinioException;
import com.jlefebure.spring.boot.minio.MinioService;
import com.pojo.ScanResult;
import feign.article.ArticleFeign;
import freemarker.template.Configuration;
import freemarker.template.Template;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * <p>
 * 自媒体图文内容信息表 服务实现类
 * </p>
 *
 * @author author
 * @since 2023-06-03
 */
@Service
@Slf4j
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {
    @Autowired
    private WmNewsMaterialService newsMaterialService;
    @Autowired
    private WmMaterialServiceImpl materialService;
    @Autowired
    private WmUserService userService;
    @Autowired
    private WmChannelService channelService;
    @Autowired
    private WmSensitiveMapper sensitiveMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private GreenTextScan textScan;
    @Autowired
    private GreenImageScan imageScan;
    @Autowired
    private MinioService minioService;
    @Autowired
    private ArticleFeign articleFeign;
    @Autowired
    private Configuration configuration;
    @Autowired
    private AipOcr ocr;
    //    @Autowired
//    private RabbitTemplate rabbitTemplate;
    @Value("${spring.minio.url}")
    private String url;
    @Value("${spring.minio.bucket}")
    private String bucket;
    //    private final static String EXCHANGE = "direct";
//    private final static String ROUTING_KEY = "blue";
    private String FILE_PATH = System.getProperty("user.home") + File.separator + "temp" + File.separator;
    private String HTML_POST = ".html";

    /**
     * 带条件的分页查询
     *
     * @param pageWeNewsDto
     * @return
     */
    @Override
    public ResponseResult listService(PageWeNewsDto pageWeNewsDto) {
        pageWeNewsDto.checkParam();
        WmNews wmNews = new WmNews();
        BeanUtils.copyProperties(pageWeNewsDto, wmNews);
        IPage<WmNews> page = new Page<>(pageWeNewsDto.getPage(), pageWeNewsDto.getSize());
        LambdaQueryWrapper<WmNews> queryWrapper = new LambdaQueryWrapper();
        if (StringUtils.isNotEmpty(pageWeNewsDto.getKeyword())) {
            queryWrapper.like(WmNews::getTitle, pageWeNewsDto.getKeyword());
        }
        if (pageWeNewsDto.getStatus() != null) {
            queryWrapper.eq(WmNews::getStatus, pageWeNewsDto.getStatus());
        }
        if (pageWeNewsDto.getChannelId() != null) {
            queryWrapper.eq(WmNews::getChannelId, pageWeNewsDto.getChannelId());
        }
        if (pageWeNewsDto.getBeginPubdate() != null && pageWeNewsDto.getEndPubdate() != null) {
            queryWrapper.between(WmNews::getPublishTime, pageWeNewsDto.getBeginPubdate(), pageWeNewsDto.getEndPubdate());
        }
        queryWrapper.eq(WmNews::getUserId, BaseContext.getThreadId());
        //
        IPage<WmNews> result = page(page, queryWrapper);
        return PageResponseResult.okResult(pageWeNewsDto.getPage(), pageWeNewsDto.getSize(), result.getTotal(), result.getRecords());
    }

    /**
     * 删除文章信息
     *
     * @param id
     * @return
     */
    @Override
    @Transactional
    public ResponseResult delById(Integer id) {
        //删除文章对应表和中间表
        WmNews byId = getById(id);
        if (byId.getStatus() == 9) {
            return ResponseResult.errorResult(HttpCodeEnum.NO_OPERATOR_AUTH);
        } else {
            removeById(id);
            if (byId.getStatus() != 0) {
                //如果不是草稿需要删除中间表中信息
                newsMaterialService.remove(new LambdaQueryWrapper<WmNewsMaterial>().eq(WmNewsMaterial::getNewsId, id));
            }
            return ResponseResult.okResult();
        }
    }

    /**
     * 查找审核通过待发布的文章
     *
     * @return
     */
    @Override
    public ResponseResult<List<WmNews>> getArticle() {
        LambdaQueryWrapper<WmNews> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WmNews::getStatus, BaseEnum.ART_PASS.getValue());
        queryWrapper.lt(WmNews::getPublishTime, new Date());
        List<WmNews> list = list(queryWrapper);
        return ResponseResult.okResult(list);
    }

    /**
     * 上下架文章kafka
     *
     * @param downUpDto
     * @return
     */
    @Override
    public ResponseResult downOrUp(WeDownUpDto downUpDto) {
        Long id = downUpDto.getId();
        Boolean enable = downUpDto.getEnable();
        WmNews byId = getById(id);
        if ((byId == null)) {
            return ResponseResult.errorResult(HttpCodeEnum.DATA_NOT_EXIST);
        } else if (byId.getStatus() != BaseEnum.ART_PUBLISH.getValue()) {
            return ResponseResult.errorResult(HttpCodeEnum.NO_OPERATOR_AUTH);
        }
        byId.setEnable(enable);
        log.info("上下架更新news表...");
        updateById(byId);
        Long articleId = byId.getArticleId();
        kafkaTemplate.send("news_down_up_topic", String.valueOf(articleId), String.valueOf(enable));
        return ResponseResult.okResult();
    }

    /**
     * 远程调用发布审核通过的文章
     *
     * @param news
     * @return
     */
    @Override
    public ResponseResult publish(WmNews news) throws Exception {
        return artPublish(news);
    }

    /**
     * 根据id查找符合发布条件的文章
     *
     * @param id
     * @return
     */
    @Override
    public WmNews findById(Integer id) {
        LambdaQueryWrapper<WmNews> queryWrapper = new LambdaQueryWrapper<>();
        //状态为8,发布时间小于当前时间
        queryWrapper.eq(WmNews::getId, id);
        queryWrapper.eq(WmNews::getStatus, BaseEnum.ART_PASS.getValue());
        queryWrapper.lt(WmNews::getPublishTime, new Date());
        return getOne(queryWrapper);
    }

    /**
     * 发布或修改文章
     *
     * @param weNewsDto
     * @return
     */
    @Override
    @GlobalTransactional
    public ResponseResult submit(WeNewsDto weNewsDto) throws Exception {
        WmNews wmNews = new WmNews();
        BeanUtils.copyProperties(weNewsDto, wmNews);
        //补全属性
        wmNews.setUserId(BaseContext.getThreadId());
        wmNews.setCreatedTime(new Date());
        //获取文章内容中的图片
        List<String> contentImgList = getContentImage(weNewsDto.getContent());
        //处理文章内容,设置状态
        disposeContent(weNewsDto, wmNews, contentImgList);
        if (weNewsDto.getStatus() == BaseEnum.ART_DRAFT.getValue()) {
            //点击存草稿
            log.info("当前点击保存为草稿...");
            if (weNewsDto.getId() != null) {
                log.info("当前为修改草稿...");
                wmNews.setId(Long.valueOf(weNewsDto.getId()));
                updateById(wmNews);
                return ResponseResult.okResult();
            } else {
                log.info("当前为新增草稿...");
                save(wmNews);
                return ResponseResult.okResult("保存草稿成功!");
            }
        } else {
            log.info("当前点击提交审核...");
            //判断修改还是添加文章
            if (weNewsDto.getId() == null) {
                //添加
                log.info("当前为新增提交审核...");
                save(wmNews);
                changeNewsMaterial(wmNews, contentImgList);
            } else {
                log.info("当前为修改提交审核...");
                wmNews.setId(Long.valueOf(weNewsDto.getId()));
                updateById(wmNews);
                //删除中间表中的所有newId的表数据
                newsMaterialService.remove(new LambdaQueryWrapper<WmNewsMaterial>().eq(WmNewsMaterial::getNewsId, wmNews.getId()));
                changeNewsMaterial(wmNews, contentImgList);
            }
            //提交审核
            log.info("开始审核...");
            boolean safeResult = false;
            try {
                safeResult = getSafeResult(wmNews, contentImgList);
            } catch (Exception e) {
                log.info("minio中不存在这个图片!");
                return ResponseResult.errorResult(HttpCodeEnum.DATA_NOT_EXIST);
            }
            if (safeResult) {
                log.info("文章内容审核通过!");
                //审核通过
                wmNews.setStatus(BaseEnum.ART_PASS.getValue());
                log.info("修改文章状态为审核通过待发布!");
                updateById(wmNews);
                //判断是否即时发布
                if (wmNews.getPublishTime()
                        .before(new Date())) {
                    log.info("文章现在发布!");
                    return artPublish(wmNews);
                } else {
                    log.info("文章延后发布!");
                    /*//向RabbitMq发送消息
                    Message message = MessageBuilder.withBody(String.valueOf(wmNews.getId())
                                    .getBytes())
                            .setExpiration(String.valueOf(wmNews.getPublishTime()
                                    .getTime() - System.currentTimeMillis()))
                            .build();
                    rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, message);
                    log.info("向mq发送消息成功...");*/
                    log.info("向redis中保存当前要延迟发布的文章信息");
                    redisTemplate.opsForZSet()
                            .add("wmNews", String.valueOf(wmNews.getId()), wmNews.getPublishTime()
                                    .getTime());
                    log.info("redis中保存成功!");
                }
                return ResponseResult.okResult(BaseEnum.ART_PASS.getName());
            }
            //未通过
            //设置状态为2
            log.info("文章审核未通过!");
            updateById(wmNews);
            return ResponseResult.okResult(BaseEnum.ART_AUDIT_FAILED.getName());
        }
    }

    private ResponseResult artPublish(WmNews wmNews) throws Exception {
        log.info("开始发布...");
        WeNews2ArticleDto dto = new WeNews2ArticleDto();
        BeanUtils.copyProperties(wmNews, dto);
        WmUser userOne = userService.getOne(new LambdaQueryWrapper<WmUser>().eq(WmUser::getId, wmNews.getUserId()));
        if (userOne != null) {
            dto.setAuthorName(userOne.getName());
        }
        WmChannel channelOne = channelService.getOne(new LambdaQueryWrapper<WmChannel>().eq(WmChannel::getId, wmNews.getChannelId()));
        if (channelOne != null) {
            dto.setChannelName(channelOne.getName());
        }
        //生成静态html
        String uuidName = generateHtml(wmNews.getContent());
        String nameExt = uuidName + HTML_POST;
        String path = FILE_PATH + nameExt;
        //设置静态页面minio访问地址
        dto.setStaticUrl(url + "/" + bucket + "/" + nameExt);
        //远程调用article服务
        log.info("远程调用article服务发布文章...");
        ResponseResult<Long> articleId = articleFeign.save(dto);
        if (articleId.getCode() == HttpCodeEnum.SUCCESS.getCode()) {
            log.info("远程调用成功!");
            wmNews.setArticleId(articleId.getData());
            //保存文章成功后将本地文件上传到minio后删除
            try {
                //健壮性判断,如果上传失败不删除本地文件
                upload2Minio(nameExt, path);
                new File(path).delete();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            log.info("远程调用成功!");
            wmNews.setStatus(BaseEnum.ART_PUBLISH.getValue());
            log.info("修改文章状态为已发布");
            updateById(wmNews);
            return ResponseResult.okResult(BaseEnum.ART_PUBLISH.getName());
        }
        return ResponseResult.errorResult(HttpCodeEnum.SERVER_ERROR);
    }

    /**
     * 生成html保存到本地,返回本地不包含拓展名的文件名
     *
     * @param content
     * @return
     * @throws Exception
     */
    private String generateHtml(String content) throws Exception {
        Template template = configuration.getTemplate("news.ftl");
        String uuidName = UUID.randomUUID()
                .toString();
        List<Content> list = JSON.parseArray(content, Content.class);
        Map<String, List> map = new HashMap<>();
        map.put("data", list);
        //写入到本地文件
        Writer writer = new FileWriter(FILE_PATH + uuidName + HTML_POST);
        template.process(map, writer);
        writer.close();
        return uuidName;
    }

    private void upload2Minio(String nameExt, String path) throws MinioException, IOException {
        FileInputStream fileInputStream = new FileInputStream(path);
        log.info("上传本地HTML文件到minio...");
        minioService.upload(Paths.get(nameExt), fileInputStream, ContentType.TEXT_HTML);
        fileInputStream.close();
    }

    /**
     * 根据文章内容,设置图片数量
     *
     * @param weNewsDto
     * @param wmNews
     * @param contentImgList
     */
    private void disposeContent(WeNewsDto weNewsDto, WmNews wmNews, List<String> contentImgList) {
        //自动类型
        if (weNewsDto.getType() == -1) {
            //设置文章图片类型:无图,单图,三图
            if (contentImgList.size() == 0) {
                wmNews.setImages(null);
                wmNews.setType(BaseEnum.NO_PIC.getValue());
            } else if (contentImgList.size() < 3) {
                wmNews.setImages(contentImgList.get(0));
                wmNews.setType(BaseEnum.ONE_PIC.getValue());
            } else {
                wmNews.setImages(contentImgList.get(0) + "," + contentImgList.get(1) + "," + contentImgList.get(2));
                wmNews.setType(BaseEnum.THREE_PIC.getValue());
            }
            //前端传图片类型
        } else {
            String images = weNewsDto.getImages();
            List<String> imageList = JSON.parseArray(images, String.class);
            wmNews.setImages(StringUtils.join(imageList, ","));
            wmNews.setType(imageList.size());
        }
    }

    /**
     * 文章审核(文本,图片)
     *
     * @param wmNews
     * @param contentImgList
     * @return
     */
    private boolean getSafeResult(WmNews wmNews, List<String> contentImgList) throws Exception {
        //文章审核,审核未通过的任何状态都需要在这里设置,外边不进行设置直接更新
        String content = getContent(wmNews.getContent());
        if (!contentCheck(content)) {
            log.info("文章内容审核未通过...");
            wmNews.setStatus(BaseEnum.ART_AUDIT_FAILED.getValue());
            return false;
        } else {
            log.info("开始图片审核...");
            FileInputStream fis = null;
            if (contentImgList != null && contentImgList.size() > 0) {
                //图片审核
                for (String s : contentImgList) {
                    //1.获取图片流
                    String urlName = FilenameUtils.getName(s);
                    String localSavePath = FILE_PATH + urlName;
                    //2.将素材中的图片从minio中下载到本地
                    log.info("从minio中下载要识别的图片到本地");
                    Path path = Paths.get(urlName);
                    minioService.getAndSave(path, localSavePath);
                    fis = new FileInputStream(localSavePath);
                    ScanResult result = imageScan.imageScan(fis);
                    //识别完毕删除下载到本地的文件
                    if (BaseEnum.PIC_REVIEW.getName()
                            .equals(result.getSuggestion())) {
                        log.info("图片审核review,无法识别,交由人工审核...");
                        wmNews.setStatus(BaseEnum.ART_HUMAN_REVIEW.getValue());
                        log.info("修改文章状态为人工审核!");
                        return false;
                    } else if (BaseEnum.PIC_BLOCK.getName()
                            .equals(result.getSuggestion())) {
                        log.info("图片审核block,审核不通过...");
                        wmNews.setStatus(BaseEnum.ART_AUDIT_FAILED.getValue());
                        log.info("修改文章状态为审核不通过!");
                        return false;
                    } else {
                        //3.调用ocr识别
                        if (!contentCheck(ocrString(localSavePath))) {
                            log.info("图片ocr审核block,审核不通过...");
                            wmNews.setStatus(BaseEnum.ART_AUDIT_FAILED.getValue());
                            return false;
                        }
                    }
                    new File(localSavePath).delete();
                }
            }
            if (fis != null) {
                fis.close();
            }
            return true;
        }
    }

    /**
     * ocr识别图片结果
     *
     * @param localSavePath
     * @return
     */
    private String ocrString(String localSavePath) {
        JSONObject jsonObject = ocr.basicGeneral(localSavePath, new HashMap<>());
        JSONArray jsonArray = jsonObject.getJSONArray(BaseEnum.OCR_WORDS_RESULT.getName());
        StringBuilder sb = new StringBuilder();
        for (Object o : jsonArray) {
            JSONObject jobj = (JSONObject) o;
            //将识别的文本保存到sb中
            sb.append(jobj.getString(BaseEnum.OCR_WORDS.getName()));
        }
        log.info("ocr图片文本内容完成并返回识别结果...");
        return sb.toString();
    }

    /**
     * 文本审核
     *
     * @param
     * @param content
     * @return
     */
    private boolean contentCheck(String content) throws Exception {
        log.info("开始自定义敏感词审核...");
        TreeMap<String, String> map = new TreeMap<>();
        //读取自定义敏感词库的词表
        for (String s : sensitiveMapper.wordList()) {
            map.put(s, s);
        }
        AhoCorasickDoubleArrayTrie<String> acdat = new AhoCorasickDoubleArrayTrie<>();
        acdat.build(map);
        //自定义敏感词审核
        List<AhoCorasickDoubleArrayTrie.Hit<String>> hits = acdat.parseText(content);
        if (hits.size() > 0) {
            log.info("自定义敏感词审核未通过...匹配内容为:{}", hits);
            return false;
        }
        //文章内容审核
        log.info("自定义敏感词审核通过...");
        log.info("开始文章内容审核...");
        ScanResult result = textScan.greeTextScan(content);
        if (result == null) {
            return true;
        } else {
            return BaseEnum.ART_PASS1.getName()
                    .equals(result.getSuggestion());
        }
    }

    /**
     * 整合文章内容
     *
     * @param
     * @return
     */
    private String getContent(String content) {
        //整合文章内容
        StringBuilder stringBuilder = new StringBuilder();
        List<Content> contentList = JSON.parseArray(content, Content.class);
        for (Content s : contentList) {
            if ("text".equals(s.getType())) {
                stringBuilder.append(s.getValue());
            }
        }
        log.info("整合文章文本内容完毕...");
        return stringBuilder.toString();
    }

    /**
     * 文章素材中间表信息添加
     *
     * @param wmNews
     * @param contentImgList
     * @return
     */
    private void changeNewsMaterial(WmNews wmNews, List<String> contentImgList) {
        //添加图片信息到素材-新闻中间表
        WmNewsMaterial nMentity = new WmNewsMaterial();
        nMentity.setNewsId(wmNews.getId());
        //获取封面中的素材信息添加中间表
        String images1 = wmNews.getImages();
        if (images1 != null) {
            String[] split = images1.split(",");
            for (String s : split) {
                WmMaterial one = materialService.getOne(new LambdaQueryWrapper<WmMaterial>().eq(WmMaterial::getUrl, s));
                if (one != null) {
                    nMentity.setMaterialId(one.getId());
                    nMentity.setType(BaseEnum.FILE_VIDEO.getValue());
                    newsMaterialService.save(nMentity);
                }
            }
        }

        //获取内容中的素材信息添加到中间表
        assert contentImgList != null;
        for (String s : contentImgList) {
            WmMaterial one = materialService.getOne(new LambdaQueryWrapper<WmMaterial>().eq(WmMaterial::getUrl, s));
            if (one != null) {
                nMentity.setMaterialId(one.getId());
                nMentity.setType(BaseEnum.FILE_PIC.getValue());
                newsMaterialService.save(nMentity);
            }
        }
    }

    /**
     * 获取文章内容中的图片
     *
     * @param content
     * @return List<String>
     */
    private List<String> getContentImage(String content) {
        //获取文章内容中的图片信息
        List<String> iList = new ArrayList<>();
        List<Content> contents = JSON.parseArray(content, Content.class);
        for (Content c : contents) {
            if (c.getType()
                    .equals("image")) {
                iList.add(c.getValue());
            }
        }
        return iList;
    }
}