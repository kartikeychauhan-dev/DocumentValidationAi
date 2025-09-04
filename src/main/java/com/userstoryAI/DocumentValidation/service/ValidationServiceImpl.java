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
import io.qdrant.client.grpc.Points;
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
        URL brd = getClass().getResource("/docs/brd.pdf");
        ExtractText extractBrdText = textReader.readRequirements(brd.getPath());
        Embedding inputEmbedding = embeddingModel.embed(" Business Requirements Document (BRD) Project Name: AI Chatbot Integration with Local Ollama Author: [Your Name] Date: 2025-09-04 Version: 1.0 Status: Draft 1. Executive Summary This project aims to integrate a Java-based backend with a locally hosted Ollama LLM (Large Language Model) instance. The integration will enable secure, fast, and offline access to AI capabilities without relying on external APIs like OpenAI or Anthropic. 2. Business Objectives Reduce dependency on external AI providers. Improve latency and data privacy by using local models. Provide users with intelligent chatbot functionality embedded in the application. 3. Background / Problem Statement Current implementation relies on remote AI APIs, which: Introduce latency and potential downtime. Expose sensitive user data to third-party services. Incur ongoing subscription costs. By integrating with a local Ollama instance, we can improve performance, privacy, and cost control. 4. Scope ✅ In Scope: Connecting the Java backend (Spring Boot) to Ollama's local API. Sending and receiving prompts/responses via HTTP. Logging and monitoring retry attempts and failures. Basic error handling and fallback strategy. ❌ Out of Scope: Training custom models in Ollama. UI/UX changes (this phase is backend-only). Scaling for concurrent multi-user support (future phase). 5. Business Requirements ID Requirement Priority Description BR-001 Local LLM Integration High The backend must connect to a local Ollama instance via HTTP. BR-002 Retry Logic Medium Retries must be logged when a connection to Ollama fails. BR-003 Configuration Support High Connection settings (URL, port, timeouts) must be configurable. BR-004 Response Time Medium The API must return responses within 1s under normal load. BR-005 Error Reporting High Failed connections must be logged with stack trace. 6. Stakeholders Name Role Responsibility Jane Doe Product Owner Define business needs, approve final integration. Dev Team Engineers Implement and test Ollama integration. QA Team Testers Validate functional and non-functional requirements. 7. Assumptions Ollama is properly installed and running on localhost. Java backend is allowed to make HTTP calls to localhost. Models are already loaded and accessible via API. 8. Constraints Ollama must be running at the time of connection; otherwise, errors will be logged and retried. Local deployment only; not designed for cloud integration in this phase. 9. Dependencies Ollama (must be running and accessible) LangChain4j Java SDK Spring Boot HTTP configuration Local network access 10. Acceptance Criteria ✅ Application connects successfully to Ollama and retrieves a valid response. ✅ All failed attempts are logged and retried up to 3 times. ✅ All connection parameters can be changed via environment variables or application.yml.").content();
        EmbeddingSearchRequest searchRequest = new EmbeddingSearchRequest(inputEmbedding, 1, 0.8, null);
        EmbeddingSearchResult<TextSegment> result = embeddingStore.search(searchRequest);
        logger.info(result.matches().toString());
        boolean isValid = false;
        if (!result.matches().isEmpty()) {
            EmbeddingMatch<TextSegment> match = result.matches().get(0);
            double score = match.score();
            TextSegment matchedRule = match.embedded();

            if (score >= 0.8) {
                logger.info("Text is valid according to rule: " + matchedRule.text());
                logger.info("Matched rule metadata: " + matchedRule.metadata());
            } else {
                logger.info("Text does not meet semantic rule (score too low)");
            }
        } else {
            logger.info(" No validation rule matched.");
        }
        return isValid;
    }
}
