package feign.user;

import com.nevvv.model.common.dtos.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient("news-user")
public interface UserFeign {
    @PostMapping("/api/v1/user/getReviewInfo")
    ResponseResult getReviewInfo(@RequestBody Map<String, String> map);
}
