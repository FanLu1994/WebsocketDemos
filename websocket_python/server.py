# @Time    : 2021/9/5 11:06
# @Author  : fanlu
import asyncio
import websockets
import json

usernames = []
userCount = 0
userConns = []


def Message(key, data):
    return json.dumps({"msg_key": key, "msg_data": data})


# 连接事件
async def OnConnect(ws,data):
    '''
    :param ws:
    :param data:
    :return:
    '''
    global userCount
    global userConns
    global usernames
    print("连接成功!")
    userCount += 1
    userConns.append(ws)
    # 通知每个客户端人数更新
    msg = Message("UserCount", userCount)
    await asyncio.wait([user.send(msg) for user in userConns])
    msg = Message("UserNameList",usernames)
    await ws.send(msg)

# 收到聊天消息
async def OnChatMessage(ws,data):
    '''
    转发聊天消息
    :param ws:
    :param data:
    :return:
    '''
    msg = Message("ChatMessage",data["msg_data"])
    await asyncio.wait([user.send(msg) for user in userConns])

# 离开事件
async def OnLeave(ws,data):
    '''
    如果设置了用户名,需要更新用户名集合,并且发送离开房间的消息; 否则只需发送人数减1
    :param ws:
    :param data:
    :return:
    '''
    global userCount
    global userConns
    global usernames
    print("离开了房间")
    userConns.remove(ws)        # 删除连接
    if data["msg_data"] in usernames:
        usernames.remove(data["msg_data"])  # 删除用户
        msg = Message("UserNameList",usernames)
        await asyncio.wait([user.send(msg) for user in userConns])
        msg = Message("LeaveRoom",data["msg_data"]+"离开了房间")
        await asyncio.wait([user.send(msg) for user in userConns])
    userCount-=1
    msg = Message("UserCount",userCount)
    await asyncio.wait([user.send(msg) for user in userConns])



# 设置用户名事件
async def OnSetName(ws,data):
    '''
    设置用户名
    :param ws:
    :param data:
    :return:
    '''
    print("设置用户名")
    global userCount
    global userConns
    global usernames
    name = data['msg_data']
    if name in usernames:
        msg = Message("SetUsername",False)
        await ws.send(msg)
    else:
        usernames.append(name)
        msg = Message("SetUsername", True)
        await ws.send(msg)
        msg = Message("EnterRoom",name+"进入了房间")
        await asyncio.wait([user.send(msg) for user in userConns])
        msg = Message("UserNameList",usernames)
        await asyncio.wait([user.send(msg) for user in userConns])


# 错误处理
async def Onerror(ws,data):
    '''
    错误处理
    :param ws:
    :param data:
    :return:
    '''
    msg = Message("Error",data)
    await ws.send(msg)


# 消息处理map
msgMap = {
    'Connect': OnConnect,
    'Leave': OnLeave,
    'ChatMessage': OnChatMessage,
    'SetName': OnSetName,
    'Error': Onerror
}


# 消息处理器
async def MessageHandler(ws,message):
    message = json.loads(message)
    if message['msg_key'] in msgMap:
        await msgMap[message['msg_key']](ws,message)
    else:
        await msgMap["Error"](ws,"msgkey doesn't exist")


# 服务端主逻辑
async def chatRoom(websocket, path):
    print("建立新连接")
    await msgMap["Connect"](websocket,"")
    while True:
        async for msg in websocket:
            print(msg)
            await MessageHandler(ws=websocket,message=msg)
