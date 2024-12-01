package com.patriclee.functioncall;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;


@Configuration
@Description("AI可以回调的全部函数")
public class FunctionCallback {

    public record cityTemperature(
            @JsonProperty(required = true, value = "cityName") @JsonPropertyDescription(value = "需要查询的城市名") String cityName,

            @JsonProperty(required = true, value = "country") @JsonPropertyDescription(value = "需要查询的国家名")  String country) {}

    /**
     * Function<cityTemperature,String> 第一个是入参，第二个是出参
     * @return
     */
    @Bean
    @Description("获取城市气温")
    public Function<cityTemperature,String> temperature(){
        return cityTemperature -> {
            return cityTemperature.country+"的"+cityTemperature.cityName + "的气温为20度";
        };
    }
}
