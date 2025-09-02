package com.userstoryAI.DocumentValidation.service;

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
import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.filter.MetadataFilterBuilder;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Points;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;


@Service
public class ValidationServiceImpl implements ValidationService {
//	VectorStore vectorStore;
//	ValidationServiceImpl(VectorStore vectorStore) {
//		this.vectorStore = vectorStore;
//	}
	Logger logger = LoggerFactory.getLogger(ValidationServiceImpl.class);
	private TextReader textReader;
	private ChatModel chatModel;
	private EmbeddingStore<TextSegment> embeddingStore;
	private EmbeddingModel embeddingModel;

//	private final VectorStore vectorStore;
	private QdrantClient qdrantClient;

	ValidationServiceImpl(TextReader textReader, EmbeddingModel embeddingModel,ChatModel chatModel, EmbeddingStore<TextSegment> embeddingStore,QdrantClient qdrantClient) {
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
		Path path = new File(resource.getPath()).toPath();
		ExtractText extractText = textReader.readRequirements(path.toFile().getAbsolutePath());
		UserMessage userMessage = new UserMessage(extractText.content()+ "summarise in points only and don't add any text from yourself" );
		String response = chatModel.chat(userMessage).aiMessage().text();
		logger.info(response);
		Document document = Document.from(
				response,
				Metadata.from(Map.of("doc_name", "dora"))
		);
		DocumentSplitter splitter = new DocumentByParagraphSplitter(250, 50);
//		Map<String, String> metadata = Map.of("doc_name", "dora");
//		TextSegment textSegment = TextSegment.from(response, Metadata.from(metadata));
		List<TextSegment> textSegments = splitter.split(document);
		for (TextSegment textSegment: textSegments) {
			embeddingStore.add(embeddingModel.embed(textSegment).content(), textSegment);
		}


		return false;
	}
}
