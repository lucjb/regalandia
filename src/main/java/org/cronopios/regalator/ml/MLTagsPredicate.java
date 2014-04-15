package org.cronopios.regalator.ml;

import java.util.List;

import org.cronopios.regalator.CanonicalCategory;

import com.google.common.base.Predicate;

public class MLTagsPredicate implements Predicate<CanonicalCategory> {

	private String tag;

	public MLTagsPredicate(String tag) {
		this.setTag(tag);
	}

	@Override
	public boolean apply(CanonicalCategory x) {
		MLCategory input = (MLCategory) x;
		List<MLCategory> path_from_root = input.getPath_from_root();
		for (MLCategory mlCategory : path_from_root) {
			if (mlCategory.getSettings().getTags().contains(this.getTag())) {
				return true;
			}
		}
		return false;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

}
