package org.cronopios.regalator.filters;

import java.util.Collection;
import java.util.Iterator;

import org.cronopios.regalator.CanonicalCategory;

public class NoLeafFilter {

	public void filter(Collection<? extends CanonicalCategory> categories) {
		for (Iterator<? extends CanonicalCategory> iterator = categories
				.iterator(); iterator.hasNext();) {
			CanonicalCategory mlCategory = iterator.next();
			if (!mlCategory.isLeaf()) {
				iterator.remove();
			}
		}
	}

}
