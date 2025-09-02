package com.userstoryAI.DocumentValidation.fileReader;

import java.io.File;
import java.io.IOException;

import com.userstoryAI.DocumentValidation.model.ExtractText;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

@Service
public class TextExtractorPDF implements TextReader{

	public ExtractText readRequirements(String path) {
		File file = new File(path);
		String text;
		PDDocument document;
		try {
			document = Loader.loadPDF(file);
			PDFTextStripper stripper = new PDFTextStripper();
			text = stripper.getText(document);
			document.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return new ExtractText(text);
	}
}
