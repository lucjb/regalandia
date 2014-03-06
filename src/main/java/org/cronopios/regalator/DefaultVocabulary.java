package org.cronopios.regalator;

import java.util.Arrays;
import java.util.Set;

import com.google.common.collect.Ordering;

public class DefaultVocabulary implements Vocabulary {

	private String[] words;
	private Ordering<String> ordering;

	public DefaultVocabulary(Set<String> words) {
		this.setOrdering(Ordering.<String> natural());
		String[] array = words.toArray(new String[words.size()]);
		Arrays.sort(array, this.getOrdering());
		this.setWords(array);
	}

	@Override
	public boolean contains(String word) {
		int search = Arrays.binarySearch(this.words, word, Ordering.natural());
		return search >= 0;
	}

	public String[] getWords() {
		return words;
	}

	public void setWords(String[] words) {
		this.words = words;
	}

	public Ordering<String> getOrdering() {
		return ordering;
	}

	public void setOrdering(Ordering<String> ordering) {
		this.ordering = ordering;
	}

}
