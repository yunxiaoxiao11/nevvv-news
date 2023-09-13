package com.nevvv.model.common.wemedia.dto;

import com.nevvv.model.common.wemedia.pojo.WmNews;
import lombok.Data;

@Data
public class WeNews2ArticleDto extends WmNews {
    private String authorName;
    private String channelName;
    private String staticUrl;
}
