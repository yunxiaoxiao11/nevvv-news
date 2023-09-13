package com.nevvv.search.service;

import com.nevvv.model.common.dtos.ResponseResult;
import com.nevvv.model.common.search.dto.PageSearchUserDto;

public interface SearchService {

    ResponseResult search(PageSearchUserDto pageSearchUserDto);
}
