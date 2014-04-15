package org.cronopios.regalator.ml;

import java.util.Collection;

import org.cronopios.regalator.filters.AbstractCanonicalCategoryFilter;

import com.google.common.collect.Lists;

public class MLTagsFilter extends AbstractCanonicalCategoryFilter<MLCategory> {
	private String tag;

	/**
	 * [others] [male] [female] [girl] [boy] [groupby] carousel acc selftitle baby
	 * 
	 * @param tag
	 */
	public MLTagsFilter(String tag) {
		this.setTag(tag);
	}

	@Override
	public Collection<? extends MLCategory> selectCategoriesToRemove(Collection<? extends MLCategory> mlCategories) {
		Collection<MLCategory> toRemove = Lists.newLinkedList();
		for (MLCategory mlCategory : mlCategories) {
			if (mlCategory.getSettings().getTags().contains(this.getTag())) {
				toRemove.add(mlCategory);
			}
		}
		return toRemove;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

}
