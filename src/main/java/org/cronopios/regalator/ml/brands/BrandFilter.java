package org.cronopios.regalator.ml.brands;

import java.text.Normalizer;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.cronopios.regalator.ml.MLCategory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class BrandFilter {

	private Set<String> vocabulary;
	private Set<String> brands = Sets.newHashSet();

	public BrandFilter(Set<String> vocabulary) {
		this.setVocabulary(vocabulary);
	}

	public void filterBrands(List<MLCategory> categories) {
		List<MLCategory> removed = Lists.newArrayList();
		for (Iterator<MLCategory> iterator = categories.iterator(); iterator.hasNext();) {
			MLCategory mlCategory = iterator.next();
			Set<MLCategory> children = mlCategory.getChildren_categories();
			if (!children.isEmpty() && children.iterator().next().isLeaf()) {
				int amountOfChildren = children.size();
				int amountOfSuspectedBrands = 0;
				for (MLCategory child : children) {
					if (suspectedBrand(child)) {
						amountOfSuspectedBrands++;
					}
				}
				if (amountOfSuspectedBrands > amountOfChildren * 1d / 2d) {
					for (MLCategory child : children) {
						this.getBrands().add(child.getName());
					}
					removed.addAll(children);
					children.clear();
				}
			}
		}

		for (Iterator<MLCategory> iterator = categories.iterator(); iterator.hasNext();) {
			MLCategory mlCategory = iterator.next();
			Set<MLCategory> children = mlCategory.getChildren_categories();
			if (!children.isEmpty() && children.iterator().next().isLeaf()) {
				int amountOfChildren = children.size();
				int amountOfSuspectedBrands = 0;
				for (MLCategory child : children) {
					if (registeredAsBrand(child)) {
						amountOfSuspectedBrands++;
					}
				}
				if (amountOfSuspectedBrands > amountOfChildren * 1d / 2d) {
					removed.addAll(children);
					children.clear();
				}
			}
		}

		for (MLCategory mlCategory : removed) {
			System.out.println(mlCategory);
		}
		categories.removeAll(removed);

	}

	private boolean registeredAsBrand(MLCategory child) {
		return this.getBrands().contains(child.getName());
	}

	public boolean suspectedBrand(MLCategory mlCategory) {
		String name = mlCategory.getName();
		String lowerCase = normalized(name);
		String[] split = lowerCase.split(" ");
		boolean isBrand = false;
		for (String string : split) {
			boolean inVocabulary = this.getVocabulary().contains(string);
			if (!inVocabulary) {
				isBrand = true;
			}
		}
		return isBrand;
	}

	private String normalized(String name) {
		String lowerCase = name.toLowerCase();
		String flattenToAscii = flattenToAscii(lowerCase);
		return flattenToAscii;
	}

	public static String flattenToAscii(String string) {
		StringBuilder sb = new StringBuilder(string.length());
		string = Normalizer.normalize(string, Normalizer.Form.NFD);
		for (char c : string.toCharArray()) {
			if (c <= '\u007F')
				sb.append(c);
		}
		return sb.toString();
	}

	public Set<String> getVocabulary() {
		return vocabulary;
	}

	public void setVocabulary(Set<String> vocabulary) {
		this.vocabulary = vocabulary;
	}

	public Set<String> getBrands() {
		return brands;
	}

	public void setBrands(Set<String> brands) {
		this.brands = brands;
	}

}
