package org.cronopios.regalator;

import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class KNearestSpheresYesNoGiftRecommender<T> implements
		GiftRecommender<T> {

	private Random random = new Random();
	private KNNRetriever<GiftRecommendation<T>> knnRetriever;
	private Collection<GiftRecommendation<T>> space = Lists.newArrayList();
	private Map<GiftRecommendation<T>, Iterable<GiftRecommendation<T>>> kNearestSpheres;
	private GiftWeighter<T> giftWeighter;

	public KNearestSpheresYesNoGiftRecommender(Collection<T> allGifts,
			final Metric<T> metric, GiftWeighter<T> giftWeighter) {
		Collection<GiftRecommendation<T>> space = Lists.newArrayList();
		for (T gift : allGifts) {
			GiftRecommendation<T> giftRecommendation = new GiftRecommendation<T>(
					gift, -1d);
			space.add(giftRecommendation);
		}
		this.setSpace(space);
		this.setGiftWeighter(giftWeighter);
		this.setKnnRetriever(new MetricBasedFullScanKNNRetriever<GiftRecommendation<T>>(
				space, this.gitRecommendationMetric(metric)));

		this.setkNearestSpheres(this.indexNeighbourhood());
		System.out.println("K nearest spheres cached.");
	}

	protected Metric<GiftRecommendation<T>> gitRecommendationMetric(
			final Metric<T> metric) {
		return new Metric<GiftRecommendation<T>>() {

			@Override
			public double compute(GiftRecommendation<T> x,
					GiftRecommendation<T> y) {
				return metric.compute(x.getGift(), y.getGift());
			}
		};
	}

	@Override
	public Set<GiftRecommendation<T>> recommend(
			Set<GiftRecommendation<T>> previousRecommendations, int n) {

		for (GiftRecommendation<T> giftRecommendation : previousRecommendations) {
			giftRecommendation.setRecommenderScore(0d);
		}

		for (GiftRecommendation<T> giftRecommendation : this.getSpace()) {
			if (giftRecommendation.getUserScore() == null) {
				this.computeRecommenderScore(giftRecommendation,
						previousRecommendations);
			}
		}
		this.normalizeRecommenderScore();

		return this.drawRecommendations(n, this.makeGiftDistribution());
	}

	private Set<GiftRecommendation<T>> drawRecommendations(
			int n,
			SimpleCategoricalProbabilityDistribution<GiftRecommendation<T>> probabilityDistribution) {
		Set<GiftRecommendation<T>> out = Sets.newHashSet();
		for (int i = 0; i < n; i++) {
			GiftRecommendation<T> next = probabilityDistribution.next();
			out.add(next);
		}

		return out;
	}

	private SimpleCategoricalProbabilityDistribution<GiftRecommendation<T>> makeGiftDistribution() {
		Iterable<GiftRecommendation<T>> points = this.getSpace();
		Map<GiftRecommendation<T>, Double> pmf = Maps.newHashMap();
		for (GiftRecommendation<T> giftPoint : points) {
			pmf.put(giftPoint, giftPoint.getRecommenderScore());
		}
		SimpleCategoricalProbabilityDistribution<GiftRecommendation<T>> probabilityDistribution = new SimpleCategoricalProbabilityDistribution<GiftRecommendation<T>>(
				pmf, this.getRandom());

		return probabilityDistribution;

	}

	protected void computeRecommenderScore(
			GiftRecommendation<T> giftRecommendation,
			Set<GiftRecommendation<T>> previousRecommendations) {
		Iterable<GiftRecommendation<T>> neighbours = this.neighbourhood(
				giftRecommendation, previousRecommendations);
		double yesCount = 0;
		double dontKnowCount = 0;
		double total = 0;

		for (GiftRecommendation<T> neighbour : neighbours) {
			if (neighbour.getUserScore() == null) {
				dontKnowCount++;
			} else if (neighbour.getUserScore() > 0.5) {
				yesCount++;
			}
			total++;
		}
		double recommenderScore = yesCount / (total);
		if (total == dontKnowCount) {
			recommenderScore = 1d / this.getSpace().size();
		}
		recommenderScore = recommenderScore
				* this.getGiftWeighter().weight(giftRecommendation.getGift());
		giftRecommendation.setRecommenderScore(recommenderScore);
	}

	protected Iterable<GiftRecommendation<T>> neighbourhood(
			GiftRecommendation<T> giftRecommendation,
			Set<GiftRecommendation<T>> previousRecommendations) {
		Iterable<GiftRecommendation<T>> neighbours = this.getkNearestSpheres()
				.get(giftRecommendation);
		return neighbours;
	}

	private void normalizeRecommenderScore() {
		double totalScore = 0;
		double totalNormalizedScore = 0;

		double n = 0;
		for (GiftRecommendation<T> giftRecommendation : this.getSpace()) {
			totalScore += giftRecommendation.getRecommenderScore();
			n++;
		}
		for (GiftRecommendation<T> giftRecommendation : this.getSpace()) {
			double recommenderScore = giftRecommendation.getRecommenderScore()
					/ totalScore;

			if (totalScore == 0) {
				recommenderScore = 1d / n;
			}
			giftRecommendation.setRecommenderScore(recommenderScore);
			totalNormalizedScore += recommenderScore;
		}
		System.out.println(totalNormalizedScore);

	}

	protected Map<GiftRecommendation<T>, Iterable<GiftRecommendation<T>>> indexNeighbourhood() {
		Map<GiftRecommendation<T>, Iterable<GiftRecommendation<T>>> neighbourhood = Maps
				.newLinkedHashMap();
		for (GiftRecommendation<T> giftRecommendation : this.getSpace()) {
			SortedMap<Double, Collection<GiftRecommendation<T>>> kNearestSpheres = this
					.getKnnRetriever().retrieveKNearestSpheres(
							giftRecommendation, 2);

			Collection<Collection<GiftRecommendation<T>>> spheres = kNearestSpheres
					.values();
			Iterable<GiftRecommendation<T>> neighbours = Iterables
					.concat(spheres);

			neighbourhood.put(giftRecommendation, neighbours);
		}
		return neighbourhood;
	}

	public KNNRetriever<GiftRecommendation<T>> getKnnRetriever() {
		return knnRetriever;
	}

	public void setKnnRetriever(KNNRetriever<GiftRecommendation<T>> knnRetriever) {
		this.knnRetriever = knnRetriever;
	}

	public Collection<GiftRecommendation<T>> getSpace() {
		return space;
	}

	public void setSpace(Collection<GiftRecommendation<T>> space) {
		this.space = space;
	}

	public Map<GiftRecommendation<T>, Iterable<GiftRecommendation<T>>> getkNearestSpheres() {
		return kNearestSpheres;
	}

	public void setkNearestSpheres(
			Map<GiftRecommendation<T>, Iterable<GiftRecommendation<T>>> kNearestSpheres) {
		this.kNearestSpheres = kNearestSpheres;
	}

	public Random getRandom() {
		return random;
	}

	public void setRandom(Random random) {
		this.random = random;
	}

	public GiftWeighter<T> getGiftWeighter() {
		return giftWeighter;
	}

	public void setGiftWeighter(GiftWeighter<T> giftWeighter) {
		this.giftWeighter = giftWeighter;
	}

}