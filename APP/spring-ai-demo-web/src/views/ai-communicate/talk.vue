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
              <v-row  v-for="(session, index) in sessionList" :key="session.id">
                <!-- 使用 v-bind 来动态控制选中卡片的样式 -->
                <v-card class="w-100 mt-4 text-left" @click="selectSession(session.id)" :class="{ 'selected-card': selectedSession === session.id }">
                  <v-card-title class="font-weight-bold">
                    {{ session.name }}
                  </v-card-title>
                  <v-card-text>
                    <span>{{ session.num || 0 }}条对话</span> <br />
                    <span>{{ session.createTime }}</span>
                  </v-card-text>

                  <!-- 删除按钮，只有选中时才显示 -->
                  <v-btn  v-if="selectedSession === session.id" icon color="red" @click.stop="deleteSession(session.id)">
                    <v-icon >mdi-delete</v-icon>
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
            <v-row style="height: 500px; overflow-y: auto">
              <v-timeline align="start" side="end">
                <v-timeline-item
                  v-for="(activity, index) in activities"
                  :key="index"
                  :icon="activity.icon"
                  :dot-color="activity.color"
                  :size="activity.size"
                  :timestamp="activity.timestamp"
                  :hollow="activity.hollow"
                  :type="activity.type">
                  <template v-slot:icon>
                    <v-avatar :image="activity.avatar"></v-avatar>
                  </template>
                  <v-card class="elevation-2">
                    <v-card-text>
                      {{ activity.content }}
                      <v-icon icon="mdi-clock-time-ten"></v-icon> {{ activity.timestamp }}
                    </v-card-text>
                  </v-card>
                </v-timeline-item>
              </v-timeline>
            </v-row>

            <!-- 聊天输入框区域 -->
            <v-row class="mt-8 bg-gray-300" align="center">
              <!-- 用户头像 -->
              <v-col cols="2" class="d-flex align-center">
                <v-avatar icon="$vuetify" :image="user.avatar" size="43"></v-avatar>
              </v-col>

              <!-- 输入框区域 -->
              <v-col cols="8">
                <v-text-field v-model="msg" label="请输入消息" @keydown.enter="sendMsg" outlined dense clearable class="chat-input" />
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
          <v-list-item class="flex justify-center"
            v-for="user in userList"
            :key="user.userId"
            @click="init(user)"
            :active="selectedUserId === user.userId" 
          >
            <v-list-item-content >
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
import { ref, onMounted } from 'vue';
import { getCurrentInstance } from 'vue';
import { SSE } from 'sse.js'
const { proxy } = getCurrentInstance();

const activities = ref([
  {
    content: '⭐欢迎使用智能客服！请问有什么可以帮您的?',
    timestamp: new Date().toLocaleDateString() + ' ' + new Date().toLocaleTimeString(),
    color: 'red-lighten-2',
    size: 'large',
    type: 'primary',
    icon: 'mdi-robot',
    avatar: 'https://i.pravatar.cc/64',
  },
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
    content: `${msg.value}`,
    timestamp: new Date().toLocaleDateString() + ' ' + new Date().toLocaleTimeString(),
    size: 'large',
    type: 'primary',
    icon: 'mdi-account',
    avatar: user.value.avatar,
  });

  // 添加一个等待消息项
  const waitingActivity = {
    content: 'waiting...',
    timestamp: new Date().toLocaleDateString() + ' ' + new Date().toLocaleTimeString(),
    size: 'small',
    type: 'success',
    icon: 'mdi-check-circle',
    avatar: 'https://i.pravatar.cc/64', // 默认的头像
  };
  activities.value.push(waitingActivity);
   // 新建一个 ChatMessage 对象，准备发送给后端
   const chatMessage = {
    aiSessionId: selectedSession.value,  // 当前活动会话ID
    creatorId: user.value.userId, // 创建者ID
    editorId: user.value.userId, // 编辑者ID（如果需要的话）
    type: 'user',  // 消息类型
    textContent: msg.value,  // 用户输入的文本消息
    // medias: [],  // 如果有媒体文件，可以在这里添加
    // createTime: new Date().toISOString(),  // 创建时间
  };

  msg.value = ''; // 清空输入框
  
  // 使用 SSE 库来处理 SSE 流
  const evtSource = new SSE('http://localhost:8080/message/chat', {
    method: 'POST',  // 使用 POST 请求
    headers: { 'Content-Type': 'application/json' },
    payload: JSON.stringify(chatMessage),  // 发送的消息体
    withCredentials: false,  // 根据需要设置
    start: false,  // 禁用自动启动，手动调用 stream()
  });

  let currentActivity = activities.value[activities.value.length - 1]; // 获取最后一个"等待..."消息项
  let currentMessage = '';  // 用于拼接接收到的部分内容

  // 监听 SSE 消息
  evtSource.addEventListener('message', async (event) => {
    const response = JSON.parse(event.data);
    // const finishReason = response.result.metadata.finishReason;
    if (response) {
        // 每次接收到新的部分消息，拼接到当前消息内容
        currentMessage += response;
        // 实时更新活动列表中的消息内容
        currentActivity.content = currentMessage;
      // 检查是否结束
      if (finishReason && finishReason.toLowerCase() === 'stop') {
        console.log('停止事件源');
        evtSource.close(); // 停止事件源
      }
    }
  });


  // 错误处理 
  evtSource.onerror = (err) => {
    console.error('Error receiving SSE stream:', err);
    currentActivity.content = 'Error occurred while receiving message.';
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
  getSession(sessionId)
};
// 删除操作
const deleteSession = (sessionId) => {
  selectedSession.value = null; // 删除后取消选中
  proxy.$api.ai.deleteSession(sessionId).then((res) => {
    getSessionList(user.value.userId);
  });
};
//页面初始化用户会话列表
const userInitDialog =ref(true);
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
}
// 初始加载
onMounted(() => {
  setTimeout(() => {
    getAllUser()
  }, 100);
});
</script>

<style scoped>
#chat {
  margin-top: 20px;
}
</style>
