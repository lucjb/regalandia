package org.cronopios.regalator;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;

public class NeighboursWithinSphereYesNoGiftRecommender<T> extends KNearestSpheresYesNoGiftRecommender<T> {

	private Metric<GiftRecommendation<T>> metric;

	public NeighboursWithinSphereYesNoGiftRecommender(Collection<T> allGifts, Metric<T> metric, GiftWeighter<T> giftWeighter) {
		super(allGifts, metric, giftWeighter);
		this.setMetric(this.gitRecommendationMetric(metric));
	}

	@Override
	protected Map<GiftRecommendation<T>, Iterable<GiftRecommendation<T>>> indexNeighbourhood() {
		return null;
	}

	@Override
	protected Iterable<GiftRecommendation<T>> neighbourhood(GiftRecommendation<T> giftRecommendation, Set<GiftRecommendation<T>> previousRecommendations) {
		Collection<GiftRecommendation<T>> neighbourhood = Lists.newArrayList();
		for (GiftRecommendation<T> prevRecommendation : previousRecommendations) {
			double distance = this.getMetric().compute(giftRecommendation, prevRecommendation);
			if (distance < 0.6) {
				neighbourhood.add(prevRecommendation);
			}

		}
		return neighbourhood;
	}

	public Metric<GiftRecommendation<T>> getMetric() {
		return metric;
	}

	public void setMetric(Metric<GiftRecommendation<T>> metric) {
		this.metric = metric;
	}

}
