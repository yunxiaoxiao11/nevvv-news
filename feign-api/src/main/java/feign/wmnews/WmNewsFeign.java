package feign.wmnews;

import com.nevvv.model.common.dtos.ResponseResult;
import com.nevvv.model.common.wemedia.pojo.WmNews;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("news-wemedia")
public interface WmNewsFeign {
    /**
     * wmnew的发布接口
     *
     * @param news
     * @return
     */
    @PostMapping("/api/v1/news/publish")
    ResponseResult publish(@RequestBody WmNews news);

    /**
     * wmnew查找审核通过未发布的文章
     *
     * @return
     */
    @GetMapping("/api/v1/news/getEnablePubArticle")
    ResponseResult<List<WmNews>> getEnablePubArticle();

    /**
     * 根据newsId查找news
     *
     * @param id
     * @return
     */
    @GetMapping("/api/v1/news/feign_find/{id}")
    ResponseResult<WmNews> feignFindById(@PathVariable Integer id);
}
