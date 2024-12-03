import request from '@/utils/axios';

export default {
    generateStreamAsString(data) {
   // 创建一个 EventSource 来处理 SSE 请求
    const eventSource = new EventSource(`http://localhost:8080/ai/generateStreamAsStringStream?messageListId=${data.messageListId}&message=${encodeURIComponent(data.message)}`);
    
    return eventSource;
  },
  /**
   * 根据会话Id获取会话
   */
  getSession(id){
    return request({
      url: '/session' + `/${id}`,
      method: 'get',
  })
  },
    /**
   * 根据UserId获取会话列表
   */
    getSessionList(id){
      return request({
        url: '/session/user' + `/${id}`,
        method: 'get',
    })
    },
    createSession(data){
      return request({
        url: '/session/user/createSession',
        method: 'post',
        data: {
          userId: data.createUserId,
          name: data.sessionName,
        }
    })
    },
    deleteSession(id){
      return request({
        url: '/session' + `/${id}`,
        method: 'delete',
    })
    },
    getAllUser(){
      return request({
        url: '/user/findAll',
        method: 'get',
    })
    },
    saveMessage(data){
      return request({
        url: '/message/save',
        method: 'post',
        data: data
    })
    },
    getHistoryMessages(conversationId, lastN) {
      return request({
        url: '/message/history',
        method: 'get',
        params: { 
          conversationId: conversationId, 
          lastN: lastN 
        }
    })
    },
    cleanHistory(conversationId) {
      return request({
        url: '/message/history/'+conversationId,
        method: 'delete',
     
    })
    },
};