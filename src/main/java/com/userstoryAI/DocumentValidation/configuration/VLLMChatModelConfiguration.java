package com.userstoryAI.DocumentValidation.configuration;

//import java.net.http.HttpClient;
//import java.time.Duration;
//import java.util.Map;
//
//import dev.langchain4j.model.chat.ChatModel;
//import dev.langchain4j.model.openai.OpenAiChatModel;
//import io.github.adamw7.genaiagentcore.http.CustomHttpClient;
//import io.github.adamw7.genaiagentcore.http.CustomHttpClientBuilder;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@ConditionalOnProperty(name = "documentvalidationagent.ai.aiType", havingValue = "VLLM")
//public class VLLMChatModelConfiguration extends ChatModelConfiguration {
//    @Bean
//    @Override
//    public ChatModel getModel() {
//        HttpClient.Builder client = HttpClient.newBuilder()
//                .version(HttpClient.Version.HTTP_1_1);
//        CustomHttpClientBuilder customHttpClientBuilder = CustomHttpClient.builder().httpClientBuilder(client);
//        return OpenAiChatModel.builder().httpClientBuilder(customHttpClientBuilder)
//                .modelName(modelName)
//                .baseUrl(apiUrl)
//                .customHeaders(Map.of("Authorization", "Bearer " + apiKey))
//                .temperature(temperature)
//                .timeout(Duration.ofMinutes(timeout))
//                .build();
//    }
//}
