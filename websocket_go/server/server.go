package server

import (
	"fmt"
	"net/http"
)

type Server struct {

}


func (s *Server) Start(){
	InitMsgMap()

	http.HandleFunc("/chat", MessageHandler)       //将chat请求交给wshandle处理
	http.HandleFunc("/ping",Ping)

	defer func(){
		if err:=recover();err!=nil{
			fmt.Println(err)
		}
	}()
	addr := "127.0.0.1:8888"
	fmt.Println("服务已经启动,地址:",addr)
	_ = http.ListenAndServe(addr, nil) //开始监听
}


func Ping (w http.ResponseWriter, r *http.Request){
	w.Write([]byte("Pong"))
}

