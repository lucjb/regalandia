import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;

public class JaccardDistance<E> implements Metric<Collection<E>> {
	
	@Override
	public double compute(Collection<E> x, Collection<E> y) {
		Collection<E> intersection = CollectionUtils.intersection(x, y);
		Collection<E> union = CollectionUtils.union(x, y);
		return 1d - (double) intersection.size() / (double) union.size();
	}

}
