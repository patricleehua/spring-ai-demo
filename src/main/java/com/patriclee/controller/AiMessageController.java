package com.patriclee.controller;


import com.patriclee.domain.dto.AiMessageDTO;
import com.patriclee.domain.entity.AiMessage;
import com.patriclee.service.AiMessageChatMemory;
import com.patriclee.service.AiMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.model.Media;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.Collections;

import static com.alibaba.dashscope.utils.JsonUtils.toJson;

@RequestMapping("/message")
@RestController
@Slf4j
@CrossOrigin
public class AiMessageController {
    private final AiMessageService aiMessageService;

    private final ChatClient  chatClient;
    private final AiMessageChatMemory chatMemory;
    private final VectorStore vectorStore;

    public AiMessageController(AiMessageService aiMessageService, @Qualifier("aliyunChatClient")  ChatClient chatClient, AiMessageChatMemory chatMemory, VectorStore vectorStore) {
        this.aiMessageService = aiMessageService;
        this.chatClient = chatClient;
        this.chatMemory = chatMemory;
        this.vectorStore = vectorStore;
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
                .advisors(advisorSpec -> {
                    useChatHistory(advisorSpec, aiMessageDTO.getAiSessionId());
                    useVectorStore(advisorSpec, aiMessageDTO.isEnableVectorStore(),aiMessageDTO.getTextContent());
                })
                .system(promptSystemSpec -> promptSystemSpec.param("current_date", LocalDate.now().toString()))
                .stream()
                .content()
                .map(chatResponse -> ServerSentEvent.builder(toJson(chatResponse))
                        // 和前端监听的事件相对应
                        .event("message")
                        .build())
                .concatWith(Flux.just(ServerSentEvent.builder(toJson("[complete]")).event("message").build())) // 结束标志
                .doOnTerminate(() -> {
                    // 流结束时执行
                    log.info("SSE stream has ended.");
                });
    }
    public void useChatHistory(ChatClient.AdvisorSpec advisorSpec, String sessionId) {
        // MessageChatMemoryAdvisor的三个参数解释。
        // 1. 如果需要存储会话和消息到数据库，自己可以实现ChatMemory接口，这里使用自己实现的AiMessageChatMemory，数据库存储。
        // 2. 传入会话id，MessageChatMemoryAdvisor会根据会话id去查找消息。
        // 3. 只需要携带最近10条消息
        // MessageChatMemoryAdvisor会在消息发送给大模型之前，从ChatMemory中获取会话的历史消息，然后一起发送给大模型。
        advisorSpec.advisors(new MessageChatMemoryAdvisor(chatMemory, sessionId, 10));
    }
    public void useVectorStore(ChatClient.AdvisorSpec advisorSpec, Boolean enableVectorStore, String question) {
        if (!enableVectorStore) return;

        String promptWithContext = """
            下面是上下文信息
            ---------------------
            {question_answer_context}
            ---------------------
            给定的上下文和提供的历史信息，而不是事先的知识，回复用户的意见。如果答案不在上下文中，告诉用户你不能回答这个问题。
            """;

        FilterExpressionBuilder builder = new FilterExpressionBuilder();
        Filter.Expression filterExpression = builder.in("question",
                Collections.singletonList("*" + question + "*")).build();

        System.out.println("Generated filter expression: " + filterExpression);

        // 先查元数据
        SearchRequest metaSearchRequest = SearchRequest
                .query(question)
                .withTopK(3) //指定要返回的相似文档的最大数量
                .withSimilarityThreshold(0.1)
                .withFilterExpression(filterExpression);

        // 执行元数据查询
        var metaSearchResult = vectorStore.similaritySearch(metaSearchRequest);
        if (metaSearchResult.isEmpty()) {
            // 如果没有查到元数据，执行相似度搜索
            SearchRequest similaritySearchRequest = SearchRequest
                    .query(question)
                    .withTopK(3)
                    .withSimilarityThreshold(0.1);
            advisorSpec.advisors(new QuestionAnswerAdvisor(vectorStore, similaritySearchRequest, promptWithContext));
        } else {
            // 查到元数据，则使用找到的元数据构建问题答案的上下文
            advisorSpec.advisors(new QuestionAnswerAdvisor(vectorStore, metaSearchRequest, promptWithContext));
        }
    }

}
