package com.nevvv.search.service.impl;

import com.nevvv.model.common.dtos.ResponseResult;
import com.nevvv.model.common.search.pojo.Associate;
import com.nevvv.search.service.AssociateService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AssociateServiceImpl implements AssociateService {
    @Autowired
    private RestHighLevelClient highLevelClient;

    @Override
    public ResponseResult<List<Associate>> search(Map<String, String> map) {
        SearchRequest searchRequest = new SearchRequest("app_info_article");
        searchRequest.source()
                .suggest(new SuggestBuilder().addSuggestion("nameSuggestion", SuggestBuilders.completionSuggestion("labels")
                        .skipDuplicates(true)
                        .prefix(map.get("searchWords")).size(Integer.parseInt(map.get("pageSize")))));
        SearchResponse search;
        try {
            search = highLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("解析查询结果...");
        List<Associate> suggestList = new ArrayList<>();
        Suggest.Suggestion<? extends Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option>> suggestion = search.getSuggest()
                .getSuggestion("nameSuggestion");
        for (Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option> options : suggestion) {
            List<? extends Suggest.Suggestion.Entry.Option> options1 = options.getOptions();
            for (Suggest.Suggestion.Entry.Option option : options1) {
                Text text = option.getText();
                suggestList.add(new Associate(String.valueOf(text)));
            }
        }
        return ResponseResult.okResult(suggestList);
    }
}
