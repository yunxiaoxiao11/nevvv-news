package com.nevvv.wemedia.service;

import com.nevvv.model.common.dtos.ResponseResult;
import com.nevvv.model.common.wemedia.pojo.WmChannel;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 频道信息表 服务类
 * </p>
 *
 * @author author
 * @since 2023-06-03
 */
public interface WmChannelService extends IService<WmChannel> {

    ResponseResult channels();
}
