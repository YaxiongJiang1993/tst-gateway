package com.tst.gateway.config;

import com.tst.gateway.lb.LaneRoundRobinLoadBalancer;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * VersionLoadBalancerConfiguration 配置类不能添加@Configuration注解。
 *
 * 在网关启动类使用注解@LoadBalancerClient指定哪些服务使用自定义负载均衡算法
 * 通过@LoadBalancerClient(value = "auth-service", configuration = VersionLoadBalancerConfiguration.class)，对于auth-service启用自定义负载均衡算法；
 * 或通过@LoadBalancerClients(defaultConfiguration = VersionLoadBalancerConfiguration.class)为所有服务启用自定义负载均衡算法。
 */

public class LoadBalancerClientConfiguration {

    @Bean
    public ReactorLoadBalancer<ServiceInstance> reactorServiceInstanceLoadBalancer(Environment environment,
                                                                                   LoadBalancerClientFactory loadBalancerClientFactory) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        return new LaneRoundRobinLoadBalancer(
                loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class), name);
    }
}
