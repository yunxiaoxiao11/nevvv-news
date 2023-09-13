package com.nevvv.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nevvv.model.common.user.dto.LoginDto;
import com.nevvv.model.common.dtos.ResponseResult;
import com.nevvv.model.common.enums.HttpCodeEnum;
import com.nevvv.model.common.user.pojo.User;
import com.nevvv.user.Mapper.UserMapper;
import com.nevvv.user.service.UserService;
import com.nevvv.utils.common.AppJwtUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserserviceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public ResponseResult login(LoginDto loginDto) {
        //判断登录身份
        if (StringUtils.isEmpty(loginDto.getPhone()) || StringUtils.isEmpty(loginDto.getPassword())) {
            //游客身份
            //生成token
            String token = AppJwtUtil.getToken(0L);
            Map<String, String> map = new HashMap<>();
            map.put("token", token);
            return ResponseResult.okResult(map);
        } else {
            //用户身份登录
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, loginDto.getPhone());
            queryWrapper.eq(User::getStatus, 1);//正常状态的用户
            User user = getOne(queryWrapper);
            if (user == null) {
                //用户不存在
                return ResponseResult.errorResult(HttpCodeEnum.AP_USER_DATA_NOT_EXIST);
            } else {
                //用户存在则校验密码
                String salt = user.getSalt();
                String verify = loginDto.getPassword() + salt;
                String password = DigestUtils.md5DigestAsHex(verify.getBytes());
                if (password.equals(user.getPassword())) {
                    //密码正确,生成token
                    String token = AppJwtUtil.getToken(user.getId());
                    redisTemplate.opsForValue()
                            .set("token:" + user.getId(), token, Duration.ofMinutes(30));
                    Map map = new HashMap();
                    map.put("token", token);
                    user.setPassword(null);
                    user.setSalt(null);
                    map.put("user", user);
                    return ResponseResult.okResult(map);
                } else {
                    //密码错误
                    return ResponseResult.errorResult(HttpCodeEnum.LOGIN_PASSWORD_ERROR);
                }
            }
        }
    }
}