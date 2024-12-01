package com.patriclee.advisor;

import org.springframework.ai.chat.client.advisor.api.*;
import reactor.core.publisher.Flux;

/**
 * 1.0.0-M3 以上自定义 Advisor
 */
public class M3CustomerLoggerAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {
    /**
     * 用户自定义日志检查（简单测试）
     * @param advisedRequest
     * @return
     */
    private AdvisedRequest checkInfo (AdvisedRequest advisedRequest){
        System.out.println("用户自定义日志检查（简单测试）:"+advisedRequest.userText());
        //可以构建新的或者什么都不处理
        return advisedRequest;
    }
    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        return chain.nextAroundCall(this.checkInfo(advisedRequest));
    }

    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        return chain.nextAroundStream(this.checkInfo(advisedRequest));
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public int getOrder() {
        return 101;
    }
}