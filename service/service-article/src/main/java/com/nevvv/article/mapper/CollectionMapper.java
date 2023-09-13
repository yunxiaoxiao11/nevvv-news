package com.nevvv.article.mapper;

import com.nevvv.model.common.article.pojo.Collection;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface CollectionMapper {
    /**
     * collection表中添加收藏数据
     * @param collection
     */
    @Insert("insert into ap_collection ( article_id, type, collection_time ) " +
            "VALUE (#{articleId},#{type},#{collectionTime})")
    void save(Collection collection);

    /**
     * 取消收藏
     * @param articleId
     * @param threadId
     */
    @Delete("delete from ap_collection where article_id=#{articleId} and entry_id=#{threadId}")
    void remove(Long articleId, Long threadId);

    /**
     * 行为回显需要调用的接口
     * @param userId
     * @param articleId
     * @return
     */
    @Select("select * from ap_collection where entry_id=#{userId} and article_id=#{articleId}")
    Collection find(String userId, String articleId);
}
