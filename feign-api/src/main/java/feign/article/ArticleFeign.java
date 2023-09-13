package feign.article;

import com.nevvv.model.common.article.dto.ArticleDocDto;
import com.nevvv.model.common.dtos.ResponseResult;
import com.nevvv.model.common.wemedia.dto.WeNews2ArticleDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("news-article")
public interface ArticleFeign {
    /**
     * 远程调用文章微服务保存审核通过的文章
     *
     * @param dto
     * @return
     */
    @PostMapping("/api/v1/article/save")
    ResponseResult<Long> save(@RequestBody WeNews2ArticleDto dto);

    /**
     * 查询所有文章的
     * 该接口是一个内部接口，被search服务远程调用的。
     */
    @GetMapping("/api/v1/article/findAll")
    ResponseResult<List<ArticleDocDto>> findAll();
    /**
     * 搜索服务远程调用接口,根据id查找文章信息
     * @param id
     * @return
     */
    @GetMapping("/api/v1/article/getById/{id}")
    ResponseResult<ArticleDocDto> getById(@PathVariable Long id);
}
