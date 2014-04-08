package org.cronopios.regalator.mains;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.cronopios.regalator.CanonicalCategory;
import org.cronopios.regalator.CanonicalCategoryJaccardDistance;
import org.cronopios.regalator.GiftRecommendation;
import org.cronopios.regalator.GiftRecommender;
import org.cronopios.regalator.GiftWeighter;
import org.cronopios.regalator.KNearestSpheresYesNoGiftRecommender;
import org.cronopios.regalator.NeighboursWithinSphereYesNoGiftRecommender;
import org.cronopios.regalator.WeightableWeighter;
import org.cronopios.regalator.ml.MLCategory;
import org.cronopios.regalator.ml.MLCategoryParser;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class InteractiveRegalator {

	public static void main(String[] args) throws IOException {
		// Collection<CanonicalCategory> iceCategories = iceCategories();
		Collection<CanonicalCategory> mlCategories = mercadoLibreTargetCategories();
		Collection<CanonicalCategory> all = Lists.newLinkedList();

		all.addAll(mlCategories);
		// all.addAll(iceCategories);

		GiftWeighter<CanonicalCategory> giftWeighter = new WeightableWeighter();
		CanonicalCategoryJaccardDistance metric = new CanonicalCategoryJaccardDistance();

		KNearestSpheresYesNoGiftRecommender<CanonicalCategory> yesNoGiftRecommender = new NeighboursWithinSphereYesNoGiftRecommender<CanonicalCategory>(all, metric, giftWeighter);
		GiftRecommender<CanonicalCategory> giftRecommender = yesNoGiftRecommender;

		int n = 5;
		Set<GiftRecommendation<CanonicalCategory>> input = Sets.newHashSet();
		Set<GiftRecommendation<CanonicalCategory>> recommendations = giftRecommender.recommend(input, n);

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		while (true) {
			for (GiftRecommendation<CanonicalCategory> giftRecommendation : recommendations) {
				System.out.println(giftRecommendation.getGift().isLeaf() + "   " + giftRecommendation);
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

	// private static Collection<CanonicalCategory> iceCategories() throws
	// FileNotFoundException {
	// Collection<? extends IceCatCategory> iceCategories = new
	// IceCatParser().parse();
	// new NoLeafFilter().filter(iceCategories);
	// return (Collection<CanonicalCategory>) iceCategories;
	// }

	private static Collection<CanonicalCategory> mercadoLibreTargetCategories() throws IOException {
		MLCategoryParser mlCategoryParser = new MLCategoryParser();
		// List<MLCategory> mlCategories =
		// mlCategoryParser.parseMLCategories("mltest.json");
		List<MLCategory> mlCategories = mlCategoryParser.parseMLCategories();

		mlCategoryParser.filterAndWeight(mlCategories);
		Collection<? extends CanonicalCategory> r = mlCategories;
		return (Collection<CanonicalCategory>) r;
	}
}
