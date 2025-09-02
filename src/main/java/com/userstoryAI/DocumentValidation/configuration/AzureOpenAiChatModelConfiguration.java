package com.userstoryAI.DocumentValidation.configuration;

//import dev.langchain4j.model.azure.AzureOpenAiChatModel;
//import dev.langchain4j.model.azure.AzureOpenAiEmbeddingModel;
//import dev.langchain4j.model.chat.ChatModel;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@ConditionalOnProperty(name = "documentvalidationagent.ai.aiType", havingValue = "AZURE_OPEN_AI")
//public class AzureOpenAiChatModelConfiguration extends ChatModelConfiguration {
//
//    @Bean
//    public ChatModel getModel() {
//        return AzureOpenAiChatModel.builder()
//                .apiKey(apiKey)
//                .endpoint(apiUrl)
//                .temperature(temperature)
//                .deploymentName(deploymentName)
//                .build();
//    }
//
//}
