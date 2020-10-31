package com.tiagods.toolgfip;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ToolGfipApplicationTests {

	@Test
	void contextLoads() {
		String regex = "(^[\\d]{4})+[^\\d].*";
		String fileName = "2437_022018_Protocolo.pdf";
		Assertions.assertTrue(fileName.matches(regex));

		Matcher matcher = Pattern.compile("([\\d]{4})").matcher(fileName);
		if (matcher.find()) {
			System.out.println(matcher.group());
		}
	}

	@Test
	void printText() throws IOException {
		PDDocument document = PDDocument.load(new File("c:/Temp/2444_.pdf"));
		PDFTextStripper stripper = new PDFTextStripper();
		String text = stripper.getText(document);

		Matcher matcher = Pattern.compile("\\s(\\d{2}\\/\\d{4})\\s").matcher(text);
		if(matcher.find()) {
			String valor = matcher.group().trim();
			System.out.println(valor);
			valor = "01/"+valor;
			LocalDate localDate = LocalDate.from(DateTimeFormatter.ofPattern("dd/MM/yyyy")
					.parse(valor));
			System.out.println(localDate);
		}
	}
	@Test
	void converterDate(){
		String text= "23/13/2020";
		try {
			LocalDate localDate = LocalDate.from(DateTimeFormatter.ofPattern("dd/MM/yyyy")
					.parse(text));
			System.out.println(localDate);
			Assertions.fail();
		}catch (DateTimeParseException ex){
		}
	}

}
