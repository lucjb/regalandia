import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class WeightedKNNRegressionGiftRecommender<T> implements GiftRecommender<T> {

	private List<T> gifts;
	private Map<T, GiftPoint<T>> giftSpace = Maps.newHashMap();
	private Multimap<GiftPoint<T>, GiftPoint<T>> knn = HashMultimap.create();
	private Random random = new Random(System.currentTimeMillis());
	private GiftRecommender<T> start;
	private KNNRetriever<GiftPoint<T>> knnRetriever;
	private int k = 10;
	private Metric<T> metric;

	public WeightedKNNRegressionGiftRecommender(List<T> gifts, final Metric<T> metric) {
		this.setGifts(Lists.newArrayList(gifts));
		this.setMetric(metric);
		this.setStart(new UniformRandomGiftRecommender<T>(this.getGifts()));
		for (T eachGift : gifts) {
			this.getGiftSpace().put(eachGift, new GiftPoint<T>(eachGift, null, Integer.MAX_VALUE));
		}
		this.setKnnRetriever(new MetricBasedFullScanKNNRetriever<GiftPoint<T>>(this.getGiftSpace().values(), new Metric<GiftPoint<T>>() {

			@Override
			public double compute(GiftPoint<T> x, GiftPoint<T> y) {
				return metric.compute(x.getGift(), y.getGift());
			}
		}));
	}

	@Override
	public Set<GiftRecommendation<T>> recommend(Set<GiftRecommendation<T>> previousRecommendations, int n) {
		if (previousRecommendations.isEmpty()) {
			return this.getStart().recommend(previousRecommendations, n);
		}
		this.populateTrainingData(previousRecommendations);
		this.estimateUserScore(previousRecommendations);
		Collection<GiftPoint<T>> values = this.getGiftSpace().values();
		for (GiftPoint<T> giftPoint : values) {
			if (giftPoint.getUncertainty() != Integer.MAX_VALUE)
				System.out.println(giftPoint);
		}
		return null;
	}

	private void estimateUserScore(Set<GiftRecommendation<T>> previousRecommendations) {
		List<GiftPoint<T>> neighbourhood = Lists.newArrayList();
		for (GiftRecommendation<T> giftRecommendation : previousRecommendations) {
			GiftPoint<T> giftPoint = this.getGiftSpace().get(giftRecommendation.getGift());
			neighbourhood.addAll(this.getKnnRetriever().retrieve(giftPoint, 10 * this.getK()));
		}

		for (GiftPoint<T> giftPoint : neighbourhood) {
			Collection<GiftPoint<T>> neighbours = this.getKnnRetriever().retrieve(giftPoint, this.getK());
			double distanceSum = 0;
			double scoreSum = 0;
			double uncertaintySum = 0;
			double neighbourCount = 0;
			for (GiftPoint<T> neighbour : neighbours) {
				if (neighbour.getUncertainty() < giftPoint.getUncertainty()) {
					// double distance =
					// this.getMetric().compute(giftPoint.getGift(),
					// neighbour.getGift());
					// distanceSum += distance;
					scoreSum += neighbour.getPredictedScore();
					uncertaintySum += neighbour.getUncertainty();
					neighbourCount++;
				}
			}
			if (neighbourCount != 0) {
				double avgScore = scoreSum / neighbourCount;
				giftPoint.setPredictedScore(avgScore);
				double avgUncertainty = uncertaintySum / neighbourCount;
				giftPoint.setUncertainty((int) Math.round(avgUncertainty));
			}
		}
	}

	private void populateTrainingData(Set<GiftRecommendation<T>> previousRecommendations) {
		for (GiftRecommendation<T> giftRecommendation : previousRecommendations) {
			GiftPoint<T> giftPoint = this.getGiftSpace().get(giftRecommendation.getGift());
			giftPoint.setPredictedScore(giftRecommendation.getUserScore());
			giftPoint.setUncertainty(1);
		}
	}

	public List<T> getGifts() {
		return gifts;
	}

	public void setGifts(List<T> gifts) {
		this.gifts = gifts;
	}

	public Random getRandom() {
		return random;
	}

	public void setRandom(Random random) {
		this.random = random;
	}

	public GiftRecommender<T> getStart() {
		return start;
	}

	public void setStart(GiftRecommender<T> start) {
		this.start = start;
	}

	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}

	public Map<T, GiftPoint<T>> getGiftSpace() {
		return giftSpace;
	}

	public void setGiftSpace(Map<T, GiftPoint<T>> giftSpace) {
		this.giftSpace = giftSpace;
	}

	public Multimap<GiftPoint<T>, GiftPoint<T>> getKnn() {
		return knn;
	}

	public void setKnn(Multimap<GiftPoint<T>, GiftPoint<T>> knn) {
		this.knn = knn;
	}

	public KNNRetriever<GiftPoint<T>> getKnnRetriever() {
		return knnRetriever;
	}

	public void setKnnRetriever(KNNRetriever<GiftPoint<T>> knnRetriever) {
		this.knnRetriever = knnRetriever;
	}

	public Metric<T> getMetric() {
		return metric;
	}

	public void setMetric(Metric<T> metric) {
		this.metric = metric;
	}

}
