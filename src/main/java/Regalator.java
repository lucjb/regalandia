import java.io.FileNotFoundException;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;

public class Regalator {

	public static void main(String[] args) throws FileNotFoundException {
		MLCategoryParser mlCategoryParser = new MLCategoryParser();

		List<MLCategory> allMlCategories = mlCategoryParser
				.parseMLCategories("all");

		JaccardIndex distanceMeasure = new JaccardIndex();
		Random r = new Random();
		for (int i = 0; i < 1000; i++) {
			MLCategory x = allMlCategories
					.get(r.nextInt(allMlCategories.size()));
			System.out.println(x);
			for (int j = 0; j < allMlCategories.size(); j++) {
				MLCategory y = allMlCategories.get(j);
				double distance = distanceMeasure.computeDistance(x, y);
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
