package com.userstoryAI.DocumentValidation.service;

import io.qdrant.client.QdrantClient;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class QdrantCleanupService {

    private final QdrantClient qdrantClient;
    @Value("${agent.vectorstore.qdrant.collection-name}")
    private String collectionName;

    Logger logger = LoggerFactory.getLogger(QdrantCleanupService.class);

    public QdrantCleanupService(QdrantClient qdrantClient) {
        this.qdrantClient = qdrantClient;
    }

    @PreDestroy
    public void onShutdown() {
        logger.info("shutdown called cleaning up all data");
        qdrantClient.deleteCollectionAsync(collectionName);
    }
}
