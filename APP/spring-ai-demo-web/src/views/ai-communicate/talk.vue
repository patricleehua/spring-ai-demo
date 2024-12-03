<template>
  <div style="width: 1000px">
    <v-container class="bg-gray-100 mb-6 w-full">
      <v-sheet border="info md" rounded>
        <v-row align="start" justify="center">
          <!--           对话列表 -->
          <v-col cols="3">
            <div class="bg-green-200">
              <v-row cols="12">
                <span>AI对话列表</span>
              </v-row>
              <v-row v-for="(session, index) in sessionList" :key="session.id">
                <!-- 使用 v-bind 来动态控制选中卡片的样式 -->
                <v-card class="w-100 mt-4 text-left" @click="selectSession(session.id)"
                  :class="{ 'selected-card': selectedSession === session.id }">
                  <v-card-title class="font-weight-bold">
                    {{ session.name }}
                  </v-card-title>
                  <v-card-text>
                    <span>{{ session.num || 0 }}条对话</span> <br />
                    <span>{{ session.createTime }}</span>
                  </v-card-text>

                  <!-- 删除按钮，只有选中时才显示 -->
                  <v-btn v-if="selectedSession === session.id" icon color="red" @click.stop="deleteSession(session.id)">
                    <v-icon>mdi-delete</v-icon>
                  </v-btn>
                       <v-btn v-if="selectedSession === session.id" icon color="blue" @click.stop="clearHistory(session.id)">
                    <v-icon>mdi-trash-can</v-icon>
                  </v-btn>
                </v-card>
              </v-row>
              <v-row cols="12 " align="end">
                <v-btn @click="dialog = true" color="primary" dark>新建对话</v-btn>
              </v-row>
            </div>
          </v-col>
          <!-- 聊天内容区域 -->
          <v-col cols="9" class="bg-blue-100">
            <v-row>
              <div class="flex-wrap chat" style="flex: 1;height: 500px; overflow-y: auto">
                <div v-for="(item, key) in activities" :key="key" :class="item.type == 'user' ? 'rightMsg' : 'leftMsg'">
                  <img v-if="item.type == 'assistant'"
                    src="data:image/png;base64,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"
                    alt="" />
                  <div class="msg">{{ item.textContent }}</div>
                  <img v-if="item.type == 'user'" :src="user.avatar" alt="" />
                </div>
              </div>
            </v-row>

            <!-- 聊天输入框区域 -->
            <v-row class="mt-8 bg-gray-300" align="center">
              <!-- 用户头像 -->
              <v-col cols="1" class="d-flex align-center">
                <v-avatar icon="$vuetify" :image="user.avatar" size="43"></v-avatar>
              </v-col>
              <!-- 按钮 -->
              <v-col cols="2" class="d-flex align-center">

                <v-switch v-model="enableVectorStore" label="RAG" false-value=false true-value=true color="primary"
                  hide-details></v-switch>

              </v-col>
              <!-- 输入框区域 -->
              <v-col cols="7">
                <v-text-field v-model="msg" label="请输入消息" @keydown.enter="sendMsg" outlined dense clearable
                  class="chat-input" />
              </v-col>

              <!-- 发送按钮 -->
              <v-col cols="2" class="text-right">
                <v-btn @click="sendMsg" color="primary" class="send-button">发送</v-btn>
              </v-col>
            </v-row>
          </v-col>
        </v-row>
      </v-sheet>
      <!-- 创建对话框 -->
      <v-dialog v-model="dialog" width="auto">
        <v-card min-width="400" title="创建对话">
          <v-card-text>
            <v-text-field v-model="createSessionForm.sessionName" label="会话名称" />
          </v-card-text>
          <template v-slot:actions>
            <v-btn class="ms-auto" text="取消" @click="dialog = false"></v-btn>
            <v-btn class="ms-auto" text="确认" @click="createSession()"></v-btn>
          </template>
        </v-card>
      </v-dialog>
      <!-- 登入选择用户 -->
      <v-dialog v-model="userInitDialog" width="auto">
        <v-card min-width="400" title="选择用户">
          <v-card-text>
            <v-list dense>
              <v-list-item class="flex justify-center" v-for="user in userList" :key="user.userId" @click="init(user)"
                :active="selectedUserId === user.userId">
                <v-list-item-content>
                  <v-avatar icon="$vuetify" :image="user.avatar" size="43"></v-avatar>
                  <span class="ml-4">{{ user.userName }}</span>
                </v-list-item-content>
              </v-list-item>
            </v-list>
          </v-card-text>
        </v-card>
      </v-dialog>
    </v-container>
  </div>
