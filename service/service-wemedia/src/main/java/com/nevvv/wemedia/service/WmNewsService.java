package com.nevvv.wemedia.service;

import com.nevvv.model.common.dtos.ResponseResult;
import com.nevvv.model.common.wemedia.dto.PageWeNewsDto;
import com.nevvv.model.common.wemedia.dto.WeDownUpDto;
import com.nevvv.model.common.wemedia.dto.WeNewsDto;
import com.nevvv.model.common.wemedia.pojo.WmNews;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 自媒体图文内容信息表 服务类
 * </p>
 *
 * @author author
 * @since 2023-06-03
 */
public interface WmNewsService extends IService<WmNews> {

    /**
     * 待条件的分页查询自媒体文章列表
     *
     * @param pageWeNewsDto
     * @return
     */
    ResponseResult listService(PageWeNewsDto pageWeNewsDto);

    /**
     * 发布或修改文章
     *
     * @param weNewsDto
     * @return
     */
    ResponseResult submit(WeNewsDto weNewsDto) throws Exception;

    /**
     * 删除文章j
     *
     * @param id
     * @return
     */
    ResponseResult delById(Integer id);

    /**
     * 查找审核通过待发布的文章
     * @return
     */
    ResponseResult<List<WmNews>> getArticle();

    ResponseResult publish(WmNews news) throws Exception;

    /**
     * 根据id查找符合发布条件的文章
     * @param id
     * @return
     */
    WmNews findById(Integer id);

    ResponseResult downOrUp(WeDownUpDto downUpDto);

}
