package org.cronopios.regalator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;

import org.cronopios.regalator.ml.MLCategory;
import org.cronopios.regalator.ml.MLCategoryJaccardDistance;
import org.cronopios.regalator.ml.MLCategoryParser;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class InteractiveRegalator {

	public static void main(String[] args) throws IOException {
		MLCategoryParser mlCategoryParser = new MLCategoryParser();

		List<MLCategory> allMlCategories = mlCategoryParser
				.parseMLCategories("all");

		List<MLCategory> recommendableGifts = Lists.newArrayList();

		int niños = 0;
		int niñas = 0;
		int mujer = 0;
		int hombre = 0;
		int discarded = 0;
		int leaves = 0;
		int otros = 0;
		int total = 0;
		for (MLCategory mlCategory : allMlCategories) {
			if (mlCategory.isFor("Niñas")) {
				niñas++;
			}
			if (mlCategory.isFor("Niños")) {
				niños++;
			}
			if (mlCategory.isFor("Hombre")) {
				hombre++;
			}
			if (mlCategory.isFor("Mujer")) {
				mujer++;
			}
			if (mlCategory.isFor("Otros")) {
				otros++;
			}
			if (mlCategory.isFor("Otras")) {
				otros++;
			}
			if (mlCategory.getTotal_items_in_this_category() > 0
					&& mlCategory.isLeaf() && !mlCategory.isFor("Otros")
					&& !mlCategory.isFor("Otras")) {
				recommendableGifts.add(mlCategory);
			} else {
				discarded++;
			}

			if (mlCategory.getChildren_categories().isEmpty()) {
				leaves++;
			}
			total++;

		}
		System.out.println("Niñas " + niñas);
		System.out.println("Niños " + niños);
		System.out.println("Mujer " + mujer);
		System.out.println("Hombre " + hombre);
		System.out.println("Otros " + otros);
		System.out.println("Leaves " + leaves);
		System.out.println("Discarded " + discarded);
		System.out.println("Candidates " + recommendableGifts.size());
		System.out.println("Total " + total);

		// WeightedRandomGiftRecommender<MLCategory>
		// weightedRandomGiftRecommender = new
		// WeightedRandomGiftRecommender<MLCategory>(
		// recommendableGifts, new MLCategoryPathJaccardIndex());

		KernelRegressionBasedGiftRecommender<MLCategory> kernelRegressionBasedGiftRecommender = new KernelRegressionBasedGiftRecommender<MLCategory>(
				recommendableGifts, new MLCategoryJaccardDistance());
		GiftRecommender<MLCategory> kernelFilteredRegressionBasedGiftRecommender = new KNNFilterOutGiftRecommender(
				recommendableGifts, new MLCategoryJaccardDistance());

		GiftRecommender<MLCategory> giftRecommender = kernelRegressionBasedGiftRecommender;

		int n = 3;
		Set<GiftRecommendation<MLCategory>> input = Sets.newHashSet();
		Set<GiftRecommendation<MLCategory>> recommendations = giftRecommender
				.recommend(input, n);

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		while (true) {
			for (GiftRecommendation<MLCategory> giftRecommendation : recommendations) {
				System.out.println(giftRecommendation);
				System.out.print("Regalabilidad? (0-1):");
				String userInput = br.readLine();
				System.out.println();
				double userScore = Double.parseDouble(userInput);
				giftRecommendation.setUserScore(userScore);
			}
			System.out.print("Seguimo? (Y-N):");
			String userInput = br.readLine();

			if (userInput.equals("N")) {
				break;
			}
			input.addAll(recommendations);
			recommendations = giftRecommender.recommend(input, n);
		}
	}

}
