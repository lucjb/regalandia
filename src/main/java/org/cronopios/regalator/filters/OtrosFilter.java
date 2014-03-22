package org.cronopios.regalator.filters;

import java.util.Collection;
import java.util.List;

import org.cronopios.regalator.CanonicalCategory;

import com.google.common.collect.Lists;

public class OtrosFilter extends AbstractCanonicalCategoryFilter<CanonicalCategory> {

	@Override
	public Collection<? extends CanonicalCategory> selectCategoriesToRemove(Collection<? extends CanonicalCategory> categories) {
		List<CanonicalCategory> toRemove = Lists.newArrayList();
		for (CanonicalCategory category : categories) {
			if (category.isFor("Otros") || category.isFor("Otras")) {
				toRemove.add(category);
			}
		}
		return toRemove;
	}

}
