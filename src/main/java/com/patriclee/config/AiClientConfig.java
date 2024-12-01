package com.patriclee.config;

import com.patriclee.advisor.M3CustomerLoggerAdvisor;
import com.patriclee.advisor.ReReadingAdvisor;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;


@Configuration
public class AiClientConfig {
    @Value("classpath:prompts/system-message.st")
    private Resource systemResource;
    @Bean
    @Qualifier("aliyunChatClient")
    public ChatClient aliyunChatClient(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory, VectorStore vectorStore) {
        // 阿里云 AI 配置
        return chatClientBuilder
                .defaultSystem(systemResource)
                .defaultAdvisors(
//                        new PromptChatMemoryAdvisor(chatMemory),
                     new M3CustomerLoggerAdvisor()
//                        new ReReadingAdvisor()

                )
                .defaultFunctions("temperature")
                .build();
    }

    @Bean
    @Qualifier("chatGptClient")
    public ChatClient chatGptClient(ChatClient.Builder chatClientBuilder) {
        // ChatGPT 配置
        return chatClientBuilder
                .defaultSystem("我是傻蛋")
                .build();
    }

}
