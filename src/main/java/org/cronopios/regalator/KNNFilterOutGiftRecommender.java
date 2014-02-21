package org.cronopios.regalator;

import java.util.List;
import java.util.Set;

public class KNNFilterOutGiftRecommender<T> extends KernelRegressionBasedGiftRecommender<T> {

	private KNNRetriever<GiftPoint<T>> knnRetriever;

	public KNNFilterOutGiftRecommender(List<T> gifts, final Metric<T> metric) {
		super(gifts, metric);
		this.setKnnRetriever(new MetricBasedFullScanKNNRetriever<GiftPoint<T>>(this.getPoints(), new Metric<GiftPoint<T>>() {

			@Override
			public double compute(GiftPoint<T> x, GiftPoint<T> y) {
				return metric.compute(x.getGift(), y.getGift());
			}
		}));
	}

	@Override
	public Set<GiftRecommendation<T>> recommend(Set<GiftRecommendation<T>> previousRecommendations, int n) {
		for (GiftRecommendation<T> giftRecommendation : previousRecommendations) {
			if (giftRecommendation.getUserScore().equals(0d)) {
				List<GiftPoint<T>> retrieve = this.getKnnRetriever().retrieve(this.getGiftSpace().get(giftRecommendation.getGift()), 10);
				GiftPoint<T> giftPoint2 = retrieve.get(9);
				double minDistance = this.getMetric().compute(giftPoint2.getGift(), giftRecommendation.getGift());
				System.out.println("min distance: " + minDistance);
				for (GiftPoint<T> giftPoint : this.getPoints()) {
					double distance = this.getMetric().compute(giftPoint.getGift(), giftRecommendation.getGift());
					if (distance <= minDistance) {
						giftPoint.setPredictedScore(0d);
						giftPoint.setCertainty(1d);
						giftPoint.setRecommendability(0d);
						System.out.println("removed: " + giftPoint.getGift());
					}
				}
			}

		}
		return super.recommend(previousRecommendations, n);
	}

	public KNNRetriever<GiftPoint<T>> getKnnRetriever() {
		return knnRetriever;
	}

	public void setKnnRetriever(KNNRetriever<GiftPoint<T>> knnRetriever) {
		this.knnRetriever = knnRetriever;
	}

}
