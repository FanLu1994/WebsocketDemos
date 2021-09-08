package com.websocket.demo;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: websocket_java_demo
 * @description: websocket处理器
 * @author: fanlu
 * @create: 2021-09-07 08:53
 **/
@ServerEndpoint("/chat")
public class MyWebSocketHandler extends BinaryWebSocketHandler {

    private MsgHandler msgHandler = new MsgHandler();


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        msgHandler.OnConnect(session);
        super.afterConnectionEstablished(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        System.out.println(session.getId()+"||"+message.getPayload());
        super.handleMessage(session, message);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message){

        try{
            System.out.println(message.getPayload());
            Message msg = JSONObject.parseObject(message.getPayload(),Message.class);
            switch (msg.msg_key){
                case "Connect":
                    this.msgHandler.OnConnect(session);
                    break;
                case "ChatMessage":
                    this.msgHandler.OnChatMessage(session,msg.msg_data);
                    break;
                case "Leave":
                    this.msgHandler.OnLeave(session,msg.msg_data.toString());
                    break;
                case "SetName":
                    this.msgHandler.SetUsername(session,msg.msg_data.toString());
                    break;
                default:
                    this.msgHandler.OnError(session,"msgkey doesn't exist");
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        // 这里调用会导致连接关闭
        //super.handleTextMessage(session, message);
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        super.handlePongMessage(session, message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println(session.getId()+"is closed");
        super.afterConnectionClosed(session, status);
    }

}
