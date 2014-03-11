package org.cronopios.regalator;

import java.util.Set;

import com.google.common.collect.Sets;

public class JaccardDistance implements Metric<Set<?>> {

	@Override
	public double compute(Set<?> x, Set<?> y) {
		double intersectionCardinality = Sets.intersection(x, y).size();
		double unionCardinality = Sets.union(x, y).size();
		return 1d - intersectionCardinality / unionCardinality;
	}

}
