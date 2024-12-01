package com.patriclee.config;


import org.springframework.ai.autoconfigure.vectorstore.redis.RedisVectorStoreAutoConfiguration;
import org.springframework.ai.autoconfigure.vectorstore.redis.RedisVectorStoreProperties;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.RedisVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPooled;


@Configuration
//禁⽤SpringAI提供的RedisStack 向量数据库的⾃动配置，会和Redis的配置冲突。
@EnableAutoConfiguration(exclude = {RedisVectorStoreAutoConfiguration.class})
// 读取RedisStack的配置信息
@EnableConfigurationProperties({RedisVectorStoreProperties.class})
public class RedisVectorConfig {

    /**
     * 创建 RedisStack 向量数据库
     *
     * @param embeddingModel 不同厂家的嵌入模型
     * @param properties    redis-stack 的配置信息
     * @see RedisVectorStoreProperties 读取 RedisStack 的配置信息
     * @return vectorStore 向量数据库
     */
    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel,RedisVectorStoreProperties properties,RedisConnectionDetails redisConnectionDetails) {
        var config = RedisVectorStore.RedisVectorStoreConfig.builder()
            .withIndexName(properties.getIndex())
            .withPrefix("rag:")
            .withMetadataFields(
                    RedisVectorStore.MetadataField.text("filename"),
                    RedisVectorStore.MetadataField.text("question")
            )
            .build();
        return new RedisVectorStore(config, embeddingModel,
            new JedisPooled(
                redisConnectionDetails.getStandalone().getHost(),
                redisConnectionDetails.getStandalone().getPort(),
                redisConnectionDetails.getUsername(),
                redisConnectionDetails.getPassword()
            ),
            properties.isInitializeSchema());
    }

}
