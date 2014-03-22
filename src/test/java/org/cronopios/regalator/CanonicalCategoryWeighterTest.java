package org.cronopios.regalator;

import java.util.List;

import org.cronopios.regalator.filters.OtrosFilter;
import org.cronopios.regalator.ml.MLCategory;
import org.cronopios.regalator.ml.MLCategoryParser;
import org.cronopios.regalator.ml.brands.FlagBasedBrandFilter;
import org.junit.Assert;
import org.junit.Test;

public class CanonicalCategoryWeighterTest {

	@Test
	public void testWeighter() throws Exception {
		MLCategoryParser mlCategoryParser = new MLCategoryParser();
		List<MLCategory> allMlCategories = mlCategoryParser.parseMLCategories();
		new OtrosFilter().filter(allMlCategories);
		new FlagBasedBrandFilter().filter(allMlCategories);

		CanonicalCategoryWeighter weighter = new CanonicalCategoryWeighter(allMlCategories, 1d);

		double totalWeight = 0;
		for (MLCategory mlCategory : allMlCategories) {
			if (mlCategory.isLeaf()) {
				double weight = weighter.weight(mlCategory);
				totalWeight += weight;
			}
		}
		Assert.assertEquals(1d, totalWeight, 10E-7);
	}
}
