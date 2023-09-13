package com.nevvv.behavior.service;

import com.nevvv.model.common.dtos.ResponseResult;

import java.util.Map;

public interface BehaviorService {
    ResponseResult like(Map<String, String> map);

    ResponseResult read(Map<String, Long> map);
}
