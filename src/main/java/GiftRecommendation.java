import org.apache.commons.lang3.builder.ToStringBuilder;

public class GiftRecommendation<T> {

	private T gift;
	private double recommenderScore;
	private Double userScore;

	public GiftRecommendation(T gift, double recommenderScore) {
		this.setGift(gift);
		this.setRecommenderScore(recommenderScore);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof GiftRecommendation))
			return false;
		GiftRecommendation<T> other = (GiftRecommendation<T>) obj;
		return this.getGift().equals(other.getGift());
	}

	@Override
	public int hashCode() {
		return this.getGift().hashCode();
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

	public Double getUserScore() {
		return userScore;
	}

	public void setUserScore(Double userScore) {
		this.userScore = userScore;
	}
}
