# @Time    : 2021/9/5 11:04
# @Author  : fanlu
import asyncio
import websockets

from server import chatRoom

asyncio.get_event_loop().run_until_complete(
    websockets.serve(chatRoom, 'localhost', 8888)
)
print("服务器已经启动,地址:localhost:8888")
asyncio.get_event_loop().run_forever()
