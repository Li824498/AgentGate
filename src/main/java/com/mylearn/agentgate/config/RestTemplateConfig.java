package com.mylearn.agentgate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.HttpURLConnection;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory() {
            @Override
            protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
                // 设置 SOCKS5 代理
                System.setProperty("socksProxyHost", "127.0.0.1");
                System.setProperty("socksProxyPort", "7890");
                super.prepareConnection(connection, httpMethod);
            }
        };
        return new RestTemplate(requestFactory);
    }
}
