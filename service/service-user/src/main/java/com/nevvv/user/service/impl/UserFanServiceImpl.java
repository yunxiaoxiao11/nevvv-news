package com.nevvv.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nevvv.common.constant.BaseEnum;
import com.nevvv.model.common.article.dto.ArticleDocDto;
import com.nevvv.model.common.dtos.ResponseResult;
import com.nevvv.model.common.enums.HttpCodeEnum;
import com.nevvv.model.common.user.pojo.UserFan;
import com.nevvv.model.common.user.pojo.UserFollow;
import com.nevvv.user.Mapper.UserFanMapper;
import com.nevvv.user.service.UserFanService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nevvv.user.service.UserFollowService;
import com.nevvv.user.service.UserService;
import com.nevvv.utils.shareData.BaseContext;
import feign.article.ArticleFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * <p>
 * APP用户粉丝信息表 服务实现类
 * </p>
 *
 * @author yuv
 * @since 2023-06-14
 */
@Service
@Slf4j
public class UserFanServiceImpl extends ServiceImpl<UserFanMapper, UserFan> implements UserFanService {
    @Autowired
    private UserFollowService followService;
    @Autowired
    private UserService userService;
    @Autowired
    private ArticleFeign articleFeign;

    @Override
    public ResponseResult follow(Map<String, String> map) {
        Long articleId = Long.valueOf(map.get("articleId"));
        Integer authorId = Integer.valueOf(map.get("authorId"));
        Short operation = Short.valueOf(map.get("operation"));
        Integer userId = Integer.valueOf(String.valueOf(BaseContext.getThreadId()));
        if (operation == 0) {
            log.info("点击关注,保存作者表粉丝关注信息...");
            UserFan userFan = new UserFan();
            userFan.setUserId(authorId);
            userFan.setFansId(userId);
            userFan.setFansName(userService.getById(userId)
                    .getName());
            userFan.setLevel(BaseEnum.FAN_LEVEL1.getValue());
            userFan.setCreatedTime(new Date());
            userFan.setIsDisplay(BaseEnum.IS_DISPLAY.getValue());
            userFan.setIsShieldComment(BaseEnum.IS_DISPLAY.getValue());
            userFan.setIsShieldComment(BaseEnum.IS_DISPLAY.getValue());
            log.info("开始保存...");
            save(userFan);
            log.info("保存结束...");
            log.info("点击关注,保存登录用户关注者表关注信息...");
            UserFollow userFollow = new UserFollow();
            userFollow.setUserId(userId);
            userFollow.setFollowId(authorId);
            log.info("远程调用article服务获取其中的作者名称...");
            ResponseResult<ArticleDocDto> result = articleFeign.getById(articleId);
            if (result != null && result.getCode()
                    .equals(HttpCodeEnum.SUCCESS.getCode())) {
                String authorName = result.getData()
                        .getAuthorName();
                userFollow.setFollowName(authorName);
            }
            userFollow.setLevel(BaseEnum.follow_level1.getValue());
            userFollow.setIsNotice(BaseEnum.IS_NOTICE.getValue());
            userFollow.setCreatedTime(new Date());
            log.info("开始保存...");
            followService.save(userFollow);
            log.info("保存结束...");
        } else {
            log.info("取消关注,删除作者粉丝关注信息...");
            LambdaQueryWrapper<UserFan> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserFan::getUserId, authorId);
            queryWrapper.eq(UserFan::getFansId, userId);
            log.info("开始删除...");
            remove(queryWrapper);
            log.info("删除结束...");
            log.info("取消关注,删除用户和关注者关注信息...");
            LambdaQueryWrapper<UserFollow> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(UserFollow::getFollowId, authorId);
            queryWrapper1.eq(UserFollow::getUserId, userId);
            log.info("开始删除...");
            followService.remove(queryWrapper1);
            log.info("删除结束...");
        }
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getReviewInfo(Map<String, String> map) {
        Integer userId = Integer.valueOf(map.get("userId"));
        Integer authorId = Integer.valueOf(map.get("authorId"));
        UserFollow one= followService.getOne(new LambdaQueryWrapper<UserFollow>().eq(UserFollow::getUserId, userId)
                .eq(UserFollow::getFollowId, authorId));
        if (one != null) {
            return ResponseResult.okResult();
        } else {
            return ResponseResult.errorResult(HttpCodeEnum.DATA_NOT_EXIST);
        }
    }
}
