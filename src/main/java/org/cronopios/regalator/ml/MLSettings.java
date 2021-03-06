package org.cronopios.regalator.ml;

import java.util.List;

public class MLSettings {
	private List<String> tags;
	private String mirror_category;
	private String vip_subdomain;
	private boolean adult_content;
	private boolean listing_allowed;
	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public String getMirror_category() {
		return mirror_category;
	}

	public void setMirror_category(String mirror_category) {
		this.mirror_category = mirror_category;
	}

	public String getVip_subdomain() {
		return vip_subdomain;
	}

	public void setVip_subdomain(String vip_subdomain) {
		this.vip_subdomain = vip_subdomain;
	}

	public boolean isAdult_content() {
		return adult_content;
	}

	public void setAdult_content(boolean adult_content) {
		this.adult_content = adult_content;
	}

	public boolean isListing_allowed() {
		return listing_allowed;
	}

	public void setListing_allowed(boolean listing_allowed) {
		this.listing_allowed = listing_allowed;
	}

}
