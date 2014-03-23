package org.cronopios.regalator.filters;

import java.util.Collection;
import java.util.Collections;
import java.util.Stack;

import org.cronopios.regalator.CanonicalCategory;

import com.google.common.collect.Sets;

public class AbstractCanonicalCategoryFilter<T extends CanonicalCategory> {

	public void filter(Collection<? extends T> categories) {
		Collection<? extends T> categoriesToRemove = this.selectCategoriesToRemove(categories);

		Stack<CanonicalCategory> stack = new Stack<CanonicalCategory>();
		for (CanonicalCategory t : categoriesToRemove) {
			stack.push(t);
		}
		Collection<CanonicalCategory> allCategoriesToRemove = Sets.newHashSet();
		while (!stack.isEmpty()) {
			CanonicalCategory pop = stack.pop();
			allCategoriesToRemove.add(pop);
			for (CanonicalCategory canonicalCategory : pop.getChildren()) {
				stack.push(canonicalCategory);
			}
		}

		categories.removeAll(allCategoriesToRemove);
		for (CanonicalCategory category : allCategoriesToRemove) {
			CanonicalCategory parent = category.getParent();
			if (parent != null) {
				Collection<? extends CanonicalCategory> children_categories = parent.getChildren();
				children_categories.remove(category);
			}
		}

		System.out.println(this.getClass() + " filtered " + allCategoriesToRemove.size() + " categories.");

	}

	public Collection<? extends T> selectCategoriesToRemove(Collection<? extends T> categories) {
		return Collections.EMPTY_LIST;
	}

}
