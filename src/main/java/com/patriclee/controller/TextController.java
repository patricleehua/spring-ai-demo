package com.patriclee.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@RestController
@CrossOrigin
public class TextController {
    private final VectorStore vectorStore;
    private final ChatClient chatGptClient;
    public TextController(@Qualifier("aliyunChatClient") ChatClient aliyunChatClient , VectorStore vectorStore) {
        this.chatGptClient = aliyunChatClient;
        this.vectorStore = vectorStore;
    }

    @CrossOrigin
    @GetMapping(value = "/ai/speak", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> generateStreamAsString(  @RequestParam(value = "messageListId") String messageListId,
                                                 @RequestParam(value = "message") String message) {
        String promptWithContext = """
                下面是上下文信息
                ---------------------
                {question_answer_context}
                ---------------------
                给定的上下文和提供的历史信息，而不是事先的知识，回复用户的意见。
                如果答案不在上下文中，告诉用户你不能回答这个问题。
                """;
        Flux<String> content = this.chatGptClient.prompt()
                .user(message)
                .system(promptSystemSpec -> promptSystemSpec.param("current_date", LocalDate.now().toString()))
                .advisors(advisorSpec ->
                        advisorSpec
                                .param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY,messageListId) //消息
                                .param(AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY,100))
                .advisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults(),promptWithContext) )
                .stream()
                .content();
        return content.concatWith(Flux.just("[complete]"));
    }

}
