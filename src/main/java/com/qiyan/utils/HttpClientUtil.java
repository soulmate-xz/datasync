package com.qiyan.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class HttpClientUtil {

    public static HttpResponse<String> post(String url, Map<String, Object> params) {
        // 构建 JSON 请求体
        String jsonBody;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            jsonBody = objectMapper.writeValueAsString(params);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        // 创建 HttpClient
        HttpClient client = HttpClient.newHttpClient();

        // 构建 HttpRequest
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json") // 设置请求头
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8)) // 设置请求体
                .build();
        try {
            // 发送请求并获取响应
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println("消息发送异常: " + e);
        }
        return null;
    }

}
