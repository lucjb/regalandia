package org.cronopios.regalator.ml;

import java.util.Collection;

import org.cronopios.regalator.filters.AbstractCanonicalCategoryFilter;

import com.google.common.collect.Lists;

public class MLVipSubDomainFilter extends AbstractCanonicalCategoryFilter<MLCategory> {

	private String vipSubDomain;

	public MLVipSubDomainFilter(String vipSubDomain) {
		this.setVipSubDomain(vipSubDomain);
	}

	@Override
	public Collection<? extends MLCategory> selectCategoriesToRemove(Collection<? extends MLCategory> mlCategories) {
		Collection<MLCategory> toRemove = Lists.newLinkedList();
		for (MLCategory mlCategory : mlCategories) {
			if (mlCategory.getSettings().getVip_subdomain().equals((this.getVipSubDomain()))) {
				toRemove.add(mlCategory);
			}
		}
		return toRemove;
	}

	public String getVipSubDomain() {
		return vipSubDomain;
	}

	public void setVipSubDomain(String vipSubDomain) {
		this.vipSubDomain = vipSubDomain;
	}
}
