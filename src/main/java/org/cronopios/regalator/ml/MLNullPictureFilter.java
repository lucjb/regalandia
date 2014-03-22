package org.cronopios.regalator.ml;

import java.util.Collection;

import org.cronopios.regalator.filters.AbstractCanonicalCategoryFilter;

import com.google.common.collect.Lists;

public class MLNullPictureFilter extends AbstractCanonicalCategoryFilter<MLCategory> {

	@Override
	public Collection<? extends MLCategory> selectCategoriesToRemove(Collection<? extends MLCategory> mlCategories) {
		Collection<MLCategory> toRemove = Lists.newLinkedList();
		for (MLCategory mlCategory : mlCategories) {

			if (mlCategory.getImageURL() == null) {
				toRemove.add(mlCategory);
			}
		}
		return toRemove;
	}

}
