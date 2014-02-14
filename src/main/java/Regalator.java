import java.io.FileNotFoundException;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

public class Regalator {

	public static void main(String[] args) throws FileNotFoundException {
		MLCategoryParser mlCategoryParser = new MLCategoryParser();

		List<MLCategory> allMlCategories = mlCategoryParser.parseMLCategories("all");

		WeightedRandomGiftRecommender<MLCategory> giftRecommender = new WeightedRandomGiftRecommender<MLCategory>(allMlCategories, new FullScanKNNRetriever<MLCategory>(allMlCategories, new MLCategoryPathJaccardIndex()));
		int n = 5;
		Random random = new Random();

		List<GiftRecommendation<MLCategory>> input = Lists.newArrayList();
		for (int i = 0; i < 3; i++) {
			List<GiftRecommendation<MLCategory>> recommend = giftRecommender.recommend(input, n);
			print(input);
			System.out.println("----------------------------------------------------");
			input.addAll(recommend);
			for (GiftRecommendation<MLCategory> giftRecommendation : input) {
				giftRecommendation.setUserScore(0.5d);
			}
		}

		print(input);

	}

	private static void print(List<GiftRecommendation<MLCategory>> input) {
		for (GiftRecommendation<MLCategory> giftRecommendation : input) {
			System.out.println(giftRecommendation);
		}
	}

	private static void showSomeRecs(List<MLCategory> allMlCategories) {
		MLCategoryPathJaccardIndex distanceMeasure = new MLCategoryPathJaccardIndex();
		Random r = new Random();
		for (int i = 0; i < 1000; i++) {
			MLCategory x = allMlCategories.get(r.nextInt(allMlCategories.size()));
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
