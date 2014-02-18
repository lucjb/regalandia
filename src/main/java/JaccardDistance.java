import java.util.Collection;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import sun.text.normalizer.UBiDiProps;

import com.google.common.collect.Sets;

public class JaccardDistance<E> implements Metric<Set<E>> {

	@Override
	public double compute(Set<E> x, Set<E> y) {
		double intersectionCardinality = Sets.intersection(x, y).size();
		double unionCardinality = Sets.union(x, y).size();
		return 1d - intersectionCardinality / unionCardinality;
	}

}
