package org.cronopios.regalator.ml.brands;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.cronopios.regalator.ml.MLCategory;

public class MLCategoryFilter {

	public void filter(Collection<MLCategory> categories) {
		Collection<MLCategory> categoriesToRemove = this
				.selectCategoriesToRemove(categories);

		categories.removeAll(categoriesToRemove);
		for (MLCategory mlCategory : categoriesToRemove) {
			MLCategory parent = mlCategory.getParent();
			if (parent != null) {
				Set<MLCategory> children_categories = parent
						.getChildren_categories();
				children_categories.remove(mlCategory);
			}
		}

		System.out.println(this.getClass() + " filtered "
				+ categoriesToRemove.size() + " categories.");

	}

	public Collection<MLCategory> selectCategoriesToRemove(
			Collection<MLCategory> categories) {
		return Collections.EMPTY_LIST;
	}

}
