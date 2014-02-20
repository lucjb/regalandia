package org.cronopios.regalator;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.cronopios.regalator.ml.MLCategory;
import org.cronopios.regalator.ml.MLCategoryParser;
import org.cronopios.regalator.ml.MLCategoryPathJaccardIndex;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class Regalator {

	public static void main(String[] args) throws FileNotFoundException {
		MLCategoryParser mlCategoryParser = new MLCategoryParser();

		List<MLCategory> allMlCategories = mlCategoryParser
				.parseMLCategories("all");

		List<MLCategory> recommendableGifts = Lists.newArrayList();
		for (MLCategory mlCategory : allMlCategories) {
			if (mlCategory.getPath_from_root().size() > 3) {
				recommendableGifts.add(mlCategory);
			}
		}

		WeightedRandomGiftRecommender<MLCategory> giftRecommender = new WeightedRandomGiftRecommender<MLCategory>(
				recommendableGifts, new MLCategoryPathJaccardIndex());
		int n = 5;

		Set<GiftRecommendation<MLCategory>> input = Sets.newHashSet();
		for (int i = 0; i < 3; i++) {
			Set<GiftRecommendation<MLCategory>> recommend = giftRecommender
					.recommend(input, n);
			print(input);
			System.out
					.println("----------------------------------------------------");
			input.addAll(recommend);
			for (GiftRecommendation<MLCategory> giftRecommendation : input) {
				giftRecommendation.setUserScore(0.5d);
			}
		}

		print(input);

	}

	private static void print(Collection<GiftRecommendation<MLCategory>> input) {
		for (GiftRecommendation<MLCategory> giftRecommendation : input) {
			System.out.println(giftRecommendation);
		}
	}

	private static void showSomeRecs(List<MLCategory> allMlCategories) {
		MLCategoryPathJaccardIndex distanceMeasure = new MLCategoryPathJaccardIndex();
		Random r = new Random();
		for (int i = 0; i < 1000; i++) {
			MLCategory x = allMlCategories
					.get(r.nextInt(allMlCategories.size()));
			System.out.println(x);
			for (int j = 0; j < allMlCategories.size(); j++) {
				MLCategory y = allMlCategories.get(j);
				double distance = distanceMeasure.computeSimilarity(x, y);
				if (distance >= 0.5) {
					System.out.println(y + ": " + distance);
				}
			}
			System.out.println("------------------------------------------");
			System.out.println("------------------------------------------");
			System.out.println("------------------------------------------");
		}
	}
}
