package com.websocket.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.websocket.Session;

/**
 * @program: websocket_java
 * @description: websocket服务类
 * @author: fanlu
 * @create: 2021-09-05 22:22
 **/
public class MsgHandler {

    public List<WebSocketSession> userConns = new ArrayList<>();
    public List<String> usernames = new ArrayList<>();
    public int userCount = 0;



    /**
     * 连接事件:更新连接列表;更新人数;发送用户名表给新的连接
     */
    public synchronized void OnConnect(WebSocketSession session) throws IOException {
        this.userConns.add(session);
        this.userCount++;
        String userCountMsg = Message.NewMessage("UserCount",this.userCount);
        String usernameMsg = Message.NewMessage("UserNameList",this.usernames);

        session.sendMessage(new TextMessage(usernameMsg));
        for (WebSocketSession userConn : userConns) {
            userConn.sendMessage(new TextMessage(userCountMsg));
            userConn.sendMessage(new TextMessage(usernameMsg));
        }
    }

    /**
     * 离开房间事件
     * @param session
     */
    public void OnLeave(WebSocketSession session,String username) throws IOException {
        if(this.usernames.contains(username)){
            // 删除用户名
            this.usernames.remove(username);
            String leaveMsg = Message.NewMessage("LeaveRoom",username+"离开了房间");
            String usernameMsg = Message.NewMessage("UserNameList",this.usernames);
            for(WebSocketSession conn : this.userConns){
                conn.sendMessage(new TextMessage(leaveMsg));
                conn.sendMessage(new TextMessage(usernameMsg));
            }
        }

        this.userConns.remove(session);
        this.userCount--;
        String userCountMsg = Message.NewMessage("UserCount",this.userCount);
        for(WebSocketSession conn : this.userConns){
            conn.sendMessage(new TextMessage(userCountMsg));
        }
    }


    /**
     * 设置用户名
     * @param session
     * @param name
     */
    public synchronized void SetUsername(WebSocketSession session,String name) throws IOException {
//        System.out.println("试试");
        if(this.usernames.contains(name)){
            String msg = Message.NewMessage("SetUsername",false);
            session.sendMessage(new TextMessage(msg));
        }else {
            String msg = Message.NewMessage("SetUsername",true);
            session.sendMessage(new TextMessage(msg));

            this.usernames.add(name);
            String enterMsg = Message.NewMessage("EnterRoom",name+"进入了房间");
            String usernameMsg = Message.NewMessage("UserNameList",this.usernames);

            for(WebSocketSession conn : this.userConns){
                conn.sendMessage(new TextMessage(enterMsg));
                conn.sendMessage(new TextMessage(usernameMsg));
            }

        }
    }

    /**
     * 转发聊天消息
     * @param session
     * @param data
     */
    public void OnChatMessage(WebSocketSession session,Object data) throws IOException {
        String msg = Message.NewMessage("ChatMessage",data);
        for(WebSocketSession conn : this.userConns){
            conn.sendMessage(new TextMessage(msg));
        }
    }

    /**
     * 处理错误
     * @param session
     * @param error
     */
    public void OnError(WebSocketSession session,String error) throws IOException {
        String msg = Message.NewMessage("Error",error);
        session.sendMessage(new TextMessage(msg));
    }

}
