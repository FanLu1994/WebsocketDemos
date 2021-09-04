package server

import "github.com/gorilla/websocket"

type User struct {
	conn *websocket.Conn
}