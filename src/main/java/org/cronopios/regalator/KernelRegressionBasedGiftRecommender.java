package org.cronopios.regalator;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.function.Gaussian;
import org.apache.commons.math3.stat.Frequency;
import org.cronopios.regalator.ml.MLCategory;
import org.cronopios.regalator.ml.MLCategoryJaccardDistance;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;

/**
 * Each gift has a user score and a certainty (GiftPoint). The user score is a
 * real number in [0,1]. 0 means the gift is totally wrong, 1 means the gift is
 * perfect. The certainty is a real number in [0,1] also. All user provided
 * scores, have certainty 1. Based on user provided scores (only these), which
 * have certainty 1 always, this algorithm tries to predict the user score of
 * all other gifts. For each unscored gift, its user score is predicted using
 * the naradaya-watson kernel estimator. Its certainty, is estimated as the
 * average of another kernel (not actually a kernel, it is just a function which
 * evaluates to 1 at d=0, and decreases for d>0).
 * 
 * Once user score and certainty is estimated for all other gitfs, a probability
 * distribution is built based on these two features. This is the probability of
 * being suggested to the user.
 * 
 * 
 * For each giftpoint g with certainty(g) < 1, its probability is:
 * 
 * w(g)=(1-certainty(g))*1/n + certainty(g)*score(g)
 * 
 * p(g)=w/sum(w)
 * 
 * n is the amount of gifts with certainty<1.
 * 
 * @author lbernardi
 * 
 * @param <T>
 */
