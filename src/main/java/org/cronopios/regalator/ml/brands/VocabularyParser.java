package org.cronopios.regalator.ml.brands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

import com.google.common.collect.Sets;

public class VocabularyParser {

	public static void main(String[] args) throws FileNotFoundException {
		VocabularyParser vocabularyParser = new VocabularyParser();
		Set<String> vocabulary = vocabularyParser.parseVocabulary("vocabulariorae.txt");
		for (String word : vocabulary) {
			System.out.println(word);
		}
	}

	public Set<String> parseVocabulary(String fileName) throws FileNotFoundException {
		File file = new File(fileName);
		Set<String> vocabulary = Sets.newLinkedHashSet();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				vocabulary.add(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return vocabulary;
	}
}
