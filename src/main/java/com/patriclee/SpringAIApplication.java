package com.patriclee;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringAIApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringAIApplication.class, args);
    }

    //对话记忆setup1
    @Bean
    public ChatMemory chatMemory(){
        return new InMemoryChatMemory();
    }
}