public class KernelRegressionBasedGiftRecommender<T> implements
		GiftRecommender<T> {

	private List<T> gifts;
	private Map<T, GiftPoint<T>> giftSpace = Maps.newHashMap();
	private Random random = new Random(0);
	private GiftRecommender<T> start;
	private Metric<T> metric;
	private UnivariateFunction scoreKernel = new Gaussian(0d,
			1d / (Math.sqrt(2 * Math.PI)));
	private UnivariateFunction certaintyKernel = new UnivariateFunction() {

		@Override
		public double value(double x) {
			return (1 - x) * Math.exp(-x);
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

		printImpact(previousRecommendations);

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
		Set<GiftRecommendation<T>> drawRecommendations = drawRecommendations(1);
		System.out.println("max probable: "
				+ drawRecommendations.iterator().next());
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
		Ordering<GiftPoint<T>> recommendabilityOrdering = Ordering
				.from(new Comparator<GiftPoint<T>>() {

					@Override
					public int compare(GiftPoint<T> o1, GiftPoint<T> o2) {
						return Double.compare(o1.getRecommendability(),
								o2.getRecommendability());
					}
				});
		List<GiftPoint<T>> sortedCopy = recommendabilityOrdering.reverse()
				.sortedCopy(this.getPoints());
		for (int i = 0; i < 100; i++) {
			GiftPoint<T> giftPoint = sortedCopy.get(i);
			System.out.println(giftPoint.getGift() + " "
					+ giftPoint.getRecommendability());
		}
		return recommendabilityOrdering.greatestOf(this.getPoints(), n);
	}

	private void estimateRecommendability() {

		// Compute amount of recommendable gifts as n.
		// Compute sum of predicted scores (only predicted user provided scores
		// are ignored since they are not recommendable)
		double scoreSum = 0d;
		double n = 0;
		for (GiftPoint<T> giftPoint : this.getPoints()) {
			if (giftPoint.getCertainty() == 1d)
				continue;

			scoreSum += giftPoint.getPredictedScore();
			n++;
		}
		// User rejected all recommended gifts, assigns 1d or whatever constant
		// to avoid 0/0
		if (scoreSum == 0) {
			scoreSum = 1d;
		}

		// Compute minimum contribution of user score to final probability.
		double minNormalizedScore = minNormalizedScore(scoreSum);

		// Compute the weight of each gift and total sum for post-normalization.
		// When certainty is 0, w must be almost 0, far lower than the minimum
		// normalizedScore
		double alpha = minNormalizedScore > 0 ? minNormalizedScore : 1d / n;
		// alpha = 1d / (10 * n);
		double wSum = 0d;
		for (GiftPoint<T> giftPoint : this.getPoints()) {
			if (giftPoint.getCertainty() == 1d)
				continue;

			Double normalizedScore = giftPoint.getPredictedScore() / scoreSum;
			double w = (1 - giftPoint.getCertainty()) * alpha
					+ giftPoint.getCertainty() * normalizedScore;

			wSum += w;
		}

		Frequency f = new Frequency();
		// Post normalization. This ensures recommendability is a probability
		// distribution.
		for (GiftPoint<T> giftPoint : this.getPoints()) {
			if (giftPoint.getCertainty() == 1d)
				continue;

			Double normalizedScore = giftPoint.getPredictedScore() / scoreSum;
			double w = (1 - giftPoint.getCertainty()) * alpha
					+ giftPoint.getCertainty() * normalizedScore;

			double recommendability = w / wSum;
			giftPoint.setRecommendability(recommendability);
			// f.addValue(recommendability);
		}
		// System.out.println(f);

	}

	private double minNormalizedScore(double scoreSum) {
		double minNormalizedScore = Double.MAX_VALUE;
		for (GiftPoint<T> giftPoint : this.getPoints()) {
			if (giftPoint.getCertainty() == 1d)
				continue;

			Double normalizedScore = giftPoint.getPredictedScore() / scoreSum;
			if (normalizedScore < minNormalizedScore) {
				minNormalizedScore = normalizedScore;
			}
		}
		return minNormalizedScore;
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

	private void printImpact(Set<GiftRecommendation<T>> previousRecommendations) {
		for (GiftRecommendation<T> giftRecommendation : previousRecommendations) {
			GiftPoint<T> giftPoint = this.getGiftSpace().get(
					giftRecommendation.getGift());
			Multimap<Double, GiftPoint<T>> distancePoints = LinkedHashMultimap
					.create();
			Collection<GiftPoint<T>> points = this.getGiftSpace().values();
			final MLCategoryJaccardDistance d = new MLCategoryJaccardDistance();
			for (GiftPoint<T> p : points) {
				double distance = d.compute((MLCategory) p.getGift(),
						(MLCategory) giftPoint.getGift());
				distancePoints.put(distance, p);
			}

			List<Double> allDistances = Ordering.natural().sortedCopy(
					distancePoints.keySet());
			for (Double distance : allDistances) {
				Collection<GiftPoint<T>> pointsAtDistance = distancePoints
						.get(distance);
				System.out.println(distance + "(" + pointsAtDistance.size()
						+ ")");
				int n = Math.min(1, points.size());
				int i = 0;
				for (Iterator<GiftPoint<T>> iterator = pointsAtDistance
						.iterator(); iterator.hasNext() && i < n;) {
					GiftPoint<T> q = iterator.next();
					i++;
					System.out.println(q.getCertainty() + ", "
							+ q.getPredictedScore() + ", "
							+ q.getRecommendability());
				}
			}

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

		double scoreDistanceWeightsSum = 0d;
		double certaintyDistanceWeightsSum = 0d;

		double distanceWeightedUserScoreSum = 0d;
		double distanceWeightedCertaintySum = 0d;
		double count = 0;
		for (GiftRecommendation<T> pr : previousRecommendations) {
			GiftPoint<T> y = this.getGiftSpace().get(pr.getGift());
			double xyDistance = this.getMetric().compute(x.getGift(),
					y.getGift());

			double scoreDistanceWeight = this.getScoreKernel()
					.value(xyDistance);
			scoreDistanceWeightsSum += scoreDistanceWeight;
			double distanceWeightedUserScore = scoreDistanceWeight
					* y.getPredictedScore();

			System.out.println(distanceWeightedUserScore);
			double certaintyDistanceWeight = this.getCertaintyKernel().value(
					xyDistance);
			certaintyDistanceWeightsSum += certaintyDistanceWeight;

			double distanceWeightedCertainty = certaintyDistanceWeight
					* y.getCertainty();

			distanceWeightedUserScoreSum += distanceWeightedUserScore;
			distanceWeightedCertaintySum += distanceWeightedCertainty;
			count++;
		}
		double avgDistanceWeightedUserScore = distanceWeightedUserScoreSum
				/ count;
		// double nadarayaWatsonDistanceWeightedUserScore =
		// distanceWeightedUserScoreSum
		// / scoreDistanceWeightsSum;
		double avgDistanceWeightedCertainty = distanceWeightedCertaintySum
				/ count;
		System.out.println(avgDistanceWeightedUserScore);
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

	public UnivariateFunction getScoreKernel() {
		return scoreKernel;
	}

	public void setScoreKernel(UnivariateFunction kernel) {
		this.scoreKernel = kernel;
	}

	public Iterable<GiftPoint<T>> getPoints() {
		return points;
	}

	public void setPoints(Iterable<GiftPoint<T>> points) {
		this.points = points;
	}

	public UnivariateFunction getCertaintyKernel() {
		return certaintyKernel;
	}

	public void setCertaintyKernel(UnivariateFunction certaintyKernel) {
		this.certaintyKernel = certaintyKernel;
	}

}
