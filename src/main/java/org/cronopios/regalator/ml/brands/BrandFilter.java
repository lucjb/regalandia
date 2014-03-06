package org.cronopios.regalator.ml.brands;

import java.text.Normalizer;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.cronopios.regalator.DefaultVocabulary;
import org.cronopios.regalator.Vocabulary;
import org.cronopios.regalator.ml.MLCategory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class BrandFilter {

	private Vocabulary vocabulary;
	private Set<String> brands = Sets.newHashSet();

	public BrandFilter(Set<String> vocabulary) {
		this.setVocabulary(new DefaultVocabulary(vocabulary));
	}

	public void filterBrands(List<MLCategory> categories) {
		List<MLCategory> removed = this.doFilter(categories);
		for (MLCategory mlCategory : removed) {
			System.out.println(mlCategory);
		}
		System.out.println(removed.size());

	}

	private List<MLCategory> doFilter(List<MLCategory> categories) {
		List<MLCategory> removed = Lists.newArrayList();
		for (Iterator<MLCategory> iterator = categories.iterator(); iterator.hasNext();) {
			MLCategory mlCategory = iterator.next();
			Set<MLCategory> children = mlCategory.getChildren_categories();
			if (!mlCategory.isLeaf()) {
				if (otrasMarcasAmongChildren(children)) {
					registerBrandStrings(children);
				}
			}
		}

		for (MLCategory mlCategory : categories) {
			for (String brandString : this.getBrands()) {
				if (mlCategory.isFor(brandString)) {
					removed.add(mlCategory);
				}
			}

		}

		// for (Iterator<MLCategory> iterator = categories.iterator();
		// iterator.hasNext();) {
		// MLCategory mlCategory = iterator.next();
		// Set<MLCategory> children = mlCategory.getChildren_categories();
		// if (!mlCategory.isLeaf()) {
		// int amountOfSuspectedBrands = 0;
		// for (MLCategory child : children) {
		// if (registeredAsBrand(child)) {
		// amountOfSuspectedBrands++;
		// }
		// }
		// if (amountOfSuspectedBrands > 2d) {
		// brandLevelFound(removed, children);
		// }
		// }
		// }

		categories.removeAll(removed);
		return removed;
	}

	private void brandLevelFound(List<MLCategory> removed, Set<MLCategory> children) {
		registerBrandStrings(children);
		removed.addAll(children);
		children.clear();
	}

	private void registerBrandStrings(Set<MLCategory> children) {
		for (MLCategory child : children) {
			this.getBrands().add(child.getName());
		}
	}

	private boolean levelSuspectedToBeBrands(Set<MLCategory> children) {
		int amountOfSuspectedBrands = 0;
		for (MLCategory child : children) {
			if (suspectedBrand(child)) {
				amountOfSuspectedBrands++;
			}
		}
		return amountOfSuspectedBrands > 2;
	}

	private boolean otrasMarcasAmongChildren(Set<MLCategory> children) {
		for (MLCategory child : children) {
			if (child.getName().equals("Otras Marcas")) {
				return true;
			}
		}
		return false;
	}

	private boolean registeredAsBrand(MLCategory child) {
		return this.getBrands().contains(child.getName());
	}

	Set<String> suspects = Sets.newHashSet();

	public boolean suspectedBrand(MLCategory mlCategory) {
		String name = mlCategory.getName();
		String lowerCase = normalized(name);
		String[] split = lowerCase.split(", *| +");
		for (String string : split) {
			String trim = string.trim();
			boolean inVocabulary = this.getVocabulary().contains(trim);
			if (!inVocabulary) {
				suspects.add(trim);
				return true;
			}
		}
		return false;
	}

	private String normalized(String name) {
		String lowerCase = name.toLowerCase();
		return lowerCase;
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

	public Set<String> getBrands() {
		return brands;
	}

	public void setBrands(Set<String> brands) {
		this.brands = brands;
	}

	public Vocabulary getVocabulary() {
		return vocabulary;
	}

	public void setVocabulary(Vocabulary vocabulary) {
		this.vocabulary = vocabulary;
	}

}
