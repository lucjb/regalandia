import java.util.List;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class WeightedRandomGiftRecommender<T> implements GiftRecommender<T> {

	private List<T> gifts;
	private Random random = new Random(System.currentTimeMillis());
	private UniformRandomGiftRecommender<T> start;
	private KNNRetriever<T> knnRetriever;
	private int k = 5;

	public WeightedRandomGiftRecommender(List<T> gifts,
			SimilarityMeasure<T> similarityMeasure) {
		this.setGifts(Lists.newArrayList(gifts));
		this.setStart(new UniformRandomGiftRecommender<T>(this.getGifts()));
		this.setKnnRetriever(new FullScanKNNRetriever<T>(this.getGifts(),
				similarityMeasure));
	}

	@Override
	public Set<GiftRecommendation<T>> recommend(
			Set<GiftRecommendation<T>> previousRecommendations, int n) {
		Set<GiftRecommendation<T>> out = Sets.newHashSet();
		ListMultimap<GiftRecommendation<T>, T> candidates = LinkedListMultimap
				.create();
		double totalScore = 0;
		for (GiftRecommendation<T> prevRec : previousRecommendations) {
			Double userScore = prevRec.getUserScore();
			if (userScore.equals(0d))
				continue;
			int kn = (int) Math.round((this.getK() * 1d / userScore));
			List<T> retrieve = this.getKnnRetriever().retrieve(
					prevRec.getGift(), kn);
			candidates.putAll(prevRec, retrieve);
			totalScore += userScore;

		}
		for (GiftRecommendation<T> prevRec : previousRecommendations) {
			int sampleSize = (int) Math.round((n - 1)
					* (prevRec.getUserScore() / totalScore));

			for (int i = 0; i < sampleSize; i++) {
				List<T> prevRecCandidates = candidates.get(prevRec);
				T gift = prevRecCandidates.get(this.getRandom().nextInt(
						prevRecCandidates.size()));
				prevRecCandidates.remove(gift);
				out.add(new GiftRecommendation<T>(gift, 1));
				this.getGifts().remove(gift);
			}
		}
		out.addAll(this.getStart().recommend(previousRecommendations,
				n - out.size()));
		return out;

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
