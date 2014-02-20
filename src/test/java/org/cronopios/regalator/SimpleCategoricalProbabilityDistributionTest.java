package org.cronopios.regalator;

import java.util.Map;
import java.util.Random;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;

import com.google.common.collect.Maps;

public class SimpleCategoricalProbabilityDistributionTest {

	private Random random = new Random(0);
	private Map<Object, Double> pmf = Maps.newHashMap();
	private SimpleCategoricalProbabilityDistribution<Object> distribution;
	private final String head = "HEAD";
	private final String tail = "TAIL";

	@After
	public void tearDown() {
		this.getPmf().clear();
	}

	@Test
	public void testUniqueCategory() {
		this.getPmf().put(head, 1d);
		this.setDistribution(this.makeADistribution());
		Assert.assertEquals(this.getDistribution().next(), head);
	}

	@Test
	public void testBinaryUniformDistribution() throws Exception {
		this.getPmf().put(head, 0.5);
		this.getPmf().put(tail, 0.5d);
		this.setDistribution(this.makeADistribution());

		int headsCount = 0;
		int tailsCount = 0;
		int sampleSize = 10000;
		for (int i = 0; i < sampleSize; i++) {
			Object next = this.getDistribution().next();
			if (next.equals(head)) {
				headsCount++;
			}
			if (next.equals(tail)) {
				tailsCount++;
			}

		}

		Assert.assertEquals(4984, headsCount);
		Assert.assertEquals(sampleSize - 4984, tailsCount);
	}

	@Test
	public void testUniformDistribution() throws Exception {
		int amountOfCategories = 12;
		double p = 1d / amountOfCategories;
		Map<Integer, Integer> counts = Maps.newHashMap();
		for (int i = 0; i < amountOfCategories; i++) {
			this.getPmf().put(i, p);
			counts.put(i, 0);
		}
		this.setDistribution(this.makeADistribution());

		int sampleSize = 10000;

		for (int i = 0; i < sampleSize; i++) {
			Integer next = (Integer) this.getDistribution().next();
			counts.put(next, counts.get(next) + 1);
		}

		Assert.assertTrue(counts.get(0).equals(855));
		Assert.assertTrue(counts.get(1).equals(849));
		Assert.assertTrue(counts.get(2).equals(821));
		Assert.assertTrue(counts.get(3).equals(790));
		Assert.assertTrue(counts.get(4).equals(880));
		Assert.assertTrue(counts.get(5).equals(821));
		Assert.assertTrue(counts.get(6).equals(808));
		Assert.assertTrue(counts.get(7).equals(831));
		Assert.assertTrue(counts.get(8).equals(852));
		Assert.assertTrue(counts.get(9).equals(836));
		Assert.assertTrue(counts.get(10).equals(859));
		Assert.assertTrue(counts.get(11).equals(798));

	}

	@Test
	public void testUniformDistributionWithManyCategories() throws Exception {
		int amountOfCategories = 10000;
		double p = 1d / amountOfCategories;
		Map<Integer, Integer> counts = Maps.newHashMap();
		for (int i = 0; i < amountOfCategories; i++) {
			this.getPmf().put(i, p);
			counts.put(i, 0);
		}
		this.setDistribution(this.makeADistribution());

		int sampleSize = 100000;

		for (int i = 0; i < sampleSize; i++) {
			Integer next = (Integer) this.getDistribution().next();
			counts.put(next, counts.get(next) + 1);
		}
		System.out.println(counts);

	}

	private SimpleCategoricalProbabilityDistribution<Object> makeADistribution() {
		return new SimpleCategoricalProbabilityDistribution<Object>(pmf, random);
	}

	public Map<Object, Double> getPmf() {
		return pmf;
	}

	public void setPmf(Map<Object, Double> pmf) {
		this.pmf = pmf;
	}

	public SimpleCategoricalProbabilityDistribution<Object> getDistribution() {
		return distribution;
	}

	public void setDistribution(
			SimpleCategoricalProbabilityDistribution<Object> distribution) {
		this.distribution = distribution;
	}

	public Random getRandom() {
		return random;
	}

	public void setRandom(Random random) {
		this.random = random;
	}

}
