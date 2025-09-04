package com.userstoryAI.DocumentValidation;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;

import java.util.List;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
public class DocumentValidationApplication implements ApplicationRunner {
    Logger logger = LoggerFactory.getLogger(DocumentValidationApplication.class);
    @Value("${agent.vectorstore.qdrant.collection-name}")
    private String collectionName;
    QdrantClient qdrantClient;

    public DocumentValidationApplication(QdrantClient qdrantClient) {
        this.qdrantClient = qdrantClient;
    }

    public static void main(String[] args) {
        SpringApplication.run(DocumentValidationApplication.class, args);
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<String> existingCollections = qdrantClient.listCollectionsAsync().get();
        if (!existingCollections.contains(collectionName)) {

            Collections.CreateCollection createCollection = Collections.CreateCollection.newBuilder()
                    .setCollectionName(collectionName)
                    .setVectorsConfig(
                            Collections.VectorsConfig.newBuilder()
                                    .setParams(
                                            Collections.VectorParams.newBuilder()
                                                    .setSize(1024)
                                                    .setDistance(Collections.Distance.Cosine)
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();
            qdrantClient.createCollectionAsync(createCollection);
            logger.info("Collection : " + collectionName + " created!!");
        } else {
            logger.info("Collection : " + collectionName + " already exists!!");
        }
    }
}
