import org.apache.commons.lang3.builder.ToStringBuilder;

public class GiftRecommendation<T> {

	private T gift;
	private double recommenderScore;
	private double userScore;

	public GiftRecommendation(T gift, double recommenderScore) {
		this.setGift(gift);
		this.setRecommenderScore(recommenderScore);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public T getGift() {
		return gift;
	}

	public void setGift(T gift) {
		this.gift = gift;
	}

	public double getRecommenderScore() {
		return recommenderScore;
	}

	public void setRecommenderScore(double recommenderScore) {
		this.recommenderScore = recommenderScore;
	}

	public double getUserScore() {
		return userScore;
	}

	public void setUserScore(double userScore) {
		this.userScore = userScore;
	}
}
