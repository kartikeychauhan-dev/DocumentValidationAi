package com.userstoryAI.DocumentValidation.configuration;

import com.userstoryAI.DocumentValidation.validation.ValidationMessages;
import dev.langchain4j.model.chat.ChatModel;
//import io.github.adamw7.genaiagentcore.model.AiType;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
public abstract class ChatModelConfiguration {
	@Value("${documentvalidationagent.ai.apiKey}")
	@NotBlank(message = ValidationMessages.AI_API_KEY)
	public String apiKey;
	@Value("${documentvalidationagent.ai.modelName:#{null}}")
	public String modelName;
//	@Value("${documentvalidationagent.ai.aiType}")
//	public AiType aiType;
	@Value("${documentvalidationagent.ai.timeout.inMins}")
	public long timeout;
	@Value("${documentvalidationagent.ai.apiUrl:#{null}}")
	public String apiUrl;
	@Value("${documentvalidationagent.ai.temperature}")
	public double temperature;

	@Value("${documentvalidationagent.ai.embeddingModel}")
	public String embeddingModel;

	public abstract ChatModel getModel();
}
