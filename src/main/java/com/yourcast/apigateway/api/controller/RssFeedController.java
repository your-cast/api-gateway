package com.yourcast.apigateway.api.controller;

import com.yourcast.apigateway.model.dto.response.RssFeedResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import com.yourcast.apigateway.api.helper.ApiGatewayControllerHelper;
import com.yourcast.apigateway.api.model.GatewayApiResponse;
import com.yourcast.apigateway.model.enums.CommandType;
import com.yourcast.apigateway.utils.ControllerUtils;

import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "Feed Controller", description = "RSS feed generator for show")
@RequestMapping(value = "/api/v1/feed/{token}", produces = MediaType.APPLICATION_JSON_VALUE)
public class RssFeedController {
    private final ApiGatewayControllerHelper apiGatewayControllerHelper;

    @Operation(summary = "Get rss feed by token", description = "Get bank name by IBAN, schema for data response is BankResponse")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success response"),
            @ApiResponse(responseCode = "408", description = "Request Time Out"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")})
    @GetMapping
    public DeferredResult<ResponseEntity<GatewayApiResponse<RssFeedResponse>>> getRssFeed(@PathVariable("token") String token) {
        LOG.info("RssFeedController.getRssFeed: token( " + token + " )");
        String key = ControllerUtils.getUUID();

        DeferredResult<ResponseEntity<GatewayApiResponse<RssFeedResponse>>> result = apiGatewayControllerHelper.createResult("GetRssFeedByToken", key);

        apiGatewayControllerHelper.getExecutor().submit(() ->
                apiGatewayControllerHelper.getChannel().sendRequest(key,
                        CommandType.CMD_FEED_GET_LIST,
                        token.getBytes(StandardCharsets.UTF_8),
                        result,
                        null));

        return result;
    }
}
