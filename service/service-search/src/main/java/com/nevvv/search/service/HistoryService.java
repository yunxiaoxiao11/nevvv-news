package com.nevvv.search.service;

import com.nevvv.model.common.dtos.ResponseResult;
import com.nevvv.model.common.history.pojo.ApUserSearch;

import java.util.List;

public interface HistoryService {
    ResponseResult<List<ApUserSearch>> load();

    ResponseResult del(String id);
}
