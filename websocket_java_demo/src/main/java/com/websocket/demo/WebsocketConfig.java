package com.websocket.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.net.http.WebSocket;

/**
 * @program: websocket_java_demo
 * @description: websocket配置
 * @author: fanlu
 * @create: 2021-09-07 08:51
 **/
@EnableWebSocket
@Configuration
public class WebsocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(myWebSocketHandler(),"/chat").setAllowedOrigins("*");
//        webSocketHandlerRegistry.addHandler(myWebSocketHandler(),"/sockjs").setAllowedOrigins("*").withSockJS();
    }


    @Bean
    MyWebSocketHandler myWebSocketHandler(){
        return new MyWebSocketHandler();
    }
}
