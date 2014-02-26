package org.cronopios.regalator.ml.brands;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.cronopios.regalator.ml.MLCategory;

import com.google.common.collect.Lists;

public class BrandFilter {

	private Set<String> vocabulary;

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
				if (amountOfSuspectedBrands > amountOfChildren * 2d / 3d) {
					removed.addAll(children);
					children.clear();
				}
			}
		}

		categories.removeAll(removed);
		for (MLCategory mlCategory : removed) {
			System.out.println(mlCategory);
		}
	}

	public boolean suspectedBrand(MLCategory mlCategory) {
		String name = mlCategory.getName();
		String lowerCase = name.toLowerCase();
		String[] split = lowerCase.split(" ");
		boolean isBrand = false;
		for (String string : split) {
			boolean inVocabulary = this.getVocabulary().contains(string);
			if (!inVocabulary) {
				isBrand = true;
				System.out.println(string);
			}
		}
		return isBrand;
	}

	public Set<String> getVocabulary() {
		return vocabulary;
	}

	public void setVocabulary(Set<String> vocabulary) {
		this.vocabulary = vocabulary;
	}

}
