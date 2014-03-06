package org.cronopios.regalator;

import java.io.FileNotFoundException;
import java.util.Set;

import junit.framework.Assert;

import org.cronopios.regalator.ml.brands.VocabularyParser;
import org.junit.Test;

public class VocabularyTest {

	@Test
	public void testContains() throws FileNotFoundException {
		VocabularyParser vocabularyParser = new VocabularyParser();
		Set<String> vocabulary = vocabularyParser.parseVocabulary("vocabulariorae.txt");
		DefaultVocabulary defaultVocabulary = new DefaultVocabulary(vocabulary);
		Assert.assertTrue(defaultVocabulary.contains("artículos".trim()));
	}

	@Test
	public void testSplit() throws Exception {
		String lowerCase = "def,ght, asd xcv";
		String[] split = lowerCase.split(", *| +");
		for (String string : split) {
			System.out.println(string);
		}
	}

}
