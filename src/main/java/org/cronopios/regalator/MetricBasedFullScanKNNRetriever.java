package org.cronopios.regalator;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;

public class MetricBasedFullScanKNNRetriever<T> implements KNNRetriever<T> {

	private Iterable<T> space;
	private Metric<T> metric;

	public MetricBasedFullScanKNNRetriever(Iterable<T> space, Metric<T> metric) {
		this.setSpace(space);
		this.setMetric(metric);
	}

	@Override
	public List<T> retrieve(final T x, int k) {
		DistanceToTargetComparator<T> comparator = new DistanceToTargetComparator<T>(
				x, this.getMetric());
		return Ordering.from(comparator).leastOf(this.getSpace(), k);
	}

	public Iterable<T> getSpace() {
		return space;
	}

	public void setSpace(Iterable<T> space) {
		this.space = space;
	}

	public Metric<T> getMetric() {
		return metric;
	}

	public void setMetric(Metric<T> metric) {
		this.metric = metric;
	}

	@Override
	public SortedMap<Double, Collection<T>> retrieveKNearestSpheres(final T x,
			int k) {
		ImmutableListMultimap<Double, T> index = Multimaps.index(
				this.getSpace(), new Function<T, Double>() {

					@Override
					public Double apply(T input) {
						return getMetric().compute(x, input);
					}
				});

		TreeMap<Double, Collection<T>> sIndex = new TreeMap<Double, Collection<T>>(
				Ordering.natural());
		sIndex.putAll(index.asMap());
		List<Double> leastOf = Ordering.natural().leastOf(sIndex.keySet(), k);
		SortedMap<Double, Collection<T>> headMap = sIndex.headMap(
				leastOf.get(leastOf.size() - 1), true);
		return headMap;
	}

}
