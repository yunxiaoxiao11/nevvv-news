import com.alibaba.fastjson.JSON;
import com.nevvv.model.common.article.dto.ArticleDocDto;
import com.nevvv.model.common.dtos.ResponseResult;
import com.nevvv.model.common.enums.HttpCodeEnum;
import com.nevvv.model.common.search.pojo.ArticleDoc;
import feign.article.ArticleFeign;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest(classes = com.nevvv.search.SearchApp.class)
public class TestImport {
    @Autowired
    private ArticleFeign articleFeign;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    public void importData() throws IOException {
        //远程调用文章微服务，获取所有文章信息
        ResponseResult<List<ArticleDocDto>> responseResult = articleFeign.findAll();
        if (responseResult.getCode() == HttpCodeEnum.SUCCESS.getCode()) {
            List<ArticleDocDto> list = responseResult.getData();
            for (ArticleDocDto articleDocDto : list) {
                System.out.println(articleDocDto);
                //将文章信息存入es
                ArticleDoc articleDoc = new ArticleDoc(articleDocDto);
                IndexRequest indexRequest = new IndexRequest("app_info_article");
                indexRequest.id(articleDocDto.getId());
                indexRequest.source(JSON.toJSONString(articleDoc), XContentType.JSON);
                restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            }
        }
    }
}