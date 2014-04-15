package org.cronopios.regalator;

import java.util.Set;

import com.google.common.base.Predicate;

public interface GiftRecommender<T> {

	public Set<GiftRecommendation<T>> recommend(Set<GiftRecommendation<T>> previousRecommendations, int n);

	public void filterGifts(Predicate<T> filter);

}
