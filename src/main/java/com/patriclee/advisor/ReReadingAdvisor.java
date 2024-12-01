package com.patriclee.advisor;

import org.springframework.ai.chat.client.advisor.api.*;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

public class ReReadingAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {
    /**
     * 实现Re2的私有代码逻辑
     * @param advisedRequest
     * @return
     */
    private AdvisedRequest before(AdvisedRequest advisedRequest) {
        //提取用户问题构建参数
        Map<String, Object> advisedUserParams = new HashMap<>(advisedRequest.userParams());
        advisedUserParams.put("re2_input_query", advisedRequest.userText());
        System.out.println("实现Re2的私有代码逻辑被执行..." );
        //修改用户原始查询&提示重新阅读问题 返回新的AdvisedRequest对象
        return AdvisedRequest.from(advisedRequest)
            .withUserText("""
                {re2_input_query}
                Read the question again: {re2_input_query}
                """)
            .withUserParams(advisedUserParams)
            .build();
    }

    /**
     * 拦截非流式请求并应用自定义实现Re2的私有代码逻辑
     * @param advisedRequest
     * @param chain
     * @return
     */
    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        return chain.nextAroundCall(this.before(advisedRequest));
    }

    /**
     * 截流请求并应用自定义实现Re2的私有代码逻辑
     * @param advisedRequest
     * @param chain
     * @return
     */
    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        return chain.nextAroundStream(this.before(advisedRequest));
    }

    /**
     * 为顾问提供唯一名称
     * @return
     */
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * 设定顾问执行顺序
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
