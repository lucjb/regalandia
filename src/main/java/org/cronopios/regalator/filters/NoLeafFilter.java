package org.cronopios.regalator.filters;

import java.util.Collection;
import java.util.Iterator;

import org.cronopios.regalator.CanonicalCategory;

public class NoLeafFilter {

	public void filter(Collection<? extends CanonicalCategory> categories) {
		int i = 0;
		for (Iterator<? extends CanonicalCategory> iterator = categories.iterator(); iterator.hasNext();) {
			CanonicalCategory mlCategory = iterator.next();
			if (!mlCategory.isLeaf()) {
				i++;
				iterator.remove();
			}
		}
		System.out.println("NoLeafFilter filtered " + i + " categories.");
	}
}
