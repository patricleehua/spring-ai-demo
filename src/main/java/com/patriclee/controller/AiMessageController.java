package com.patriclee.controller;


import com.patriclee.domain.dto.AiMessageDTO;
import com.patriclee.domain.entity.AiMessage;
import com.patriclee.service.AiMessageChatMemory;
import com.patriclee.service.AiMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.model.Media;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import static com.alibaba.dashscope.utils.JsonUtils.toJson;

@RequestMapping("/message")
@RestController
@Slf4j
@CrossOrigin
public class AiMessageController {
    private final AiMessageService aiMessageService;

    private final ChatClient  chatClient;
    private final AiMessageChatMemory chatMemory;

    public AiMessageController(AiMessageService aiMessageService, @Qualifier("aliyunChatClient")  ChatClient chatClient, AiMessageChatMemory chatMemory) {
        this.aiMessageService = aiMessageService;
        this.chatClient = chatClient;
        this.chatMemory = chatMemory;
    }

    /**
     * 消息保存
     *
     * @param aiMessageDTO 用户发送的消息/AI回复的消息
     */
    @PostMapping("/save")
    public void save(@RequestBody AiMessageDTO aiMessageDTO) {
        aiMessageService.saveAiMessage(aiMessageDTO);
    }

    /**
     *
     * @param aiMessageDTO 消息包含文本信息，会话id，多媒体信息（图片语言）。
     * @return SSE流
     */
    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @CrossOrigin
    public Flux<ServerSentEvent<String>> chatStreamWithHistory(@RequestBody AiMessageDTO aiMessageDTO) {
        // MessageChatMemoryAdvisor的三个参数解释。
        // 1. 如果需要存储会话和消息到数据库，自己可以实现ChatMemory接口，这里使用自己实现的AiMessageChatMemory，数据库存储。
        // 2. 传入会话id，MessageChatMemoryAdvisor会根据会话id去查找消息。
        // 3. 只需要携带最近10条消息
        var messageChatMemoryAdvisor = new MessageChatMemoryAdvisor(chatMemory, aiMessageDTO.getAiSessionId(), 10);
        AiMessage aiMessage = aiMessageService.convertToAiMessage(aiMessageDTO);
        return this.chatClient.prompt()
                .user(promptUserSpec -> {
                    // AiMessage转成Message
                    Message message = AiMessageChatMemory.toSpringAiMessage(aiMessage);
                    if (message instanceof UserMessage userMessage &&
                            !CollectionUtils.isEmpty(userMessage.getMedia())) {
                        // 用户发送的图片/语言
                        Media[] medias = new Media[userMessage.getMedia().size()];
                        promptUserSpec.media(userMessage.getMedia().toArray(medias));
                    }
                    // 用户发送的文本
                    promptUserSpec.text(message.getContent());
                }) // MessageChatMemoryAdvisor会在消息发送给大模型之前，从ChatMemory中获取会话的历史消息，然后一起发送给大模型。
                .advisors(messageChatMemoryAdvisor)
                .stream()
                .content()
                .map(chatResponse -> ServerSentEvent.builder(toJson(chatResponse))
                        // 和前端监听的事件相对应
                        .event("message")
                        .build())
                .doOnTerminate(() -> {
                    // 流结束时执行
                    log.info("SSE stream has ended.");
                });
    }

}
