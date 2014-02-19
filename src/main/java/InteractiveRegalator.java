import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class InteractiveRegalator {

	public static void main(String[] args) throws IOException {
		MLCategoryParser mlCategoryParser = new MLCategoryParser();

		List<MLCategory> allMlCategories = mlCategoryParser.parseMLCategories("all");

		List<MLCategory> recommendableGifts = Lists.newArrayList();
		for (MLCategory mlCategory : allMlCategories) {
			if (mlCategory.getPath_from_root().size() > 3) {
				recommendableGifts.add(mlCategory);
			}
		}

		// WeightedRandomGiftRecommender<MLCategory>
		// weightedRandomGiftRecommender = new
		// WeightedRandomGiftRecommender<MLCategory>(
		// recommendableGifts, new MLCategoryPathJaccardIndex());

		KernelRegressionBasedGiftRecommender<MLCategory> kernelRegressionBasedGiftRecommender = new KernelRegressionBasedGiftRecommender<MLCategory>(recommendableGifts, new MLCategoryJaccardDistance());

		GiftRecommender<MLCategory> giftRecommender = kernelRegressionBasedGiftRecommender;

		int n = 3;
		Set<GiftRecommendation<MLCategory>> input = Sets.newHashSet();
		Set<GiftRecommendation<MLCategory>> recommendations = giftRecommender.recommend(input, n);

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
