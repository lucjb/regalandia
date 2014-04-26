package org.cronopios.regalator;

import java.util.List;

public interface GiftItemSearchingService {

	public List<? extends GiftItem> search(CanonicalCategory category, Integer minPrice, Integer maxPrice);
}
