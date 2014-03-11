package org.cronopios.regalator.filters;

import java.util.Collection;
import java.util.Collections;

import org.cronopios.regalator.CanonicalCategory;

public class AbstractCanonicalCategoryFilter {

	public void filter(Collection<? extends CanonicalCategory> categories) {
		Collection<CanonicalCategory> categoriesToRemove = this
				.selectCategoriesToRemove(categories);

		categories.removeAll(categoriesToRemove);
		for (CanonicalCategory category : categoriesToRemove) {
			CanonicalCategory parent = category.getParent();
			if (parent != null) {
				Collection<? extends CanonicalCategory> children_categories = parent
						.getChildren();
				children_categories.remove(category);
			}
		}

		System.out.println(this.getClass() + " filtered "
				+ categoriesToRemove.size() + " categories.");

	}

	public Collection<CanonicalCategory> selectCategoriesToRemove(
			Collection<? extends CanonicalCategory> categories) {
		return Collections.EMPTY_LIST;
	}

}
