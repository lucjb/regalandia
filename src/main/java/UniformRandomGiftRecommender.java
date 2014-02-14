import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

public class UniformRandomGiftRecommender<T> implements GiftRecommender<T> {
	private List<T> gifts;
	private Random random = new Random();

	public UniformRandomGiftRecommender(List<T> gifts) {
		this.setGifts(gifts);
	}

	@Override
	public List<GiftRecommendation<T>> recommend(List<GiftRecommendation<T>> previousRecommendations, int n) {
		List<GiftRecommendation<T>> recs = Lists.newArrayList();
		for (int i = 0; i < n; i++) {
			T gift = this.getGifts().get(this.getRandom().nextInt(this.getGifts().size()));
			recs.add(new GiftRecommendation<T>(gift, 1d));
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
