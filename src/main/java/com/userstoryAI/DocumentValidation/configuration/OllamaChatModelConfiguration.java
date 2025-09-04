package com.userstoryAI.DocumentValidation.configuration;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;

import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;

@Configuration
//@ConditionalOnProperty(name = "documentvalidationagent.ai.aiType", havingValue = "OLLAMA")
public class OllamaChatModelConfiguration extends ChatModelConfiguration {

	@Bean
	public ChatModel getModel() {
		return OllamaChatModel
				.builder()
				.modelName(modelName)
				.customHeaders(Map.of("Authorization", "Bearer " + apiKey))
				.baseUrl(apiUrl)
				.temperature(temperature)
				.timeout(Duration.ofMinutes(timeout))
				.build();
	}

	@Bean
	public EmbeddingModel embeddingModel() {
		return OllamaEmbeddingModel.builder()
				.baseUrl(apiUrl)
				.modelName(embeddingModel)
				.customHeaders(Map.of("Authorization", "Bearer " + apiKey)) //not required for local implemenations
				.build();
	}
}
