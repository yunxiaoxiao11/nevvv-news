package com.nevvv.user.service;

import com.nevvv.model.common.dtos.ResponseResult;
import com.nevvv.model.common.user.pojo.UserFan;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * APP用户粉丝信息表 服务类
 * </p>
 *
 * @author yuv
 * @since 2023-06-14
 */
public interface UserFanService extends IService<UserFan> {

    ResponseResult follow(Map<String, String> map);

    ResponseResult getReviewInfo(Map<String, String> map);
}
