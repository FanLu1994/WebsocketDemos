package server

import (
	"encoding/json"
	"fmt"
	"net/http"

	"github.com/gorilla/websocket"
)

var upGrader = websocket.Upgrader{
	CheckOrigin: func(r *http.Request) bool {
		return true
	},
}

var (
	userCount    = 0
	userNameList = make([]string, 0)
	userConnList = make([]*websocket.Conn, 0)
	msgMap       = make(map[string]func(ws *websocket.Conn, data interface{}), 0)
)

// 初始化消息映射
func InitMsgMap() {
	msgMap["Connect"] = OnConnect
	msgMap["ChatMessage"] = OnChatMessage
	msgMap["Leave"] = OnLeave
	msgMap["SetName"] = OnSetName
	msgMap["Error"] = OnError
}

// 消息处理器
func MessageHandler(w http.ResponseWriter, r *http.Request) {
	//升级get请求为webSocket协议
	ws, err := upGrader.Upgrade(w, r, nil)
	if err != nil {
		return
	}

	// 触发连接事件
	msgMap["Connect"](ws, "")

	defer ws.Close()
	for {
		//读取ws中的数据
		_, message, err := ws.ReadMessage()
		// fmt.Println(message)
		msg := new(Message)
		err = json.Unmarshal(message, &msg)
		// fmt.Println(msg)
		if err != nil {
			break
		}

		if _, ok := msgMap[msg.MsgKey]; ok {
			// 处理消息
			msgMap[msg.MsgKey](ws, msg.MsgData)
		} else {
			message, _ = json.Marshal(Message{
				MsgKey:  "Error",
				MsgData: "msgkey doesn't exist",
			})
			msgMap["Error"](ws, message)
		}

	}
}

// 连接事件
func OnConnect(ws *websocket.Conn, data interface{}) {
	// 通知人数加一
	userCount++
	fmt.Println(userCount)
	msg, _ := json.Marshal(Message{
		MsgKey:  "UserCount",
		MsgData: userCount,
	})
	msg2, _ := json.Marshal(Message{
		MsgKey:  "UserNameList",
		MsgData: userNameList,
	})
	ws.WriteMessage(websocket.TextMessage, msg)
	ws.WriteMessage(websocket.TextMessage, msg2)
	for _, conn := range userConnList {
		_ = conn.WriteMessage(websocket.TextMessage, msg)
	}
	userConnList = append(userConnList, ws)
}

// 收到客户端的聊天消息进行转发
// data = {username:"zhangsan",msg:"你好"}
func OnChatMessage(ws *websocket.Conn, data interface{}) {
	message, _ := json.Marshal(Message{
		MsgKey:  "ChatMessage",
		MsgData: data,
	})
	for _, conn := range userConnList {
		_ = conn.WriteMessage(websocket.TextMessage, message)
	}
}

// 检查数组是否存在元素
func IsContain(items []string, item string) bool {
	for _, eachItem := range items {
		if eachItem == item {
			return true
		}
	}
	return false
}

// 离开聊天室
func OnLeave(ws *websocket.Conn, data interface{}) {
	if IsContain(userNameList, data.(string)) {
		// 删除用户名
		nameList := make([]string, 0)
		for _, name := range userNameList {
			if name != data.(string) {
				nameList = append(nameList, name)
			}
		}
		userNameList = nameList

		msg1, _ := json.Marshal(Message{
			MsgKey:  "LeaveRoom",
			MsgData: fmt.Sprintf("%s离开了房间", data.(string)),
		})
		msg2, _ := json.Marshal(Message{
			MsgKey:  "UserNameList",
			MsgData: userNameList,
		})
		// 通知客户端 离开了房间,以及更新用户列表
		for _, conn := range userConnList {
			conn.WriteMessage(websocket.TextMessage, msg1)
			conn.WriteMessage(websocket.TextMessage, msg2)
		}
	}

	// 删除连接
	connList := make([]*websocket.Conn, 0)
	for _, conn := range userConnList {
		if conn != ws {
			connList = append(connList, conn)
		}
	}
	userConnList = connList

	// 减少人数
	userCount--
	msg, _ := json.Marshal(Message{
		MsgKey:  "UserCount",
		MsgData: userCount,
	})
	for _, conn := range userConnList {
		conn.WriteMessage(websocket.TextMessage, msg)
	}

}

// 客户端设定自己的名字
func OnSetName(ws *websocket.Conn, data interface{}) {
	if IsContain(userNameList, data.(string)) || data.(string) == "" {
		msg, _ := json.Marshal(
			Message{
				MsgKey:  "SetUsername",
				MsgData: false,
			})
		// 通知客户端该名称不可用
		ws.WriteMessage(websocket.TextMessage, msg)
	} else {
		// 通知客户端该名称可用
		msg, _ := json.Marshal(
			Message{
				MsgKey:  "SetUsername",
				MsgData: true,
			})
		ws.WriteMessage(websocket.TextMessage, msg)

		userNameList = append(userNameList, data.(string))

		msg1, _ := json.Marshal(Message{
			MsgKey:  "EnterRoom",
			MsgData: fmt.Sprintf("%s进入了房间", data.(string)),
		})
		msg2, _ := json.Marshal(Message{
			MsgKey:  "UserNameList",
			MsgData: userNameList,
		})
		// 通知每个客户端 进入房间 以及 用户名列表更新
		for _, conn := range userConnList {
			conn.WriteMessage(websocket.TextMessage, msg1)
			conn.WriteMessage(websocket.TextMessage, msg2)
		}
	}
}

// 处理错误
func OnError(ws *websocket.Conn, data interface{}) {
	_ = ws.WriteMessage(websocket.TextMessage, data.([]byte))
}
