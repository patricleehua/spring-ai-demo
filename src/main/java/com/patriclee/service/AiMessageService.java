package com.patriclee.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.patriclee.domain.dto.AiMessageDTO;
import com.patriclee.domain.entity.AiMessage;

import java.util.List;

/**
* @author leehua
* @description 针对表【ai_message(聊天记录表)】的数据库操作Service
* @createDate 2024-11-29 09:08:54
*/
public interface AiMessageService extends IService<AiMessage> {

    List<AiMessage> getaiMessageListBySessionId(String conversationId, int lastN);

    void deleteBySessionId(String conversationId);

    void saveAiMessage(AiMessageDTO aiMessageDTO);

     AiMessage convertToAiMessage(AiMessageDTO aiMessageDTO) ;
}
