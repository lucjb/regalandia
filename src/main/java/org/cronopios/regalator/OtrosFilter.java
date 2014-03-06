package org.cronopios.regalator;

import java.util.List;

import org.cronopios.regalator.ml.MLCategory;

import com.google.common.collect.Lists;

public class OtrosFilter {

	public void filter(List<MLCategory> categories) {
		System.out.println("Otros filter received " + categories.size() + " categories.");
		List<MLCategory> toRemove = Lists.newArrayList();
		for (MLCategory mlCategory : categories) {
			if (mlCategory.isFor("Otros") || mlCategory.isFor("Otras")) {
				toRemove.add(mlCategory);
			}
		}
		for (MLCategory mlCategory : toRemove) {
			System.out.println(mlCategory);
		}
		categories.removeAll(toRemove);
		System.out.println("Otros filter filtered " + toRemove.size() + " categories. " + categories.size() + " retained.");
	}

}
