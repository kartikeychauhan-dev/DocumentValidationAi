package com.userstoryAI.DocumentValidation.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

public interface ValidationService {
	boolean validateContent(String value) throws IOException, ExecutionException, InterruptedException, URISyntaxException;
}
