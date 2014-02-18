import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.function.Gaussian;

import com.google.common.collect.Maps;

public class KernelRegressionBasedGiftRecommender<T> implements
		GiftRecommender<T> {

	private List<T> gifts;
	private Map<T, GiftPoint<T>> giftSpace = Maps.newHashMap();
	private Random random = new Random(System.currentTimeMillis());
	private GiftRecommender<T> start;
	private Metric<T> metric;
	private double sigma = 1;
	private UnivariateFunction kernel = new Gaussian(0d, this.getSigma());
	private Iterable<GiftPoint<T>> points;

	public KernelRegressionBasedGiftRecommender(List<T> gifts, Metric<T> metric) {
		this.setGifts(gifts);
		this.setMetric(metric);
		this.setStart(new UniformRandomGiftRecommender(this.getGifts()));
		for (T t : gifts) {
			this.getGiftSpace().put(t, new GiftPoint(t, null, 0d));
		}
		this.setPoints(this.getGiftSpace().values());
	}

	@Override
	public Set<GiftRecommendation<T>> recommend(
			Set<GiftRecommendation<T>> previousRecommendations, int n) {

		populateFacts(previousRecommendations);
		estimateUserScores(previousRecommendations);
		estimateRecommendability();

		Collection<GiftPoint<T>> values = this.getGiftSpace().values();

		return start.recommend(previousRecommendations, n);
	}

	private void estimateRecommendability() {
		double totalW = 0d;
		for (GiftPoint<T> giftPoint : this.getPoints()) {
			if (giftPoint.getCertainty() == 1d)
				continue;
			double w = giftPoint.getCertainty() * giftPoint.getPredictedScore();
			totalW += w;
		}

		for (GiftPoint<T> giftPoint : this.getPoints()) {
			if (giftPoint.getCertainty() == 1d)
				continue;
			double w = giftPoint.getCertainty() * giftPoint.getPredictedScore();
			giftPoint.setRecommendability(w / totalW);
		}
	}

	private void populateFacts(
			Set<GiftRecommendation<T>> previousRecommendations) {
		for (GiftRecommendation<T> giftRecommendation : previousRecommendations) {
			GiftPoint<T> giftPoint = this.getGiftSpace().get(
					giftRecommendation.getGift());
			giftPoint.setPredictedScore(giftRecommendation.getUserScore());
			giftPoint.setCertainty(1d);
		}
	}

	private void estimateUserScores(
			Set<GiftRecommendation<T>> previousRecommendations) {
		for (GiftPoint<T> x : this.getPoints()) {
			if (x.getCertainty() == 1d)
				continue;
			estimateUserScore(x, previousRecommendations);
		}

	}

	public void estimateUserScore(GiftPoint<T> x,
			Set<GiftRecommendation<T>> previousRecommendations) {
		double distanceWeightedUserScoreSum = 0d;
		double distanceWeightsSum = 0d;
		double distanceWeightedCertaintySum = 0d;
		for (GiftRecommendation<T> pr : previousRecommendations) {
			GiftPoint<T> y = this.getGiftSpace().get(pr.getGift());
			double xyDistance = this.getMetric().compute(x.getGift(),
					y.getGift());
			double distanceWeight = this.getKernel().value(xyDistance);
			distanceWeightsSum += distanceWeight;
			double distanceWeightedUserScore = distanceWeight
					* y.getPredictedScore();
			double distanceWeightedCertainty = distanceWeight
					* y.getCertainty();
			distanceWeightedUserScoreSum += distanceWeightedUserScore;
			distanceWeightedCertaintySum += distanceWeightedCertainty;
		}
		double avgDistanceWeightedUserScore = distanceWeightedUserScoreSum
				/ distanceWeightsSum;
		double avgDistanceWeightedCertainty = distanceWeightedCertaintySum
				/ distanceWeightsSum;
		x.setPredictedScore(avgDistanceWeightedUserScore);
		x.setCertainty(avgDistanceWeightedCertainty);
	}

	public List<T> getGifts() {
		return gifts;
	}

	public void setGifts(List<T> gifts) {
		this.gifts = gifts;
	}

	public Map<T, GiftPoint<T>> getGiftSpace() {
		return giftSpace;
	}

	public void setGiftSpace(Map<T, GiftPoint<T>> giftSpace) {
		this.giftSpace = giftSpace;
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

	public Metric<T> getMetric() {
		return metric;
	}

	public void setMetric(Metric<T> metric) {
		this.metric = metric;
	}

	public double getSigma() {
		return sigma;
	}

	public void setSigma(double sigma) {
		this.sigma = sigma;
	}

	public UnivariateFunction getKernel() {
		return kernel;
	}

	public void setKernel(UnivariateFunction kernel) {
		this.kernel = kernel;
	}

	public Iterable<GiftPoint<T>> getPoints() {
		return points;
	}

	public void setPoints(Iterable<GiftPoint<T>> points) {
		this.points = points;
	}

}
