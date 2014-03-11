package org.cronopios.regalator;

import com.google.common.collect.Sets;

public class CanonicalCategoryJaccardDistance implements
		Metric<CanonicalCategory> {

	private JaccardDistance jaccardDistance = new JaccardDistance();

	@Override
	public double compute(CanonicalCategory x, CanonicalCategory y) {
		return this.getJaccardDistance().compute(
				Sets.newHashSet(x.getPathFromRoot()),
				Sets.newHashSet(y.getPathFromRoot()));
	}

	public JaccardDistance getJaccardDistance() {
		return jaccardDistance;
	}

	public void setJaccardDistance(JaccardDistance jaccardDistance) {
		this.jaccardDistance = jaccardDistance;
	}
}
