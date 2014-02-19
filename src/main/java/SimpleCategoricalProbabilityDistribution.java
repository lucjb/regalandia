import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class SimpleCategoricalProbabilityDistribution<T> implements
		CategoricalProbabilityDistribution<T> {

	private Map<T, Double> upperBounds = Maps.newHashMap();
	private List<T> sortedCategories = Lists.newArrayList();
	private Random random = new Random(System.currentTimeMillis());

	public SimpleCategoricalProbabilityDistribution(Map<T, Double> pmf,
			Random random) {
		this.setRandom(random);
		Double cum = 0d;
		pmf.keySet();
		for (T t : pmf.keySet()) {
			Double pOfT = pmf.get(t);
			if (pOfT > 0d) {
				this.getUpperBounds().put(t, cum + pOfT);
				cum += pOfT;
				this.getSortedCategories().add(t);
			}
		}

		if (Math.abs(cum - 1d) > 1E-7) {
			throw new IllegalArgumentException(
					"The provided PMF does not integrate to 1: " + cum);
		}

	}

	@Override
	public T next() {
		double nextDouble = this.getRandom().nextDouble();
		int i = 0;
		double upperBound = this.getUpperBounds().get(
				this.getSortedCategories().get(i));
		for (; i < this.getSortedCategories().size()
				&& nextDouble >= upperBound; i++) {
			upperBound = this.getUpperBounds().get(
					this.getSortedCategories().get(i));
		}

		return this.getSortedCategories().get(i);
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

}
