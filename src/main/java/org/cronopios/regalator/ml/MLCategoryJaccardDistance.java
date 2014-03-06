package org.cronopios.regalator.ml;

import org.cronopios.regalator.JaccardDistance;
import org.cronopios.regalator.Metric;

import com.google.common.collect.Sets;

public class MLCategoryJaccardDistance implements Metric<MLCategory> {

	private JaccardDistance<MLCategory> jaccardDistance = new JaccardDistance<MLCategory>();

	@Override
	public double compute(MLCategory x, MLCategory y) {
		return this.getJaccardDistance().compute(
				Sets.newHashSet(x.getPath_from_root()),
				Sets.newHashSet(y.getPath_from_root()));
	}

	public JaccardDistance<MLCategory> getJaccardDistance() {
		return jaccardDistance;
	}

	public void setJaccardDistance(JaccardDistance<MLCategory> jaccardDistance) {
		this.jaccardDistance = jaccardDistance;
	}
}
