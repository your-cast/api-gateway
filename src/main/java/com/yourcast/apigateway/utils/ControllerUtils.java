package com.yourcast.apigateway.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@UtilityClass
public class ControllerUtils {
    public static String getUUID() {
        return UUID.randomUUID().toString();
    }
}
