package com.nevvv.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nevvv.common.constant.BaseEnum;
import com.nevvv.model.common.dtos.PageResponseResult;
import com.nevvv.model.common.dtos.ResponseResult;
import com.nevvv.model.common.enums.HttpCodeEnum;
import com.nevvv.model.common.wemedia.dto.PageWemediaDto;
import com.nevvv.model.common.wemedia.pojo.WmMaterial;
import com.nevvv.model.common.wemedia.pojo.WmNewsMaterial;
import com.nevvv.utils.shareData.BaseContext;
import com.nevvv.wemedia.mapper.WmMaterialMapper;
import com.nevvv.wemedia.service.WmMaterialService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nevvv.wemedia.service.WmNewsMaterialService;
import com.jlefebure.spring.boot.minio.MinioException;
import com.jlefebure.spring.boot.minio.MinioService;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * <p>
 * 自媒体图文素材信息表 服务实现类
 * </p>
 *
 * @author author
 * @since 2023-06-03
 */
@Service
public class WmMaterialServiceImpl extends ServiceImpl<WmMaterialMapper, WmMaterial> implements WmMaterialService {
    @Autowired
    private MinioService minioService;
    @Value("${spring.minio.url}")
    private String url;
    @Value("${spring.minio.bucket}")
    private String bucket;
    @Autowired
    private WmNewsMaterialService materialService;

    /**
     * 素材收藏
     *
     * @param id
     * @return
     */
    @Override
    public ResponseResult collect(Integer id) {
        //判断素材是否存在
        LambdaQueryWrapper<WmMaterial> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WmMaterial::getId, id);
        WmMaterial one = getOne(queryWrapper);
        if (one == null) {
            return ResponseResult.errorResult(HttpCodeEnum.DATA_NOT_EXIST);
        }
        if (one.getIsCollection()) {
            one.setIsCollection(BaseEnum.IS_NOT_COLLECTION.getValue());
        } else one.setIsCollection(BaseEnum.IS_COLLECTION.getValue());
        updateById(one);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult delPic(Integer id) {
        //判断素材是否存在
        WmMaterial material = getById(id);
        if (material == null) {
            return ResponseResult.errorResult(HttpCodeEnum.DATA_NOT_EXIST);
        }
        //如果素材已经被文章使用,不可以被删除
        LambdaQueryWrapper<WmNewsMaterial> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WmNewsMaterial::getMaterialId, id);
        List<WmNewsMaterial> newsMaterial = materialService.list(queryWrapper);
        if (newsMaterial.size() == 0 || newsMaterial == null) {
            return ResponseResult.errorResult(HttpCodeEnum.CAN_NOT_DEL);
        }
        //删除minio中的对应图片
        String[] result = material.getUrl()
                .split("/");
        try {
            minioService.remove(Paths.get(result[result.length - 1]));
        } catch (MinioException e) {
            throw new RuntimeException(e);
        }

        remove(new LambdaQueryWrapper<WmMaterial>().eq(WmMaterial::getId, id)
                .eq(WmMaterial::getUserId, BaseContext.getThreadId()));
        return ResponseResult.okResult();
    }

    /**
     * 素材库分页查询
     *
     * @param wemediaDto
     * @return
     */
    @Override
    public PageResponseResult listService(PageWemediaDto wemediaDto) {
        //健壮性判断
        wemediaDto.checkParam();
        IPage<WmMaterial> page = new Page<>(wemediaDto.getPage(), wemediaDto.getSize());
        LambdaQueryWrapper<WmMaterial> queryWrapper = new LambdaQueryWrapper<>();
        //构造条件
        queryWrapper.eq(WmMaterial::getUserId, BaseContext.getThreadId());
        if (wemediaDto.getIsCollection() == 1) {
            queryWrapper.eq(WmMaterial::getIsCollection, BaseEnum.IS_COLLECTION.getValue());
        }
        queryWrapper.orderByDesc(WmMaterial::getCreatedTime);
        IPage<WmMaterial> result = page(page, queryWrapper);
        //查询结果
        long total = result.getTotal();
        List<WmMaterial> records = result.getRecords();
        return PageResponseResult.okResult(wemediaDto.getPage(), wemediaDto.getSize(), total, records);
    }

    /**
     * 上传图片到素材库
     *
     * @param multipartFile
     * @return
     */
    @Override
    public ResponseResult load(MultipartFile multipartFile) {
        if (multipartFile == null) {
            return ResponseResult.errorResult(HttpCodeEnum.DATA_NOT_EXIST);
        }
        //获取文件名
        String originalFilename = multipartFile.getOriginalFilename();
        String extensionName = FilenameUtils.getExtension(originalFilename);
        //存到minio中的路径和文件名
        String name = UUID.randomUUID() + "." + extensionName;
        Path source = Paths.get(name);
        //上传
        try {
            minioService.upload(source, multipartFile.getInputStream(), getContentType(extensionName));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //生成访问地址
        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setCreatedTime(new Date());
        wmMaterial.setUserId(BaseContext.getThreadId());
        wmMaterial.setUrl(url + "/" + bucket + "/" + name);
        wmMaterial.setIsCollection(false);
        wmMaterial.setType(BaseEnum.FILE_PIC.getValue());
        save(wmMaterial);
        return ResponseResult.okResult(wmMaterial);
    }

    /**
     * 获取文件后缀名对应的类型
     *
     * @param extensionName
     * @return
     */
    private static ContentType getContentType(String extensionName) {
        Map<String, ContentType> map = new HashMap<>();
        map.put("jpg", ContentType.IMAGE_JPEG);
        map.put("JPG", ContentType.IMAGE_JPEG);
        map.put("jpeg", ContentType.IMAGE_JPEG);
        map.put("JPEG", ContentType.IMAGE_JPEG);
        map.put("png", ContentType.IMAGE_PNG);
        map.put("PNG", ContentType.IMAGE_PNG);
        map.put("bmp", ContentType.IMAGE_BMP);
        map.put("BMP", ContentType.IMAGE_BMP);
        return map.get(extensionName);
    }
}
