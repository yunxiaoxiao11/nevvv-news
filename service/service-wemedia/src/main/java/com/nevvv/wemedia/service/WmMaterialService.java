package com.nevvv.wemedia.service;

import com.nevvv.model.common.dtos.PageResponseResult;
import com.nevvv.model.common.dtos.ResponseResult;
import com.nevvv.model.common.wemedia.dto.PageWemediaDto;
import com.nevvv.model.common.wemedia.pojo.WmMaterial;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 自媒体图文素材信息表 服务类
 * </p>
 *
 * @author author
 * @since 2023-06-03
 */
public interface WmMaterialService extends IService<WmMaterial> {

    /**
     * 上传图片到素材库
     * @param multipartFile
     * @return
     */
    ResponseResult load(MultipartFile multipartFile);

    /**
     * 素材库分页查询
     * @param wemediaDto
     * @return
     */
    PageResponseResult listService(PageWemediaDto wemediaDto);

    /**
     * 素材收藏
     * @param id
     * @return
     */
    ResponseResult collect(Integer id);

    /**
     * 素材删除
     * @param id
     * @return
     */
    ResponseResult delPic(Integer id);
}
