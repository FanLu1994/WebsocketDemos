package com.websocket.demo;

import com.alibaba.fastjson.JSON;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: websocket_java
 * @description: websocket消息封装
 * @author: fanlu
 * @create: 2021-09-06 21:08
 **/
public class Message{
    public String msg_key;
    public Object msg_data;

    public Message(String msg_key, Object msg_data) {
        this.msg_key = msg_key;
        this.msg_data = msg_data;
    }

    public static String NewMessage(String key, Object data){
//        HashMap<String,Object>  map = new HashMap<String,Object>();
//        map.put("msg_key",key);
//        map.put("msg_data",data);
        Message msg = new Message(key,data);
        return JSON.toJSONString(msg);
    }

}
