import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class SimpleCategoricalProbabilityDistribution<T> implements CategoricalProbabilityDistribution<T> {

	private Map<T, Double> upperBounds = Maps.newHashMap();
	private List<T> sortedCategories;
	private Random random = new Random(System.currentTimeMillis());

	public SimpleCategoricalProbabilityDistribution(Map<T, Double> pmf, Random random) {
		this.setRandom(random);
		this.setSortedCategories(Lists.newArrayList(pmf.keySet()));
		Double cum = 0d;
		for (T t : sortedCategories) {
			Double pOfT = pmf.get(t);
			this.getUpperBounds().put(t, cum + pOfT);
			cum += pOfT;
		}

	}

	@Override
	public T next() {
		double nextDouble = this.getRandom().nextDouble();
		int i = 0;
		for (; i < this.getSortedCategories().size() && nextDouble < this.getUpperBounds().get(this.getSortedCategories().get(i)); i++) {
		}
		return this.getSortedCategories().get(i - 1);
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
