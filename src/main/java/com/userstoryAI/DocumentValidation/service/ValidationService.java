package com.userstoryAI.DocumentValidation.service;

import java.io.FileNotFoundException;

public interface ValidationService {
	boolean validateContent(String value) throws FileNotFoundException;
}