</template>

<script setup>
import { ref, onMounted,nextTick  } from 'vue';
import { getCurrentInstance } from 'vue';
import { SSE } from 'sse.js';
const { proxy } = getCurrentInstance();

const activities = ref([
  
]);

const msg = ref('');
let eventSource;
const user = ref({
  username: '李华',
  userId: '1',
  avatar: 'https://cdn.pixabay.com/photo/2021/11/12/03/04/woman-6787784_1280.png', // 头像的图片地址
});

// 发送消息函数
const sendMsg = () => {
  if (msg == null) return; // 如果消息为空，则不发送

  // 添加用户发送的消息到活动列表
  activities.value.push({
    textContent: `${msg.value}`,
    createTime: new Date().toLocaleDateString() + ' ' + new Date().toLocaleTimeString(),
    type: 'user',
    avatar: user.value.avatar,
    aiSessionId: selectedSession.value,
    creatorId: user.value.userId, // 创建者ID
    editorId: user.value.userId, // 编辑者ID（如果需要的话）
  });

  // 添加一个等待消息项
  const waitingActivity = {
    textContent: 'waiting...',
    createTime: new Date().toLocaleDateString() + ' ' + new Date().toLocaleTimeString(),
    type: 'assistant',
    avatar: 'https://i.pravatar.cc/64',
    aiSessionId: selectedSession.value,
  };
  activities.value.push(waitingActivity);
  // 新建一个 ChatMessage 对象，准备发送给后端
  const chatMessage = {
    aiSessionId: selectedSession.value, // 当前活动会话ID
    creatorId: user.value.userId, // 创建者ID
    editorId: user.value.userId, // 编辑者ID（如果需要的话）
    enableVectorStore: enableVectorStore.value,
    type: 'user', // 消息类型
    textContent: msg.value, // 用户输入的文本消息
    // medias: [],  // 如果有媒体文件，可以在这里添加
    // createTime: new Date().toISOString(),  // 创建时间
  };
  //信息持久化
  saveMessage(chatMessage);
  scrollToNew();
  msg.value = ''; // 清空输入框

  // 使用 SSE 库来处理 SSE 流
  const evtSource = new SSE('http://localhost:8080/message/chat', {
    method: 'POST', // 使用 POST 请求
    headers: { 'Content-Type': 'application/json' },
    payload: JSON.stringify(chatMessage), // 发送的消息体
    withCredentials: false, // 根据需要设置
    start: false, // 禁用自动启动，手动调用 stream()
  });

  let currentActivity = activities.value[activities.value.length - 1]; // 获取最后一个"等待..."消息项
  let currentMessage = ''; // 用于拼接接收到的部分内容

  // 监听 SSE 消息
  evtSource.addEventListener('message', async (event) => {
    const response = JSON.parse(event.data);
    console.log(response);
    // const finishReason = response.result.metadata.finishReason;
    if (response) {
      // 检查是否结束
      if (response === '[complete]') {
        console.log('停止事件源');

        let aiMessage = {
          aiSessionId: selectedSession.value, // 当前活动会话ID
          creatorId: '001', // 创建者ID
          editorId: '001', // 编辑者ID（如果需要的话）
          type: 'assistant', // 消息类型
          textContent: currentMessage, // AI生成的文本消息
        };
        saveMessage(aiMessage);
        scrollToNew();
        evtSource.close(); // 停止事件源
      } else {
        scrollToNew();
        // 每次接收到新的部分消息，拼接到当前消息内容
        currentMessage += response;
        // 实时更新活动列表中的消息内容
        currentActivity.textContent = currentMessage;
      }
    }
  });

  // 错误处理
  evtSource.onerror = (err) => {
    console.error('Error receiving SSE stream:', err);
    currentActivity.textContent = 'Error occurred while receiving message.';
  };

  // 启动流
  evtSource.stream(); // 调用 stream() 来启动流
};

