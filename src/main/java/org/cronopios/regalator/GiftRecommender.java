package org.cronopios.regalator;
import java.util.Set;

public interface GiftRecommender<T> {

	public Set<GiftRecommendation<T>> recommend(Set<GiftRecommendation<T>> previousRecommendations, int n);

}
