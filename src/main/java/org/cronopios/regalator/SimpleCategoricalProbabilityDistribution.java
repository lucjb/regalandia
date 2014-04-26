package org.cronopios.regalator;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.collections.ListUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class SimpleCategoricalProbabilityDistribution<T> implements CategoricalProbabilityDistribution<T> {

	private Map<T, Double> upperBounds = Maps.newHashMap();
	private List<T> sortedCategories = Lists.newArrayList();
	private List<Double> sortedCumPs = Lists.newArrayList();
	private Random random;

	public SimpleCategoricalProbabilityDistribution(Map<T, Double> pmf, Random random) {
		this.setRandom(random);
		Double cum = 0d;
		pmf.keySet();
		for (T t : pmf.keySet()) {
			Double pOfT = pmf.get(t);
			if (pOfT > 0d) {
				// this.getUpperBounds().put(t, cum + pOfT);
				cum += pOfT;
				this.getSortedCategories().add(t);
				this.getSortedCumPs().add(cum);
			}
		}

		if (Math.abs(cum - 1d) > 1E-7) {
			throw new IllegalArgumentException("The provided PMF does not integrate to 1: " + cum);
		}
	}

	@Override
	public T next() {
		double nextDouble = this.getRandom().nextDouble();
		int binarySearch = Collections.binarySearch(this.getSortedCumPs(), nextDouble);
		if (binarySearch >= 0)
			return this.getSortedCategories().get(binarySearch);
		return this.getSortedCategories().get(-binarySearch - 1);
		// int i = 0;
		// double upperBound =
		// this.getUpperBounds().get(this.getSortedCategories().get(i));
		// for (; i < this.getSortedCategories().size() && nextDouble >=
		// upperBound;) {
		// i++;
		// upperBound =
		// this.getUpperBounds().get(this.getSortedCategories().get(i));
		// }
		//
		// return this.getSortedCategories().get(i);
	}

	public Map<T, Double> getUpperBounds() {
		return upperBounds;
	}

	public void setUpperBounds(Map<T, Double> upperBounds) {
		this.upperBounds = upperBounds;
	}

	public Random getRandom() {
		return random;
	}

	public void setRandom(Random random) {
		this.random = random;
	}

	public List<T> getSortedCategories() {
		return sortedCategories;
	}

	public void setSortedCategories(List<T> sortedCategories) {
		this.sortedCategories = sortedCategories;
	}

	public List<Double> getSortedCumPs() {
		return sortedCumPs;
	}

	public void setSortedCumPs(List<Double> sortedCumPs) {
		this.sortedCumPs = sortedCumPs;
	}

}
