import java.util.List;

public interface GiftRecommender<T> {

	public List<GiftRecommendation<T>> recommend(List<GiftRecommendation<T>> previousRecommendations, int n);

}
