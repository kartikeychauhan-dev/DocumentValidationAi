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
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import io.qdrant.client.QdrantClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;
import java.util.Map;


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
    public boolean validateContent(String value) throws FileNotFoundException {
        URL resource = getClass().getResource("/docs/DORA.pdf");
        URL brd = getClass().getResource("/docs/brd.pdf");
        ExtractText extractText = textReader.readRequirements(resource.getPath());
        ExtractText extractBrdText = textReader.readRequirements(brd.getPath());
		UserMessage userMessage = new UserMessage(extractText.content()+ " summarize with regulations in points and don't add any extra text" );
		String response = chatModel.chat(userMessage).aiMessage().text();
		logger.info(response);
        Document document = Document.from(
                response,
                Metadata.from(Map.of("doc_name", "dora"))
        );
        DocumentSplitter splitter = new DocumentByParagraphSplitter(250, 50);
        List<TextSegment> textSegments = splitter.split(document);

        for (TextSegment textSegment : textSegments) {
            embeddingStore.add(embeddingModel.embed(textSegment).content(), textSegment);
        }
		Embedding queryEmbedding = embeddingModel.embed(extractText.content()).content();
		EmbeddingSearchRequest embeddingSearchRequest = new EmbeddingSearchRequest(queryEmbedding,5,0.8,null);
		EmbeddingSearchResult<TextSegment> a = embeddingStore.search(embeddingSearchRequest);
		logger.info(a.toString());

        return false;
    }
}
