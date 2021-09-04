package server


type Message struct {
	MsgKey string 					`json:"msg_key"`
	MsgData interface{} 			`json:"msg_data"`
}