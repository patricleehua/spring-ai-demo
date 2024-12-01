//package com.patriclee.advisor;//package com.patriclee.advisor;
//
//import org.springframework.ai.chat.client.AdvisedRequest;
//import org.springframework.ai.chat.client.RequestResponseAdvisor;
//import org.springframework.ai.chat.model.ChatResponse;
//import reactor.core.publisher.Flux;
//
//import java.util.Map;
//
///**
// * 1.0.0-M2以下自定义 Advisor
// */
//public class M2CustomerLoggerAdvisor  implements RequestResponseAdvisor {
//    @Override
//    public String getName() {
//         return this.getClass().getName();
//    }
//
//    @Override
//    public AdvisedRequest adviseRequest(AdvisedRequest request, Map<String, Object> context) {
//        System.out.println("用户自定义日志检查（简单测试）:"+request.userText());
//        return request;
//    }
//
//    @Override
//    public ChatResponse adviseResponse(ChatResponse response, Map<String, Object> context) {
//        return response;
//    }
//
//    @Override
//    public Flux<ChatResponse> adviseResponse(Flux<ChatResponse> fluxResponse, Map<String, Object> context) {
//        return RequestResponseAdvisor.super.adviseResponse(fluxResponse, context);
//    }
//}
