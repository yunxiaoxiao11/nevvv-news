package com.nevvv.model.common.wemedia.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 自媒体图文引用素材信息表
 * </p>
 *
 * @author author
 * @since 2023-06-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wm_news_material")
public class WmNewsMaterial implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 素材ID
     */
    private Long materialId;

    /**
     * 图文ID
     */
    private Long newsId;

    /**
     * 引用类型
            0 内容引用
            1 主图引用
     */
    private Integer type;

    /**
     * 引用排序
     */
    private Boolean ord;


}
