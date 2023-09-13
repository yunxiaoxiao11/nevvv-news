package com.nevvv.article.service;

import com.nevvv.model.common.dtos.ResponseResult;

import java.util.Map;

public interface CollectionService {

    ResponseResult collection(Map<String, String> map);
}
