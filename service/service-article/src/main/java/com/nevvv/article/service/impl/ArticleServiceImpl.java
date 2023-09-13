package com.nevvv.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nevvv.article.mapper.ArticleMapper;
import com.nevvv.article.mapper.CollectionMapper;
import com.nevvv.article.service.ArticleConfigService;
import com.nevvv.article.service.ArticleContentService;
import com.nevvv.article.service.ArticleService;
import com.nevvv.common.constant.BaseEnum;
import com.nevvv.model.common.article.dto.ArticleDocDto;
import com.nevvv.model.common.article.dto.ArticleHomeDto;
import com.nevvv.model.common.article.dto.ArticleHotDto;
import com.nevvv.model.common.article.pojo.Article;
import com.nevvv.model.common.article.pojo.ArticleConfig;
import com.nevvv.model.common.article.pojo.ArticleContent;
import com.nevvv.model.common.article.pojo.Collection;
import com.nevvv.model.common.dtos.ResponseResult;
import com.nevvv.model.common.wemedia.dto.WeNews2ArticleDto;
import com.nevvv.model.common.wemedia.pojo.Content;
import com.nevvv.utils.shareData.BaseContext;
import feign.user.UserFeign;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 文章信息表，存储已发布的文章 服务实现类
 * </p>
 *
 * @author
 * @since 2023-05-31
 */
