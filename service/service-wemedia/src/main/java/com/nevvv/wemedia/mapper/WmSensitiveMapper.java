package com.nevvv.wemedia.mapper;

import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface WmSensitiveMapper {
    @Select("select sensitives from wm_sensitive")
    List<String> wordList();
}
