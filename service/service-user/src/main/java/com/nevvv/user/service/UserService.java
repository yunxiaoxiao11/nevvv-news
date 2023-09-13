package com.nevvv.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nevvv.model.common.user.dto.LoginDto;
import com.nevvv.model.common.dtos.ResponseResult;
import com.nevvv.model.common.user.pojo.User;

public interface UserService extends IService<User>{
    ResponseResult login(LoginDto loginDto);
}