@Service
@Slf4j
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private CollectionMapper collectionMapper;
    @Autowired
    private ArticleConfigService configService;
    @Autowired
    private ArticleContentService contentService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private UserFeign userFeign;

    /**
     * 下拉刷新
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult<List<Article>> loadNew(ArticleHomeDto dto) {
        List<Article> articles = articleMapper.loadNew(dto.getTag(), dto.getSize(), dto.getMaxBehotTime());
        return ResponseResult.okResult(articles);
    }

    /**
     * 上滑加载
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult<List<Article>> loadMore(ArticleHomeDto dto) {
        List<Article> articles = articleMapper.loadMore(dto.getTag(), dto.getSize(), dto.getMinBehotTime());
        return ResponseResult.okResult(articles);
    }

    /**
     * 加载推荐内容
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult<List<Article>> loadNewByTag(ArticleHomeDto dto) {
        List<Article> articles = articleMapper.loadNewByTag(dto.getTag(), dto.getSize());
        return ResponseResult.okResult(articles);
    }

    /**
     * 搜索服务远程调用接口,根据id查找article内容
     * @param id
     * @return
     */
    @Override
    public ResponseResult<ArticleDocDto> findById(Long id) {
        ArticleDocDto articleDocDto = articleMapper.findById(id);
        String content = getStringBuilder(articleDocDto.getContent());
        articleDocDto.setContent(content);
        return ResponseResult.okResult(articleDocDto);
    }

    /**
     * 查询所有符合es添加条件的文章
     * @return
     */
    @Override
    public ResponseResult<List<ArticleDocDto>> findAll() {
        List<ArticleDocDto> dtoList = articleMapper.findAll();
        for (ArticleDocDto articleDocDto : dtoList) {
            String content = getStringBuilder(articleDocDto.getContent());
            articleDocDto.setContent(content);
        }
        return ResponseResult.okResult(dtoList);
    }

    private String getStringBuilder(String content) {
        List<Content> contentSegments = JSON.parseArray(content, Content.class);
        StringBuilder stringBuilder = new StringBuilder();
        for (Content contentSegment : contentSegments) {
            String type = contentSegment.getType();
            String value = contentSegment.getValue();
            if (type.equals("text")) {
                stringBuilder.append(value);
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 根据频道id查找热点文章
     * @param chanalId
     * @return
     */
    @Override
    public ResponseResult<List<ArticleHotDto>> getHotNewsByChannalId(String chanalId) {
        String s = redisTemplate.opsForValue()
                .get("hotArticl-" + chanalId);
        return ResponseResult.okResult(JSON.parseArray(s, ArticleHotDto.class));
    }

    /**
     * redis中存储各个频道的热点文章
     */
    @Override
    public void hotNews() {
        log.info("获取文章表中的所有热点新闻...");
        Date date = new Date();
        Date date1 = DateUtils.addDays(date, -5);
        List<ArticleHotDto> articleHotDtos = articleMapper.hotNews(date1);
        Map<Integer, List<ArticleHotDto>> collect = articleHotDtos.stream()
                .collect(Collectors.groupingBy(ArticleHotDto::getChannelId));
        // 这里直接用map的方式来存储
        Set<Integer> keys = collect.keySet();
        for (Integer integer : keys) {
            List<ArticleHotDto> articleHotDtoList = collect.get(integer);
            List<ArticleHotDto> collect1 = articleHotDtoList.stream()
                    .limit(10)
                    .collect(Collectors.toList());
            log.info("存储到redis中");
            redisTemplate.opsForValue()
                    .set("hotarticle-" + integer, JSON.toJSONString(collect1));
        }
        log.info("推荐文章的热点新闻...");
        redisTemplate.opsForValue()
                .set("hotArticl-__all__", JSON.toJSONString((articleHotDtos.stream()
                        .limit(10)
                        .collect(Collectors.toList()))));
    }

    /**
     * 前端加载文章行为,数据回显
     * @param map
     * @return
     */
    @Override
    public ResponseResult loadBehavior(Map<String, String> map) {
        String articleId = map.get("articleId");
        Map<String, Boolean> mapVo = new HashMap<>();
        if (BaseContext.getThreadId() == null) {
            log.info("用户未登录,响应false!");
            mapVo.put("islike", false);
            mapVo.put("iscollection", false);
            mapVo.put("isfollow", false);
            return ResponseResult.okResult(mapVo);
        } else {
            log.info("用户登录!");
            String userId = String.valueOf(BaseContext.getThreadId());
            Map<String, String> mapDto = new HashMap<>();
            mapDto.put("userId", userId);
            mapDto.put("authorId", userId);
            log.info("远程调用用户服务查询当前登录用户是否关注了这个作者...");
            ResponseResult reviewInfo = userFeign.getReviewInfo(mapDto);
            if (reviewInfo.getCode() != null) {
                log.info("当前用户关注了这个作者!");
                mapVo.put("isfollow", true);
            } else {
                log.info("当前用户未关注这个作者!");
                mapVo.put("isfollow", false);
            }
            log.info("在redis中查找当前用户是否like这篇文章...");
            String like = (String) redisTemplate.opsForHash()
                    .get(BaseEnum.LIKE_PRE.getName() + articleId, userId);
            if ("0".equals(like)) {
                log.info("当前用户喜欢这篇文章!");
                mapVo.put("islike", true);
            } else {
                log.info("当前用户不喜欢这篇文章!");
                mapVo.put("islike", false);
            }
            log.info("在收藏表中查找当前用户是否有");
            Collection collection = collectionMapper.find(userId, articleId);
            if (collection != null) {
                log.info("当前用户收藏了这篇文章!");
                mapVo.put("iscollection", true);
            } else {
                log.info("当前用户没有收藏这篇文章!");
                mapVo.put("iscollection", false);
            }
            return ResponseResult.okResult(mapVo);
        }
    }

    /**
     * 接收News服务的发布文章请求
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult<Long> saveFromNews(WeNews2ArticleDto dto) {
        //保存文章基本信息表
        Article article = new Article();
        BeanUtils.copyProperties(dto, article);
        article.setAuthorId(dto.getUserId());
        article.setCollection(BaseEnum.NEW_ARTICLE_NUM.getValue());
        article.setLikes(BaseEnum.NEW_ARTICLE_NUM.getValue());
        article.setComment(BaseEnum.NEW_ARTICLE_NUM.getValue());
        article.setViews(BaseEnum.NEW_ARTICLE_NUM.getValue());
        article.setFlag(BaseEnum.NEW_ARTICLE_NUM.getValue());
        if (dto.getImages() != null) {
            article.setLayout(Arrays.asList(dto.getImages()
                            .split(","))
                    .size());
        }
        article.setPublishTime(new Date());
        article.setCreatedTime(new Date());
        save(article);
        //保存文章配置表
        ArticleConfig articleConfig = new ArticleConfig();
        articleConfig.setArticleId(article.getId());
        articleConfig.setIsComment(BaseEnum.IS_COMMENT.getValue());
        articleConfig.setIsForward(BaseEnum.IS_COMMENT.getValue());
        articleConfig.setIsDelete(BaseEnum.IS_NOT_COMMENT.getValue());
        articleConfig.setIsDown(BaseEnum.IS_NOT_COMMENT.getValue());
        configService.save(articleConfig);
        //保存文章内容表
        ArticleContent articleContent = new ArticleContent();
        articleContent.setArticleId(article.getId());
        articleContent.setContent(dto.getContent());
        contentService.save(articleContent);
        //返回生成的文章id
        return ResponseResult.okResult(article.getId());
    }
}
