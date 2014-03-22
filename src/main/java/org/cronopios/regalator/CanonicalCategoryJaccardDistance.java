package org.cronopios.regalator;

import java.util.Set;

import com.google.common.collect.Sets;

public class CanonicalCategoryJaccardDistance implements Metric<CanonicalCategory> {

	private JaccardDistance jaccardDistance = new JaccardDistance();

	@Override
	public double compute(CanonicalCategory x, CanonicalCategory y) {
		return this.getJaccardDistance().compute(pathFromRootNames(x), pathFromRootNames(y));
	}

	private Set<String> pathFromRootNames(CanonicalCategory x) {
		Set<String> xNames = Sets.newHashSet();
		for (CanonicalCategory canonicalCategory : x.getPathFromRoot()) {
			xNames.add(canonicalCategory.getName());
		}
		return xNames;
	}

	public JaccardDistance getJaccardDistance() {
		return jaccardDistance;
	}

	public void setJaccardDistance(JaccardDistance jaccardDistance) {
		this.jaccardDistance = jaccardDistance;
	}
}
