package com.nevvv.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nevvv.model.common.dtos.ResponseResult;
import com.nevvv.model.common.wemedia.dto.WemediaDto;
import com.nevvv.model.common.wemedia.pojo.WmUser;

/**
 * <p>
 * 自媒体用户信息表 服务类
 * </p>
 *
 * @author
 * @since 2023-06-02
 */
public interface WmUserService extends IService<WmUser> {

    ResponseResult login(WemediaDto wemediaDto);
}
