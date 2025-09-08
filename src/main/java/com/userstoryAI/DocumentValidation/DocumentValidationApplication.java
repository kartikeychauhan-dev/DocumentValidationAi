package com.userstoryAI.DocumentValidation;

import com.userstoryAI.DocumentValidation.fileReader.TextReader;
import com.userstoryAI.DocumentValidation.model.ExtractText;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URL;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class DocumentValidationApplication implements ApplicationRunner {
    Logger logger = LoggerFactory.getLogger(DocumentValidationApplication.class);
    @Value("${agent.vectorstore.qdrant.collection-name}")
    private String collectionName;
    QdrantClient qdrantClient;
    TextReader textReader;
    ChatModel chatModel;
    EmbeddingModel embeddingModel;
    EmbeddingStore<TextSegment> embeddingStore;

    public DocumentValidationApplication(QdrantClient qdrantClient, TextReader textReader, ChatModel chatModel, EmbeddingModel embeddingModel, EmbeddingStore<TextSegment> embeddingStore) {
        this.qdrantClient = qdrantClient;
        this.textReader = textReader;
        this.chatModel = chatModel;
        this.embeddingModel = embeddingModel;
        this.embeddingStore = embeddingStore;
    }

    public static void main(String[] args) {
        SpringApplication.run(DocumentValidationApplication.class, args);
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<String> existingCollections = qdrantClient.listCollectionsAsync().get();
        if (!existingCollections.contains(collectionName)) {
            Collections.CreateCollection createCollection = getCollectionCreated();
            qdrantClient.createCollectionAsync(createCollection);
            logger.info("Collection : " + collectionName + " created!!");
            URL resource = getClass().getResource("/docs/DORA.pdf");
            ExtractText extractText = textReader.readRequirements(resource.getPath());
            UserMessage userMessage = new UserMessage(extractText.content()+ " summarize with detail regulations in points and don't add any extra text" );
            String response = chatModel.chat(userMessage).aiMessage().text();
            logger.info(response);
//            TextSegment ruleSegment = new TextSegment(
//                    response,
//                    Metadata.from(Map.of(
//                            "ruleType", "semantic",
//                            "description", "Text must be GDPR-related and suitable for a PDF",
//                            "documentType", "DORA"
//                    ))
//            );
//            DocumentSplitter splitter = new DocumentByParagraphSplitter(250, 50);
//            Document document = new Document(List.of(ruleSegment));

            Document document = Document.from(
                    response,
                    Metadata.from(Map.of("doc_name", "dora"))
            );
//            DocumentSplitter splitter = new DocumentByParagraphSplitter(250, 50);
//            List<TextSegment> textSegments = splitter.split(document);

//            for (TextSegment textSegment : textSegments) {
                embeddingStore.add(embeddingModel.embed(document.toTextSegment()).content(), document.toTextSegment());
//            }
                logger.info("Rules inserted");
        } else {
            logger.info("Collection : " + collectionName + " already exists!!");
        }

    }
    public Collections.CreateCollection getCollectionCreated() {
        return  Collections.CreateCollection.newBuilder()
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
    }
}
