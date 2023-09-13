package com.nevvv.search.service;

import com.nevvv.model.common.dtos.ResponseResult;
import com.nevvv.model.common.search.pojo.Associate;

import java.util.List;
import java.util.Map;

public interface AssociateService {
    ResponseResult<List<Associate>> search(Map<String, String> map);
}
