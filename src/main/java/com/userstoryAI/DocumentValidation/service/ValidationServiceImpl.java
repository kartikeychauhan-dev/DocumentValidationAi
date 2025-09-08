package com.userstoryAI.DocumentValidation.service;

import com.userstoryAI.DocumentValidation.fileReader.TextReader;
import com.userstoryAI.DocumentValidation.model.ExtractText;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QueryFactory;
import io.qdrant.client.grpc.Points;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


@Service
public class ValidationServiceImpl implements ValidationService {

    Logger logger = LoggerFactory.getLogger(ValidationServiceImpl.class);
    private TextReader textReader;
    private ChatModel chatModel;
    private EmbeddingStore<TextSegment> embeddingStore;
    private EmbeddingModel embeddingModel;
    private QdrantClient qdrantClient;

    ValidationServiceImpl(TextReader textReader, EmbeddingModel embeddingModel, ChatModel chatModel, EmbeddingStore<TextSegment> embeddingStore, QdrantClient qdrantClient) {
        this.textReader = textReader;
        this.chatModel = chatModel;
        this.embeddingStore = embeddingStore;
        this.embeddingModel = embeddingModel;
        this.qdrantClient = qdrantClient;
//		this.vectorStore = vectorStore;
    }



    @Override
    public boolean validateContent(String value) throws IOException, ExecutionException, InterruptedException, URISyntaxException {
        URL brd = getClass().getResource("/docs/brd.txt");
//        ExtractText extractBrdText = textReader.readRequirements(brd.getPath());
        Path path = Paths.get(brd.toURI());

        ExtractText extractBrdText = new ExtractText( Files.readString(path));
        Embedding inputEmbedding = embeddingModel.embed(extractBrdText.content()).
                content();
//        EmbeddingSearchRequest searchRequest = new EmbeddingSearchRequest(inputEmbedding, 1, 0.8, null);
//        EmbeddingSearchResult<TextSegment> result = embeddingStore.search(searchRequest);
//        logger.info(result.matches().toString());

        float[] queryEmbedding = inputEmbedding.vector();
        Points.QueryPoints query = Points.QueryPoints.newBuilder()
                .setCollectionName("vector_store")
                .setQuery(QueryFactory.nearest(queryEmbedding))
                .setLimit(10)
                .build();
        var response = qdrantClient.queryAsync(query).get();

        boolean isValid = false;
        Points.ScoredPoint a=  response.get(0);
//        if (!result.matches().isEmpty()) {
//            EmbeddingMatch<TextSegment> match = result.matches().get(0);
//            double score = match.score();
//            TextSegment matchedRule = match.embedded();

            if (a.getScore() >= 0.8) {
                logger.info("Text is valid according to rule: " + a.toString());
//                logger.info("Matched rule metadata: " + matchedRule.metadata());
                isValid= true;
            } else {
                logger.info("Text does not meet semantic rule (score too low)");
            }
//        } else {
//            logger.info(" No validation rule matched.");
//        }
//        qdrantClient.deleteCollectionAsync("vector")
        return isValid;
    }
}
