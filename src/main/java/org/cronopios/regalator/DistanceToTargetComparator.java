package org.cronopios.regalator;

import java.util.Comparator;

public class DistanceToTargetComparator<T> implements Comparator<T> {

	private T target;
	private Metric<T> metric;

	public DistanceToTargetComparator(T target, Metric<T> metric) {
		this.setTarget(target);
		this.setMetric(metric);
	}

	@Override
	public int compare(T a, T b) {
		double axDistance = getMetric().compute(a, this.getTarget());
		double bxDistance = getMetric().compute(b, this.getTarget());
		return Double.compare(axDistance, bxDistance);
	}

	public Metric<T> getMetric() {
		return metric;
	}

	public void setMetric(Metric<T> metric) {
		this.metric = metric;
	}

	public T getTarget() {
		return target;
	}

	public void setTarget(T target) {
		this.target = target;
	}

}
