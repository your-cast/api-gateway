package ua.aval.lcyapigateway.routes;

import lombok.AllArgsConstructor;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ua.aval.lcyapigateway.config.RoutesProperties;

@Configuration
@AllArgsConstructor
public class RssFeedRoutes {
    private final RoutesProperties routesProperties;

    @Bean
    public RouteLocator rssFeedGatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("feed", p -> p
                        .path("/api/feed/**")
                        .and()
                        .method("GET")
                        .filters(rw -> rw.rewritePath("/api/feed/(?<token>.*)", "/api/v1/feed/${token}"))
                        .uri(routesProperties.getFeed()))
                .build();
    }
}
