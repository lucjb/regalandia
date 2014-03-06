package org.cronopios.regalator.ml.brands;

import java.util.List;
import java.util.Set;

import org.cronopios.regalator.ml.MLCategory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class FlagBasedBrandFilter {

	private Set<String> brands = Sets.newHashSet();

	public void filterBrands(List<MLCategory> categories) {
		List<MLCategory> removed = Lists.newArrayList();
		for (MLCategory mlCategory : categories) {
			if (otrasMarcasAmongChildren(mlCategory.getChildren_categories())) {
				removed.addAll(mlCategory.getChildren_categories());
				this.registerBrandStrings(mlCategory.getChildren_categories());
				mlCategory.getChildren_categories().clear();
			}
		}
		for (MLCategory mlCategory : categories) {
			for (String brandString : this.getBrands()) {
				if (mlCategory.isFor(brandString)) {
					removed.add(mlCategory);
					mlCategory.getParent().getChildren_categories()
							.remove(mlCategory);
				}
			}
		}

		categories.removeAll(removed);
		System.out.println(this.getClass() + " filtered " + removed.size()
				+ " categories. Found " + this.getBrands().size()
				+ " brand strings.");

	}

	private void registerBrandStrings(Set<MLCategory> children) {
		for (MLCategory child : children) {
			this.getBrands().add(child.getName());
		}
	}

	private boolean otrasMarcasAmongChildren(Set<MLCategory> children) {
		for (MLCategory child : children) {
			if (child.getName().equals("Otras Marcas")) {
				return true;
			}
		}
		return false;
	}

	public Set<String> getBrands() {
		return brands;
	}

	public void setBrands(Set<String> brands) {
		this.brands = brands;
	}

}
