package org.cronopios.regalator;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import com.google.common.collect.Ordering;

public class MetricBasedFullScanKNNRetriever<T> implements KNNRetriever<T> {

	private Collection<T> space;
	private Metric<T> metric;

	public MetricBasedFullScanKNNRetriever(Collection<T> space, Metric<T> metric) {
		this.setSpace(space);
		this.setMetric(metric);
	}

	@Override
	public List<T> retrieve(final T x, int k) {
		return Ordering.from(new Comparator<T>() {
			public int compare(T a, T b) {
				double axDistance = getMetric().compute(a, x);
				double bxDistance = getMetric().compute(b, x);
				return Double.compare(axDistance, bxDistance);
			}
		}).leastOf(this.getSpace(), k);
	}

	public Collection<T> getSpace() {
		return space;
	}

	public void setSpace(Collection<T> space) {
		this.space = space;
	}

	public Metric<T> getMetric() {
		return metric;
	}

	public void setMetric(Metric<T> metric) {
		this.metric = metric;
	}

}
