package com.nevvv.model.common.wemedia.dto;

import com.nevvv.model.common.dtos.PageRequestDto;
import lombok.Data;

import java.io.Serializable;

@Data
public class PageWemediaDto extends PageRequestDto implements Serializable {
    private Short isCollection;
}
