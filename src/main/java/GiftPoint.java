import org.apache.commons.lang3.builder.ToStringBuilder;

public class GiftPoint<T> {

	private T gift;
	private Double predictedScore;
	private Integer uncertainty;

	public GiftPoint(T gift, Double predictedScore, Integer uncertainty) {
		this.setGift(gift);
		this.setPredictedScore(predictedScore);
		this.setUncertainty(uncertainty);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public int hashCode() {
		return this.getGift().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof GiftPoint)) {
			return false;
		}
		GiftPoint<T> other = (GiftPoint<T>) obj;
		return this.getGift().equals(other);
	}

	public T getGift() {
		return gift;
	}

	public void setGift(T gift) {
		this.gift = gift;
	}

	public Double getPredictedScore() {
		return predictedScore;
	}

	public void setPredictedScore(Double predictedScore) {
		this.predictedScore = predictedScore;
	}

	public Integer getUncertainty() {
		return uncertainty;
	}

	public void setUncertainty(Integer uncertainty) {
		this.uncertainty = uncertainty;
	}

}
