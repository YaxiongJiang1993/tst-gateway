package com.tst.gateway.predicate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.GatewayPredicate;
import org.springframework.cloud.gateway.handler.predicate.QueryRoutePredicateFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;

import javax.validation.constraints.NotEmpty;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * 自定义断言：
 *  1. bean
 *  2. 命名规则 xxxRoutePredicateFactory xxx是你配置的规则名,必须以RoutePredicateFactory结尾
 *  3. 必须继承 AbstractRoutePredicateFactory
 *  4. 实现静态内部类，用于接受数据 shortcutFieldOrder绑定
 */

@Slf4j
@Component
public class LaneRoutePredicateFactory extends AbstractRoutePredicateFactory<LaneRoutePredicateFactory.Config> {

    public static final String PARAM_KEY = "param";

    public static final String REGEXP_KEY = "regexp";

    public LaneRoutePredicateFactory() {
        super(Config.class);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList(PARAM_KEY, REGEXP_KEY);
    }

    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        return new GatewayPredicate() {
            @Override
            public boolean test(ServerWebExchange exchange) {
                if (!StringUtils.hasText(config.regexp)) {
                    // check existence of header
                    return exchange.getRequest().getQueryParams().containsKey(config.param);
                }

                List<String> values = exchange.getRequest().getQueryParams().get(config.param);
                if (values == null) {
                    return false;
                }
                for (String value : values) {
                    if (value != null && value.matches(config.regexp)) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public Object getConfig() {
                return config;
            }

            @Override
            public String toString() {
                return String.format("Query: param=%s regexp=%s", config.getParam(), config.getRegexp());
            }
        };
    }

    @Validated
    public static class Config {

        @NotEmpty
        private String param;

        private String regexp;

        public String getParam() {
            return param;
        }

        public Config setParam(String param) {
            this.param = param;
            return this;
        }

        public String getRegexp() {
            return regexp;
        }

        public Config setRegexp(String regexp) {
            this.regexp = regexp;
            return this;
        }

    }
}
