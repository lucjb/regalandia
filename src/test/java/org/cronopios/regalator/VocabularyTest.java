package org.cronopios.regalator;

import java.io.FileNotFoundException;
import java.util.Set;

import junit.framework.Assert;

import org.cronopios.regalator.ml.brands.VocabularyParser;
import org.junit.Test;

public class VocabularyTest {

	
	@Test
	public void testSplit() throws Exception {
		String lowerCase = "def,ght, asd xcv";
		String[] split = lowerCase.split(", *| +");
		for (String string : split) {
			System.out.println(string);
		}
	}

}
