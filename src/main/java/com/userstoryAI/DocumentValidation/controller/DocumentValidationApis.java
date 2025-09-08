package com.userstoryAI.DocumentValidation.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import com.userstoryAI.DocumentValidation.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DocumentValidationApis {

	@Autowired
	private ValidationService validationService;

	@GetMapping("/validate")
	public ResponseEntity<String> validate() throws IOException, ExecutionException, InterruptedException, URISyntaxException {
			boolean isValid = validationService.validateContent("test");
		return ResponseEntity.ok().body("Valid :"+isValid);
	}
}
