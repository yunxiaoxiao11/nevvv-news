package com.nevvv.model.common.article.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;

/**
 * <p>
 * APP已发布文章配置表
 * </p>
 *
 * @author
 * @since 2023-05-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ap_article_config")
public class ArticleConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 文章ID
     */
    @Column(name = "article_id")
    private Long articleId;

    /**
     * 是否可评论
     */
    @Column(name = "is_comment")
    private Boolean isComment;

    /**
     * 是否转发
     */
    @Column(name = "is_forward")
    private Boolean isForward;

    /**
     * 是否下架
     */
    @Column(name = "is_down")
    private Boolean isDown;

    /**
     * 是否已删除
     */
    @Column(name = "is_delete")
    private Boolean isDelete;


}
