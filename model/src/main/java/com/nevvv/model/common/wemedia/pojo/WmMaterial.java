package com.nevvv.model.common.wemedia.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 自媒体图文素材信息表
 * </p>
 *
 * @author author
 * @since 2023-06-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wm_material")
public class WmMaterial implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 自媒体用户ID
     */
    private Long userId;

    /**
     * 图片地址
     */
    private String url;

    /**
     * 素材类型0图片
     * 1视频
     */
    private Integer type;

    /**
     * 是否收藏
     */
    private Boolean isCollection;

    /**
     * 创建时间
     */
    private Date createdTime;


}
