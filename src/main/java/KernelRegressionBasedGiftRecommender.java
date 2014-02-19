import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.math3.analysis.UnivariateFunction;

import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

public class KernelRegressionBasedGiftRecommender<T> implements
		GiftRecommender<T> {

	private List<T> gifts;
	private Map<T, GiftPoint<T>> giftSpace = Maps.newHashMap();
	private Random random = new Random(System.currentTimeMillis());
	private GiftRecommender<T> start;
	private Metric<T> metric;
	private double sigma = 1d / (10d * Math.sqrt(2 * Math.PI));
	// private UnivariateFunction kernel = new Gaussian(0d, this.getSigma());
	private UnivariateFunction kernel = new UnivariateFunction() {

		@Override
		public double value(double x) {
			return Math.exp(-10 * x);
		}
	};
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
		if (previousRecommendations.isEmpty()) {
			return this.getStart().recommend(previousRecommendations, n);
		}

		populateFacts(previousRecommendations);
		estimateUserScores(previousRecommendations);
		estimateRecommendability();

		// return drawRecommendations(n);
		return drawRecommendations(n, makeGiftDistribution());
	}

	private Set<GiftRecommendation<T>> drawRecommendations(int n) {
		Set<GiftRecommendation<T>> out = Sets.newHashSet();
		List<GiftPoint<T>> maxProbableGifts = this.maxProbableGifts(n);
		for (GiftPoint<T> giftPoint : maxProbableGifts) {
			out.add(new GiftRecommendation<T>(giftPoint.getGift(), giftPoint
					.getRecommendability()));
		}
		return out;
	}

	private Set<GiftRecommendation<T>> drawRecommendations(
			int n,
			SimpleCategoricalProbabilityDistribution<GiftPoint<T>> probabilityDistribution) {
		Set<GiftRecommendation<T>> out = Sets.newHashSet();
		for (int i = 0; i < n; i++) {
			GiftPoint<T> next = probabilityDistribution.next();
			out.add(new GiftRecommendation<T>(next.getGift(), next
					.getRecommendability()));
		}
		return out;
	}

	private SimpleCategoricalProbabilityDistribution<GiftPoint<T>> makeGiftDistribution() {
		Iterable<GiftPoint<T>> points = this.getPoints();
		Map<GiftPoint<T>, Double> pmf = Maps.newHashMap();
		for (GiftPoint<T> giftPoint : points) {
			pmf.put(giftPoint, giftPoint.getRecommendability());
		}
		SimpleCategoricalProbabilityDistribution<GiftPoint<T>> probabilityDistribution = new SimpleCategoricalProbabilityDistribution<GiftPoint<T>>(
				pmf, this.getRandom());

		return probabilityDistribution;

	}

	private List<GiftPoint<T>> maxProbableGifts(int n) {
		return Ordering.from(new Comparator<GiftPoint<T>>() {

			@Override
			public int compare(GiftPoint<T> o1, GiftPoint<T> o2) {
				return Double.compare(o1.getRecommendability(),
						o2.getRecommendability());
			}
		}).greatestOf(this.getPoints(), n);
	}

	private void estimateRecommendability() {
		double certaintySum = 0d;
		double scoreSum = 0d;
		for (GiftPoint<T> giftPoint : this.getPoints()) {
			if (giftPoint.getCertainty() == 1d)
				continue;

			certaintySum += giftPoint.getCertainty();
			scoreSum += giftPoint.getPredictedScore();
		}
		if (scoreSum == 0) {
			scoreSum = 1d;
		}

		double normalizedCertaintySum = 0d;
		double normalizedScoreSum = 0d;
		for (GiftPoint<T> giftPoint : this.getPoints()) {
			if (giftPoint.getCertainty() == 1d)
				continue;

			Double normalizedCertainty = giftPoint.getCertainty()
					/ certaintySum;

			Double normalizedScore = giftPoint.getPredictedScore() / scoreSum;

			normalizedCertaintySum += normalizedCertainty;
			normalizedScoreSum += normalizedScore;

		}

		for (GiftPoint<T> giftPoint : this.getPoints()) {
			if (giftPoint.getCertainty() == 1d)
				continue;

			Double normalizedCertainty = giftPoint.getCertainty()
					/ certaintySum;

			Double normalizedScore = giftPoint.getPredictedScore() / scoreSum;

			double recommendability = (normalizedCertainty + normalizedScore)
					/ (normalizedCertaintySum + normalizedScoreSum);
			giftPoint.setRecommendability(recommendability);
		}

	}

	private void populateFacts(
			Set<GiftRecommendation<T>> previousRecommendations) {
		for (GiftRecommendation<T> giftRecommendation : previousRecommendations) {
			GiftPoint<T> giftPoint = this.getGiftSpace().get(
					giftRecommendation.getGift());
			giftPoint.setPredictedScore(giftRecommendation.getUserScore());
			giftPoint.setCertainty(1d);
			giftPoint.setRecommendability(0d);
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
		double count = 0;
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
			count++;
		}
		double nadarayaWatsonDistanceWeightedUserScore = distanceWeightedUserScoreSum
				/ distanceWeightsSum;
		double avgDistanceWeightedCertainty = distanceWeightedCertaintySum
				/ count;
		x.setPredictedScore(nadarayaWatsonDistanceWeightedUserScore);
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
