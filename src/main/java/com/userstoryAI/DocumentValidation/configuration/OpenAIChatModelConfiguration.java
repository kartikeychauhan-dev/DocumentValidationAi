//package com.userstoryAI.DocumentValidation.configuration;
//
//import java.time.Duration;
//
//import dev.langchain4j.model.chat.ChatModel;
//
//import dev.langchain4j.model.openai.OpenAiChatModel;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
////@ConditionalOnProperty(name = "documentvalidationagent.ai.aiType", havingValue = "OPEN_AI")
//public class OpenAIChatModelConfiguration extends ChatModelConfiguration {
//    @Bean
//    @Override
//    public ChatModel getModel() {
//        return OpenAiChatModel
//                .builder()
//                .modelName(modelName)
//                .baseUrl(apiUrl)
//                .temperature(temperature)
//                .timeout(Duration.ofMinutes(timeout))
//                .build();
//    }
//}
