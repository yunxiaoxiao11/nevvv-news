package com.nevvv.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nevvv.model.common.dtos.ResponseResult;
import com.nevvv.model.common.enums.HttpCodeEnum;
import com.nevvv.model.common.wemedia.dto.WemediaDto;
import com.nevvv.model.common.wemedia.pojo.WmUser;
import com.nevvv.utils.common.AppJwtUtil;
import com.nevvv.wemedia.mapper.WmUserMapper;
import com.nevvv.wemedia.service.WmUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 自媒体用户信息表 服务实现类
 * </p>
 *
 * @author
 * @since 2023-06-02
 */
@Service
public class WmUserServiceImpl extends ServiceImpl<WmUserMapper, WmUser> implements WmUserService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public ResponseResult login(WemediaDto wemediaDto) {
        LambdaQueryWrapper<WmUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WmUser::getName, wemediaDto.getName());
        queryWrapper.eq(WmUser::getStatus, 1);
        WmUser wmUser = getOne(queryWrapper);
        //判断用户名是否存在
        if (wmUser == null) {
            return ResponseResult.errorResult(HttpCodeEnum.AP_USER_DATA_NOT_EXIST);
        }
        //校验密码
        String verify = wmUser.getSalt() + wemediaDto.getPassword();
        String password = DigestUtils.md5DigestAsHex(verify.getBytes());
        if (password.equals(wmUser.getPassword())) {
            //密码正确,生成token
            String token = AppJwtUtil.getToken(Long.valueOf(wmUser.getId()));
            //添加token到redis
            redisTemplate.opsForValue()
                    .set("token:" + wmUser.getId(), token, Duration.ofMinutes(30));
            //封装响应前端的数据
            Map map = new HashMap();
            map.put("token", token);
            wmUser.setPassword(null);
            wmUser.setSalt(null);
            map.put("user", wmUser);
            return ResponseResult.okResult(map);
        } else {
            //密码错误
            return ResponseResult.errorResult(HttpCodeEnum.LOGIN_PASSWORD_ERROR);
        }
    }
}