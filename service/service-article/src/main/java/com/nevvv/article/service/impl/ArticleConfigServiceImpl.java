package com.nevvv.article.service.impl;

import com.nevvv.article.mapper.ArticleConfigMapper;
import com.nevvv.article.service.ArticleConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nevvv.model.common.article.pojo.ArticleConfig;
import org.springframework.stereotype.Service;

/**
 * <p>
 * APP已发布文章配置表 服务实现类
 * </p>
 *
 * @author 
 * @since 2023-05-31
 */
@Service
public class ArticleConfigServiceImpl extends ServiceImpl<ArticleConfigMapper, ArticleConfig> implements ArticleConfigService {

}
