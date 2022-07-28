package ua.aval.lcyapigateway.config;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ConfigurationProperties("services")
@Validated
public class RoutesProperties {
    @NotBlank
    @URL
    private String feed;
}
