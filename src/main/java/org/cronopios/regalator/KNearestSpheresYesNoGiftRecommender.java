package org.cronopios.regalator;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;

import org.apache.commons.math3.stat.Frequency;
import org.cronopios.regalator.filters.CategoryStringFilter;
import org.cronopios.regalator.ml.MLTagsPredicate;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class KNearestSpheresYesNoGiftRecommender<T> implements GiftRecommender<T> {

	private Random random = new Random();
	private KNNRetriever<GiftRecommendation<T>> knnRetriever;
	private Collection<GiftRecommendation<T>> space = Lists.newArrayList();
	private Map<GiftRecommendation<T>, Iterable<GiftRecommendation<T>>> kNearestSpheres;
	private GiftWeighter<T> giftWeighter;

	public KNearestSpheresYesNoGiftRecommender(Collection<T> allGifts, final Metric<T> metric, GiftWeighter<T> giftWeighter) {
		Collection<GiftRecommendation<T>> space = Lists.newArrayList();
		for (T gift : allGifts) {
			GiftRecommendation<T> giftRecommendation = new GiftRecommendation<T>(gift, -1d);
			space.add(giftRecommendation);
		}
		this.setSpace(space);
		this.setGiftWeighter(giftWeighter);
		this.setKnnRetriever(new MetricBasedFullScanKNNRetriever<GiftRecommendation<T>>(space, this.gitRecommendationMetric(metric)));

		this.setkNearestSpheres(this.indexNeighbourhood());
		System.out.println("K nearest spheres cached.");
	}

	protected Metric<GiftRecommendation<T>> gitRecommendationMetric(final Metric<T> metric) {
		return new Metric<GiftRecommendation<T>>() {

			@Override
			public double compute(GiftRecommendation<T> x, GiftRecommendation<T> y) {
				return metric.compute(x.getGift(), y.getGift());
			}
		};
	}

	@Override
	public Set<GiftRecommendation<T>> recommend(Set<GiftRecommendation<T>> previousRecommendations, int n) {
		System.out.println("previousRecommendations: " + +previousRecommendations.size());

		for (GiftRecommendation<T> giftRecommendation : this.getSpace()) {
			if (giftRecommendation.getUserScore() == null) {
				this.computeRecommenderScore(giftRecommendation, previousRecommendations);
			}
		}
		for (GiftRecommendation<T> giftRecommendation : previousRecommendations) {
			double weight = this.getGiftWeighter().weight(giftRecommendation.getGift());
			giftRecommendation.setRecommenderScore(weight / this.getSpace().size());
		}
		this.normalizeRecommenderScore();

		return this.drawRecommendations(n, this.makeGiftDistribution());
	}

	private void boostSelected() {
		int count = 0;

		Collection<GiftRecommendation<T>> space2 = this.getSpace();
		Predicate<CanonicalCategory> filter = Predicates.or(new MLTagsPredicate("girl"), new CategoryStringFilter("Niñas"));
		for (Iterator<GiftRecommendation<T>> iterator = space2.iterator(); iterator.hasNext();) {
			GiftRecommendation<T> giftRecommendation = iterator.next();
			T gift = giftRecommendation.getGift();
			CanonicalCategory gift2 = (CanonicalCategory) gift;
			if (filter.apply(gift2)) {
				double weight = this.getGiftWeighter().weight(giftRecommendation.getGift());

				giftRecommendation.setRecommenderScore(500d * weight / this.getSpace().size());
				count++;
			}
		}
		System.out.println(filter + " filtered " + count + " gifts.");
	}

	private Set<GiftRecommendation<T>> drawRecommendations(int n, SimpleCategoricalProbabilityDistribution<GiftRecommendation<T>> probabilityDistribution) {
		Set<GiftRecommendation<T>> out = Sets.newHashSet();
		while (out.size() < n) {
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
		SimpleCategoricalProbabilityDistribution<GiftRecommendation<T>> probabilityDistribution = new SimpleCategoricalProbabilityDistribution<GiftRecommendation<T>>(pmf, this.getRandom());

		return probabilityDistribution;

	}

	protected double computeRecommenderScore(GiftRecommendation<T> giftRecommendation, Set<GiftRecommendation<T>> previousRecommendations) {
		Iterable<GiftRecommendation<T>> neighbours = this.neighbourhood(giftRecommendation, previousRecommendations);
		double yesCount = 0;
		double noCount = 0;
		double dontKnowCount = 0;
		double total = 0;

		for (GiftRecommendation<T> neighbour : neighbours) {
			if (neighbour.getUserScore() == null) {
				dontKnowCount++;
			} else if (neighbour.getUserScore() > 0.5) {
				yesCount++;
			} else {
				noCount++;
			}
			total++;
		}
		double weight = this.getGiftWeighter().weight(giftRecommendation.getGift());
		double recommenderScore = giftRecommendation.getRecommenderScore();
		if (total == dontKnowCount) {
			recommenderScore = 1d / this.getSpace().size() * weight;
		} else if (yesCount > (yesCount + noCount) / 2d) {
			recommenderScore = giftRecommendation.getRecommenderScore() * 10d * weight;
		} else if (noCount > (yesCount + noCount) / 2d) {
			recommenderScore = giftRecommendation.getRecommenderScore() * 0.1 * weight;
		}
		giftRecommendation.setRecommenderScore(recommenderScore);
		return total;
	}

	protected Iterable<GiftRecommendation<T>> neighbourhood(GiftRecommendation<T> giftRecommendation, Set<GiftRecommendation<T>> previousRecommendations) {
		Iterable<GiftRecommendation<T>> neighbours = this.getkNearestSpheres().get(giftRecommendation);
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
			double recommenderScore = giftRecommendation.getRecommenderScore() / totalScore;

			if (totalScore == 0) {
				recommenderScore = 1d / n;
			}
			giftRecommendation.setRecommenderScore(recommenderScore);
			totalNormalizedScore += recommenderScore;
		}
		System.out.println(totalNormalizedScore);

	}

	protected Map<GiftRecommendation<T>, Iterable<GiftRecommendation<T>>> indexNeighbourhood() {
		Frequency f = new Frequency();
		Map<GiftRecommendation<T>, Iterable<GiftRecommendation<T>>> neighbourhood = Maps.newLinkedHashMap();
		for (GiftRecommendation<T> giftRecommendation : this.getSpace()) {
			SortedMap<Double, Collection<GiftRecommendation<T>>> kNearestSpheres = this.getKnnRetriever().retrieveKNearestSpheres(giftRecommendation, 3);

			Collection<Collection<GiftRecommendation<T>>> spheres = kNearestSpheres.values();
			Iterable<GiftRecommendation<T>> neighbours = Iterables.concat(spheres);

			neighbourhood.put(giftRecommendation, neighbours);
			f.addValue(Iterables.size(neighbours));
		}
		System.out.println("negih" + f);
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

	public void setkNearestSpheres(Map<GiftRecommendation<T>, Iterable<GiftRecommendation<T>>> kNearestSpheres) {
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

	@Override
	public void filterGifts(Predicate<T> filter) {
		int count = 0;
		Collection<GiftRecommendation<T>> space2 = this.getSpace();
		for (Iterator<GiftRecommendation<T>> iterator = space2.iterator(); iterator.hasNext();) {
			GiftRecommendation<T> giftRecommendation = iterator.next();
			T gift = giftRecommendation.getGift();
			if (filter.apply(gift)) {
				iterator.remove();
				count++;
			}
		}
		System.out.println(filter + " filtered " + count + " gifts.");
	}

}
