package com.userstoryAI.DocumentValidation.configuration;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QdrantConfiguration {

	@Value("${agent.vectorstore.qdrant.host}")
	private String hostname;
	@Value("${agent.vectorstore.qdrant.port}")
	private int port;
	@Value("${agent.vectorstore.qdrant.api-key}")
	private String apiKey;
	@Value("${agent.vectorstore.qdrant.use-tls}")
	private boolean usetls;
	@Value("${agent.vectorstore.qdrant.initialize-schema}")
	private boolean initializeSchema;
	@Value("${agent.vectorstore.qdrant.collection-name}")
	private String collectionName;

	@Bean
	public QdrantClient qdrantClient() {
		QdrantGrpcClient.Builder grpcClientBuilder =
				QdrantGrpcClient.newBuilder(
								hostname
								, port
								, usetls)
						.withApiKey(apiKey);

		return new QdrantClient(grpcClientBuilder.build());
	}

	@Bean
	public EmbeddingStore<TextSegment> embeddingStore(EmbeddingModel embeddingModel) {
		return QdrantEmbeddingStore.builder()
				.host(hostname)
				.port(port)
				.apiKey(apiKey)
				.collectionName(collectionName)
				.useTls(usetls)
				.client(qdrantClient())
				.build();
	}

}
