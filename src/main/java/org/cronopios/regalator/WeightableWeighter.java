package org.cronopios.regalator;

public class WeightableWeighter implements GiftWeighter<CanonicalCategory> {

	@Override
	public double weight(CanonicalCategory gift) {
		return gift.weight();
	}

}
