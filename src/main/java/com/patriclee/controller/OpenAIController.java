package com.patriclee.controller;

import com.patriclee.advisor.M3CustomerLoggerAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@RestController
@CrossOrigin
public class OpenAIController {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    public OpenAIController(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory, VectorStore vectorStore) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClientBuilder.defaultSystem(
                """
                        您是“PatricLee”Open公司的客户聊天支持代理。请以友好、乐于助人且愉快的方式来回复。
                        您正在通过在线聊天系统与客户互动。
                        请讲中文。
                        今天的日期是 {current_date}.
                        """
        )
                .defaultAdvisors(
                        new PromptChatMemoryAdvisor(chatMemory)
                )
                .build();
    }
    @CrossOrigin
    @GetMapping(value = "/ai/generateStreamAsStringStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> generateStreamAsString(  @RequestParam(value = "messageListId") String messageListId,
                                                 @RequestParam(value = "message") String message) {

        Flux<String> content = this.chatClient.prompt()
                .user(message)
                .system(promptSystemSpec -> promptSystemSpec.param("current_date", LocalDate.now().toString()))
                .advisors(advisorSpec ->
                        advisorSpec
                                .param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY,messageListId) //消息
                                .param(AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY,100))
                .advisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults()) )
                .advisors(new M3CustomerLoggerAdvisor())
                .stream()
                .content();
        return content.concatWith(Flux.just("[complete]"));
    }
}
