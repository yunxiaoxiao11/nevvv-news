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
 * 自媒体图文内容信息表
 * </p>
 *
 * @author author
 * @since 2023-06-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wm_news")
public class WmNews implements Serializable {

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
     * 标题
     */
    private String title;
    /**
     * 图文内容
     */
    private String content;
    /**
     * 文章布局
            0 无图文章
            1 单图文章
            3 多图文章
     */
    private Integer type;
    /**
     * 图文频道ID
     */
    private Integer channelId;
    private String labels;
    /**
     * 创建时间
     */
    private Date createdTime;
    /**
     * 提交时间
     */
    private Date submitedTime;
    /**
     * 当前状态
            0 草稿
            1 提交（待审核）
            2 审核失败
            3 人工审核
            4 人工审核通过
            8 审核通过（待发布）
            9 已发布
     */
    private Integer status;
    /**
     * 定时发布时间，不定时则为空
     */
    private Date publishTime;
    /**
     * 拒绝理由
     */
    private String reason;

    /**
     * 发布库文章ID
     */
    private Long articleId;
    /**
     * //图片用逗号分隔
     */
    private String images;
    private Boolean enable;
}
