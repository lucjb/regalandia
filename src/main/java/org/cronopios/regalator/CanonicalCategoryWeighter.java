package org.cronopios.regalator;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class CanonicalCategoryWeighter implements
		GiftWeighter<CanonicalCategory> {

	private Map<CanonicalCategory, Double> weighgts = Maps.newHashMap();

	public CanonicalCategoryWeighter(
			Iterable<? extends CanonicalCategory> recommendableGifts,
			double totalWeight) {
		Collection<CanonicalCategory> nodes = Lists.newArrayList();
		for (CanonicalCategory mlCategory : recommendableGifts) {
			if (mlCategory.isRoot()) {
				nodes.add(mlCategory);
			}
		}

		distributeWeight(nodes, totalWeight);
		System.out.println("Weighter weighted "
				+ this.getWeighgts().keySet().size() + " gifts.");
	}

	private void distributeWeight(
			Collection<? extends CanonicalCategory> nodes, double parentWieght) {
		for (CanonicalCategory node : nodes) {
			double weight = parentWieght / nodes.size();
			this.getWeighgts().put(node, weight);
			distributeWeight(node.getChildren(), weight);
		}
	}

	@Override
	public double weight(CanonicalCategory gift) {
		return this.getWeighgts().get(gift);
	}

	public Map<CanonicalCategory, Double> getWeighgts() {
		return weighgts;
	}

	public void setWeighgts(Map<CanonicalCategory, Double> weighgts) {
		this.weighgts = weighgts;
	}

}
