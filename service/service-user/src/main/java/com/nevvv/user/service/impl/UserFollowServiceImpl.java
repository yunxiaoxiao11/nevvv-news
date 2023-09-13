package com.nevvv.user.service.impl;

import com.nevvv.model.common.user.pojo.UserFollow;
import com.nevvv.user.Mapper.UserFollowMapper;
import com.nevvv.user.service.UserFollowService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * APP用户关注信息表 服务实现类
 * </p>
 *
 * @author yuv
 * @since 2023-06-14
 */
@Service
public class UserFollowServiceImpl extends ServiceImpl<UserFollowMapper, UserFollow> implements UserFollowService {

}
