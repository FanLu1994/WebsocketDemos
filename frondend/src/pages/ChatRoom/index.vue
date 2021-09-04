<template>
  <div>
    <div class="flex flex-row container mx-auto  border-2
    border-green-400 rounded shadow-lg bg-green-400 h-screen p-5">
      <div class="w-1/4 grid grid-rows-4 ">
        <div class="text-left px-3 bg-green-400">
          当前在线:{{userCount}}人
        </div>
        <div class="row-span-3 text-left px-3 bg-white border-l-2 border-r-4 border-b-2 border-green-400 overflow-auto">
          <span>
            用户列表:
          </span>
          <p v-for="user in userList">
            {{user}}
          </p>
        </div>
      </div>
      <div class="w-3/4 grid grid-rows-4  bg-green-400">
        <div class="bg-white row-span-3 overflow-auto" id="messageBox">
          <div v-for="item in historyMessage" >
            <div v-if="item.username!==undefined" class="px-5 text-left">
              <span class="text-purple-500">{{item.username}}</span>:{{item.msg}}
            </div>
            <div v-else class="text-pink-400">
              系统提示:{{item.msg}}
            </div>
          </div>
        </div>
        <div class="grid grid-cols-4 my-2 mx-2">
          <div class="col-span-3">
            <el-input v-model="message"  @keyup.enter.native="sendMessage()"></el-input>
          </div>
          <div>
            <el-button @click="sendMessage">发送消息</el-button>
          </div>
        </div>
      </div>
    </div>


    <el-dialog
      title="设置用户名"
      :visible.sync="dialogVisible"
      custom-class="rounded"
      width="25%">
      <span>
        <el-input v-model="username" @keyup.enter.native="setUsername()"></el-input>
      </span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="setUsername">确 定</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
export default {
  name: "index",
  data(){
    return {
      userCount:0,
      userList:[],
      username:'',
      usernameAvailable:false,

      socket:'',

      historyMessage:[],
      message:"",

      dialogVisible:false,
    }
  },

  created(){
    this.connect()
    window.onbeforeunload = e => {      //刷新时弹出提示
      this.leaveRoom()
    };

  },

  methods:{
    // 连接到服务端
    connect(){
      let that = this
      this.socket = new WebSocket("ws://localhost:8888/chat")
      this.socket.onmessage = function(event) {
        that.msgHandler(event.data)
      };
      this.socket.onclose = function(evt) {
        console.log("Connection closed.");
      };
    },


    // 设置用户名
    setUsername(){
      if(this.username===""){
        this.$message.error("用户名不能为空")
        return
      }

      let data = {
        msg_key: "SetName",
        msg_data: this.username
      }
      this.socket.send(JSON.stringify(data))
    },

    // 发送聊天消息
    sendMessage(){
      if(!this.usernameAvailable){
        // this.$message.error("请先设置用户名")
        this.dialogVisible = true
        return
      }
      if(this.message===""){
        this.$message.error("不能发送空消息")
        return
      }
      let data = {
        msg_key: "ChatMessage",
        msg_data:{username:this.username,msg:this.message}
      }
      this.socket.send(JSON.stringify(data))
      this.message = ""
    },

    // 离开聊天室
    leaveRoom(){
      let data = {
        msg_key: "Leave",
        msg_data: this.username
      }
      this.socket.send(JSON.stringify(data))
      this.socket.close()
    },

    // 处理回包消息
    msgHandler(data){
      let msg = JSON.parse(data)
      console.log(msg)
      switch (msg.msg_key) {
        case "UserCount":
          this.userCount = msg.msg_data
          break
        case "EnterRoom":
          this.historyMessage.push({msg:msg.msg_data})
          break
        case "LeaveRoom":
          this.historyMessage.push({msg:msg.msg_data})
          break
        case "SetUsername":
          this.usernameAvailable = msg.msg_data;
          if(this.usernameAvailable){
            this.dialogVisible = false
          }else{
            this.$message.error("用户名已被占用")
          }
          break
        case 'UserNameList':
          this.userList = msg.msg_data
          break
        case 'ChatMessage':
          this.historyMessage.push(msg.msg_data)
          let messageBox = document.getElementById('messageBox');
          messageBox.scrollTop = messageBox.scrollHeight;
          break
        default:
          console.log(msg.msg_key)
          console.log("默认")
      }

    },


    // websockt接受消息
    onMessage(evt){
      console.log(evt)
    },

    // websocket关闭
    close(){
      console.log("主动断开连接")
      this.socket.close()
    }
  },

  destroyed() {
    this.leaveRoom()
  }
}
</script>

<style scoped>

</style>
