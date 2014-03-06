package org.cronopios.regalator;

import java.util.Collection;
import java.util.List;

import org.cronopios.regalator.ml.MLCategory;
import org.cronopios.regalator.ml.brands.MLCategoryFilter;

import com.google.common.collect.Lists;

public class OtrosFilter extends MLCategoryFilter {

	@Override
	public Collection<MLCategory> selectCategoriesToRemove(
			Collection<MLCategory> categories) {
		List<MLCategory> toRemove = Lists.newArrayList();
		for (MLCategory mlCategory : categories) {
			if (mlCategory.isFor("Otros") || mlCategory.isFor("Otras")) {
				toRemove.add(mlCategory);
			}
		}
		return toRemove;
	}

}
