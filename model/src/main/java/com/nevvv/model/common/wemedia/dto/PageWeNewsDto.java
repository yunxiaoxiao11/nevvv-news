package com.nevvv.model.common.wemedia.dto;

import com.nevvv.model.common.dtos.PageRequestDto;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class PageWeNewsDto extends PageRequestDto implements Serializable {
    private String keyword;
    private Integer status;
    private Integer channelId;
    private LocalDate beginPubdate;
    private LocalDate endPubdate;
}
