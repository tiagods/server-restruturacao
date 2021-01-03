package com.tiagods.prolink.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.UUID;

@Configuration
public class RequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        HttpHeaders headers = httpRequest.getHeaders();

        String cid = headers.containsKey("cid") ? headers.getFirst("cid") : null;

        if(StringUtils.isEmpty(cid)) {
            cid = UUID.randomUUID().toString();
        }

        httpRequest.getHeaders().add("cid", cid);

        ClientHttpResponse response = execution.execute(httpRequest, body);

        response.getHeaders().add("cid", cid);
        return response;
    }
}
