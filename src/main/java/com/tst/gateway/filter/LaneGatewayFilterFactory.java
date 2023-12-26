package com.tst.gateway.filter;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriTemplate;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.springframework.cloud.gateway.support.GatewayToStringStyler.filterToStringCreator;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.*;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

public class LaneGatewayFilterFactory extends AbstractGatewayFilterFactory<LaneGatewayFilterFactory.Config> {

    public static final String HEADER_KEY = "header";

    private static final Log log = LogFactory.getLog(LaneGatewayFilterFactory.class);

    public LaneGatewayFilterFactory() {
        super(LaneGatewayFilterFactory.Config.class);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList(HEADER_KEY);
    }

    @Override
    public GatewayFilter apply(LaneGatewayFilterFactory.Config config) {
        return new GatewayFilter() {
            final UriTemplate uriTemplate = new UriTemplate(config.header);

            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                String headerName = exchange.getRequest().getQueryParams().getFirst("headerName");
                if (StringUtils.isNotBlank(headerName)) {
                    if (config.getHeader().equals(headerName)) {
                        return chain.filter(exchange);
                    } else {
                        exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
                        return exchange.getResponse().setComplete();
                    }
                }
                return chain.filter(exchange);
            }

            @Override
            public String toString() {
                return filterToStringCreator(LaneGatewayFilterFactory.this).append("header", config.getHeader())
                        .toString();
            }
        };
    }

    @Data
    public static class Config {

        private String header;

    }
}
