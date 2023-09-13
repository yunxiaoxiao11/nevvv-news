package com.nevvv.article.service.impl;

import com.nevvv.article.mapper.ArticleContentMapper;
import com.nevvv.article.service.ArticleContentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nevvv.model.common.article.pojo.ArticleContent;
import org.springframework.stereotype.Service;

/**
 * <p>
 * APP已发布文章内容表 服务实现类
 * </p>
 *
 * @author
 * @since 2023-05-31
 */
@Service
public class ArticleContentServiceImpl extends ServiceImpl<ArticleContentMapper, ArticleContent> implements ArticleContentService {

}
