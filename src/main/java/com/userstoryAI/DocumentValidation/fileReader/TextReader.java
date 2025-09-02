package com.userstoryAI.DocumentValidation.fileReader;

import java.io.FileNotFoundException;

import com.userstoryAI.DocumentValidation.model.ExtractText;

public interface TextReader {
	public ExtractText readRequirements(String path) throws FileNotFoundException;
}
