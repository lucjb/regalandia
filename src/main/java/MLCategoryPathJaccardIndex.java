import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;

public class MLCategoryPathJaccardIndex implements SimilarityMeasure<MLCategory> {

	public double computeSimilarity(MLCategory x, MLCategory y) {
		Collection intersection = CollectionUtils.intersection(x.getPath_from_root(), y.getPath_from_root());
		Collection union = CollectionUtils.union(x.getPath_from_root(), y.getPath_from_root());
		return (double) intersection.size() / (double) union.size();
	}


}
