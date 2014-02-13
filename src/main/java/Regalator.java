import java.io.FileNotFoundException;
import java.util.List;

public class Regalator {

	public static void main(String[] args) throws FileNotFoundException {
		MLCategoryParser mlCategoryParser = new MLCategoryParser();

		List<MLCategory> allMlCategories = mlCategoryParser
				.parseMLCategories("all");

		JaccardIndex distanceMeasure = new JaccardIndex();
		for (int i = 0; i < allMlCategories.size(); i++) {
			MLCategory x = allMlCategories.get(i);
			for (int j = 0; j < allMlCategories.size(); j++) {
				MLCategory y = allMlCategories.get(j);
				double distance = distanceMeasure.computeDistance(x, y);
				if (distance >= 0.5) {
					System.out.println(x + "  " + y + ": " + distance);
				}
			}

		}

	}
}
