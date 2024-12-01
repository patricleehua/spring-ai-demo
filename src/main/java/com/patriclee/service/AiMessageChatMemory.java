package com.patriclee.service;

import com.patriclee.domain.entity.AiMessage;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.model.Media;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 将对话消息保存进数据库
 */
@Service
public class AiMessageChatMemory implements ChatMemory {

    private final AiSessionService AiSessionService;
    private final AiMessageService AiMessageService;
    public AiMessageChatMemory(AiSessionService aiSessionService, AiMessageService aiMessageService) {
        this.AiSessionService = aiSessionService;
        this.AiMessageService = aiMessageService;
    }
    /**
     * 不实现，手动前端发起请求保存用户的消息和大模型回复的消息
     */
    @Override
    public void add(String conversationId, List<Message> messages) {
    }
    /**
     * 查询会话内的消息最新n条历史记录
     *
     * @param conversationId 会话id
     * @param lastN          最近n条
     * @return org.springframework.ai.chat.messages.Message格式的消息
     */
    @Override
    public List<Message> get(String conversationId, int lastN) {

        List<AiMessage> aiMessages = AiMessageService.getaiMessageListBySessionId(conversationId, lastN);
        if (aiMessages != null){
            // 转成Message对象
            return aiMessages.stream().map(AiMessageChatMemory::toSpringAiMessage).toList();
        }
        return List.of();
    }
    /**
     * 清除会话内的消息
     *
     * @param conversationId 会话id
     */
    @Override
    public void clear(String conversationId) {
        AiMessageService.deleteBySessionId(conversationId);
    }

    public static Message toSpringAiMessage(AiMessage aiMessage) {
        List<Media> mediaList = new ArrayList<>();
        if (!(aiMessage.getMedias() == null)) {
            mediaList = aiMessage.getMedias().stream().map(AiMessageChatMemory::toSpringAiMedia).toList();
        }
        switch (aiMessage.getType()){
            case "user" -> {
                return new UserMessage(aiMessage.getTextContent(), mediaList);
            }
            case "assistant" -> {
                return new AssistantMessage(aiMessage.getTextContent());
            }
            case "system" -> {
                return new SystemMessage(aiMessage.getTextContent());
            }
            default -> throw new RuntimeException("不支持的消息类型");
        }

    }
    @SneakyThrows
    public static Media toSpringAiMedia(AiMessage.Media media) {
        return new Media(new MediaType(media.type), new URL(media.data));
    }
}
