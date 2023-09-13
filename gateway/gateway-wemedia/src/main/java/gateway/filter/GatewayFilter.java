package gateway.filter;

import com.nevvv.utils.common.AppJwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Component
@Order(1)
@Slf4j
public class GatewayFilter implements GlobalFilter {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String requestUrl = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR)
                .toString();
        String[] split = requestUrl.split("/");
        log.info("前端访问的路径为:{}", requestUrl);
        //请求路径不是登录拒绝访问
        if (split[split.length - 2].equals("login")) {
            //登录请求放行
            return chain.filter(exchange);
        }
        //不是登录
        List<String> list = request.getHeaders()
                .get("token");
        String s;
        try {
            s = list.get(0);
        } catch (Exception e) {
            //token为空,响应401
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        Claims claimsBody;
        try {
            //解析token
            claimsBody = AppJwtUtil.getClaimsBody(s);
            Long id = claimsBody.get("id", Long.class);
            request.mutate()
                    .header("loginId", String.valueOf(id));
            String tokenStr = "token:" + claimsBody.get("id");
            String redisToken = redisTemplate.opsForValue()
                    .get(tokenStr);
            if (StringUtils.isEmpty(redisToken)) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
            if (redisToken.equals(s)) {
                //令牌续命
                redisTemplate.expire(tokenStr, Duration.ofMinutes(30));
                return chain.filter(exchange);
            } else {
                //重复登陆
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
        } catch (Exception e) {
            //出现异常说明校验不合法,响应401
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
    }
}