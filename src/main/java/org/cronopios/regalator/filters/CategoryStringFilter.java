package org.cronopios.regalator.filters;

import java.util.Collection;

import org.cronopios.regalator.CanonicalCategory;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

public class CategoryStringFilter extends AbstractCanonicalCategoryFilter<CanonicalCategory> implements Predicate<CanonicalCategory> {

	String[] namesInPath;

	public CategoryStringFilter(String... namesInPath) {
		this.setNamesInPath(namesInPath);
	}

	@Override
	public Collection<CanonicalCategory> selectCategoriesToRemove(Collection<? extends CanonicalCategory> categories) {
		Collection<CanonicalCategory> selection = Lists.newArrayList();

		for (CanonicalCategory category : categories) {
			if (this.allNamesInPath(category)) {
				selection.add(category);
			}
		}
		return selection;
	}

	private boolean allNamesInPath(CanonicalCategory category) {
		for (String name : this.getNamesInPath()) {
			if (!category.isFor(name)) {
				return false;
			}
		}
		return true;
	}

	public String[] getNamesInPath() {
		return namesInPath;
	}

	public void setNamesInPath(String[] namesInPath) {
		this.namesInPath = namesInPath;
	}

	@Override
	public boolean apply(CanonicalCategory input) {
		return this.allNamesInPath(input);
	}

}
