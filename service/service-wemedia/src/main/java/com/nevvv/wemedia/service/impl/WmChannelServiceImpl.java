package com.nevvv.wemedia.service.impl;

import com.nevvv.model.common.dtos.ResponseResult;
import com.nevvv.model.common.wemedia.pojo.WmChannel;
import com.nevvv.wemedia.mapper.WmChannelMapper;
import com.nevvv.wemedia.service.WmChannelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 频道信息表 服务实现类
 * </p>
 *
 * @author author
 * @since 2023-06-03
 */
@Service
public class WmChannelServiceImpl extends ServiceImpl<WmChannelMapper, WmChannel> implements WmChannelService {

    @Override
    public ResponseResult channels() {
        List<WmChannel> list = list();
        return ResponseResult.okResult(list);
    }
}