//会话管理
const selectedSession = ref(null); // 当前选中的 session ID
//获取会话
const getSession = (sessionId) => {
  proxy.$api.ai.getSession(sessionId).then((res) => {
    selectedSession.value = res.data.id;
    getHistoryMessages(res.data.id);
  });
};
const sessionList = ref(null);
//获取用户对话列表
const getSessionList = (userId) => {
  proxy.$api.ai
    .getSessionList(userId)
    .then((res) => {
      sessionList.value = res.data;
    })
    .catch((error) => {
      console.error('Error fetching session list:', error);
    });
};
//创建会话
const dialog = ref(false);
const createSessionForm = ref({
  sessionName: '',
  createUserId: '',
});
const createSession = () => {
  createSessionForm.value.createUserId = user.value.userId;
  proxy.$api.ai
    .createSession(createSessionForm.value)
    .then((res) => {
      console.log(res.data);
      dialog.value = false;
      getSessionList(user.value.userId);
    })
    .catch((error) => {
      console.error('Error creating session:', error);
    });
};
// 选中卡片时设置 selectedSession
const selectSession = (sessionId) => {
  // 如果选中的卡片已经是当前选中的卡片，则取消选中
  selectedSession.value = selectedSession.value === sessionId ? null : sessionId;
  getSession(sessionId);
};
// 删除操作
const deleteSession = (sessionId) => {
  selectedSession.value = null; // 删除后取消选中
  proxy.$api.ai.deleteSession(sessionId).then((res) => {
    getSessionList(user.value.userId);
  });
};
//页面初始化用户会话列表
const userInitDialog = ref(true);
const userList = ref(null);
const getAllUser = () => {
  proxy.$api.ai.getAllUser().then((res) => {
    userList.value = res.data;
  });
};
const init = (data) => {
  user.value = data;
  getSessionList(data.userId);
  userInitDialog.value = false;
};
//保存聊天信息
const saveMessage = (data) => {
  proxy.$api.ai.saveMessage(data).then((res) => {
    console.log(res);
  });
};
//获取历史消息
const getHistoryMessages = (conversationId) => {
  let lastN = 10;
  proxy.$api.ai
    .getHistoryMessages(conversationId, lastN)
    .then((res) => {
 
      activities.value = res.data;
      if(res.data.length == 0){
        activities.value = [{
          textContent: '⭐欢迎使用智能客服！请问有什么可以帮您的?',
          createTime: new Date().toLocaleDateString() + ' ' + new Date().toLocaleTimeString(),
          avatar: 'https://i.pravatar.cc/64',
          type: 'assistant',
          aiSessionId: selectedSession.value,
          id: '1',
          },]
      }
      scrollToNew();
    })
    .catch((error) => {
      console.error('Error fetching history messages:', error);
    });
};

// 等待DOM更新完成。自动滚动到最新发送的消息处
const scrollToNew = async () => {
  await nextTick();
  const chatContainer = document.querySelector(".chat");
  if (chatContainer) {
    chatContainer.scrollTop = chatContainer.scrollHeight;
  }
};
// 使用使用 rag支持
const enableVectorStore = ref(false);
//清除历史消息
const clearHistory = (conversationId) => {
    proxy.$api.ai.cleanHistory(conversationId).then((res) => {
    activities.value = [{
          textContent: '⭐欢迎使用智能客服！请问有什么可以帮您的?',
          createTime: new Date().toLocaleDateString() + ' ' + new Date().toLocaleTimeString(),
          avatar: 'https://i.pravatar.cc/64',
          type: 'assistant',
          aiSessionId: selectedSession.value,
          id: '1',
          },]
    });
};
// 初始加载
onMounted(() => {
  setTimeout(() => {
    getAllUser();
  }, 100);
});
</script>

<style scoped>
#chat {
  margin-top: 20px;
}

.leftMsg,
.rightMsg {
  display: flex;
  flex-direction: row;
  justify-content: start;
  align-items: center;
  margin: 10px;

  img {
    width: 40px;
    height: 40px;
    border-radius: 20px;
    overflow: hidden;
    object-fit: cover;
    margin: 0 10px;
  }

  .msg {
    display: inline-block;
    padding: 10px;
    word-wrap: anywhere;
    max-width: 600px;
    background-color: #f6f5f5;
    border-radius: 10px;
  }
}

.rightMsg {
  justify-content: end;

  .msg {
    color: black;
    background-color: #a0b1d0;
  }
}
</style>
