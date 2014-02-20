package org.cronopios.regalator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.Sets;

public class UniformRandomGiftRecommender<T> implements GiftRecommender<T> {
	private List<T> gifts;
	private Random random = new Random(0);

	public UniformRandomGiftRecommender(List<T> gifts) {
		this.setGifts(gifts);
	}

	@Override
	public Set<GiftRecommendation<T>> recommend(Set<GiftRecommendation<T>> previousRecommendations, int n) {
		Set<GiftRecommendation<T>> recs = Sets.newHashSet();
		for (int i = 0; i < n; i++) {
			T gift = this.getGifts().get(this.getRandom().nextInt(this.getGifts().size()));
			recs.add(new GiftRecommendation<T>(gift, 1d));
			this.getGifts().remove(gift);
		}
		return recs;
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

}
