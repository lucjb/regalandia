import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

public class WeightedRandomGiftRecommender<T> implements GiftRecommender<T> {

	private List<T> gifts;
	private Random random = new Random();
	private UniformRandomGiftRecommender<T> start;
	private KNNRetriever<T> knnRetriever;
	private int k = 5;

	public WeightedRandomGiftRecommender(List<T> gifts, KNNRetriever<T> knnRetriever) {
		this.setGifts(gifts);
		this.setStart(new UniformRandomGiftRecommender<T>(gifts));
		this.setKnnRetriever(knnRetriever);
	}

	@Override
	public List<GiftRecommendation<T>> recommend(List<GiftRecommendation<T>> previousRecommendations, int n) {
		if (previousRecommendations.isEmpty()) {
			return this.getStart().recommend(previousRecommendations, n);
		} else {
			List<GiftRecommendation<T>> out = Lists.newArrayList();
			ListMultimap<GiftRecommendation<T>, T> candidates = LinkedListMultimap.create();
			double totalScore = 0;
			for (GiftRecommendation<T> prevRec : previousRecommendations) {
				List<T> retrieve = this.getKnnRetriever().retrieve(prevRec.getGift(), (int) (this.getK() * 1d / prevRec.getUserScore()));
				candidates.putAll(prevRec, retrieve);
				totalScore += prevRec.getUserScore();
			}
			for (GiftRecommendation<T> prevRec : previousRecommendations) {
				int sampleSize = (int) (n * (prevRec.getUserScore() / totalScore));
				for (int i = 0; i < sampleSize; i++) {
					List<T> prevRecCandidates = candidates.get(prevRec);
					T gift = prevRecCandidates.get(this.getRandom().nextInt(prevRecCandidates.size()));
					prevRecCandidates.remove(gift);
					out.add(new GiftRecommendation(gift, 1));
				}
			}
			return out;
		}

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

	public UniformRandomGiftRecommender<T> getStart() {
		return start;
	}

	public void setStart(UniformRandomGiftRecommender<T> start) {
		this.start = start;
	}

	public KNNRetriever<T> getKnnRetriever() {
		return knnRetriever;
	}

	public void setKnnRetriever(KNNRetriever<T> knnRetriever) {
		this.knnRetriever = knnRetriever;
	}

	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}

}
