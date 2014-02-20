package org.cronopios.regalator.ml;
import org.cronopios.regalator.JaccardDistance;
import org.cronopios.regalator.Metric;

public class MLCategoryJaccardDistance implements Metric<MLCategory> {

	private JaccardDistance<MLCategory> jaccardDistance = new JaccardDistance<MLCategory>();

	@Override
	public double compute(MLCategory x, MLCategory y) {
		return this.getJaccardDistance().compute(x.getPath_from_root(),
				y.getPath_from_root());
	}

	public JaccardDistance<MLCategory> getJaccardDistance() {
		return jaccardDistance;
	}

	public void setJaccardDistance(JaccardDistance<MLCategory> jaccardDistance) {
		this.jaccardDistance = jaccardDistance;
	}
}
