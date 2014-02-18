import java.util.Comparator;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

public class FullScanKNNRetriever<T> implements KNNRetriever<T> {
	private List<T> gifts;
	private SimilarityMeasure<T> similarityMeasure;

	public FullScanKNNRetriever(List<T> gifts, SimilarityMeasure<T> similarityMeasure) {
		this.setGifts(gifts);
		this.setSimilarityMeasure(similarityMeasure);
	}

	@Override
	public List<T> retrieve(final T x, int k) {
		if (k < 0)
			k = this.getGifts().size();
		Ordering<T> from = Ordering.from(new Comparator<T>() {
			public int compare(T y, T z) {
				double xyDistance = getSimilarityMeasure().computeSimilarity(x, y);
				double xzDistance = getSimilarityMeasure().computeSimilarity(x, z);
				return Double.compare(xyDistance, xzDistance);
			};
		});
		List<T> knn = Lists.newArrayList(from.greatestOf(this.getGifts(), k));
		return knn;
	}

	public List<T> getGifts() {
		return gifts;
	}

	public void setGifts(List<T> gifts) {
		this.gifts = gifts;
	}

	public SimilarityMeasure<T> getSimilarityMeasure() {
		return similarityMeasure;
	}

	public void setSimilarityMeasure(SimilarityMeasure<T> similarityMeasure) {
		this.similarityMeasure = similarityMeasure;
	}

}
