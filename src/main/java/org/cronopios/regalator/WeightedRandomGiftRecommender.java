package org.cronopios.regalator;

import java.util.List;
import java.util.Random;
import java.util.Set;

import com.google.common.base.Predicate;
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

	public WeightedRandomGiftRecommender(List<T> gifts, Metric<T> metric) {
		this.setGifts(Lists.newArrayList(gifts));
		this.setStart(new UniformRandomGiftRecommender<T>(this.getGifts()));
		this.setKnnRetriever(new MetricBasedFullScanKNNRetriever<T>(this
				.getGifts(), metric));
	}

	@Override
	public Set<GiftRecommendation<T>> recommend(
			Set<GiftRecommendation<T>> previousRecommendations, int n) {
		Set<GiftRecommendation<T>> out = Sets.newHashSet();
		removeRejectedCandidates(previousRecommendations);
		double totalScore = 0;
		ListMultimap<GiftRecommendation<T>, T> candidates = LinkedListMultimap
				.create();
		for (GiftRecommendation<T> prevRec : previousRecommendations) {
			if (prevRec.getUserScore() == 0d)
				continue;
			Double userScore = prevRec.getUserScore();
			int kn = (int) Math.round((this.getK() * 1d / userScore));
			List<T> retrieve = this.getKnnRetriever().retrieve(
					prevRec.getGift(), kn);
			candidates.putAll(prevRec, retrieve);
			totalScore += userScore;
		}
		for (GiftRecommendation<T> prevRec : previousRecommendations) {
			int sampleSize = (int) Math.round((n - 1)
					* (prevRec.getUserScore() / totalScore));
			List<T> prevRecCandidates = candidates.get(prevRec);

			for (int i = 0; i < sampleSize; i++) {
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

	private void removeRejectedCandidates(
			Set<GiftRecommendation<T>> previousRecommendations) {
		for (GiftRecommendation<T> prevRec : previousRecommendations) {
			Double userScore = prevRec.getUserScore();
			if (userScore.equals(0d)) {
				List<T> retrieve = this.getKnnRetriever().retrieve(
						prevRec.getGift(), k);
				this.getGifts().removeAll(retrieve);
			}
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

	@Override
	public void filterGifts(Predicate<T> filter) {
		// TODO Auto-generated method stub
		
	}

}
