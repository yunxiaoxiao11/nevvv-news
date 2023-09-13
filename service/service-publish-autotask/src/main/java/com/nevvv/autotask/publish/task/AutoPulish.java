package com.nevvv.autotask.publish.task;

import com.nevvv.model.common.wemedia.pojo.WmNews;
import feign.wmnews.WmNewsFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

//@Component
public class AutoPulish {
    @Autowired
    private WmNewsFeign newsFeign;

    public List<WmNews> getFromDb() {
        return newsFeign.getEnablePubArticle()
                .getData();
    }

    @Scheduled(cron = "0 0/2 * * * ?")
    public void publish() {
        for (WmNews wmNews : getFromDb()) {
            newsFeign.publish(wmNews);
        }
    }
}