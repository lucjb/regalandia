package org.cronopios.regalator.ml;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.cronopios.regalator.GiftItem;

public class MLItem implements GiftItem {

	private String id;
	private String title;
	private String subtitle;
	private Double price;
	private int available_quantity;
	private int sold_quantity;
	private String thumbnail;
	private String permalink;
	private String condition;
	private String listing_type_id;
	private String category_id;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public int getAvailable_quantity() {
		return available_quantity;
	}

	public void setAvailable_quantity(int available_quantity) {
		this.available_quantity = available_quantity;
	}

	public int getSold_quantity() {
		return sold_quantity;
	}

	public void setSold_quantity(int sold_quantity) {
		this.sold_quantity = sold_quantity;
	}

	@Override
	public String getImage() {
		return this.getThumbnail();
	}

	@Override
	public String getExternalURL() {
		return this.getPermalink();
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getPermalink() {
		return permalink;
	}

	public void setPermalink(String permalink) {
		this.permalink = permalink;
	}

	public String getListing_type_id() {
		return listing_type_id;
	}

	public void setListing_type_id(String listing_type_id) {
		this.listing_type_id = listing_type_id;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getCategory_id() {
		return category_id;
	}

	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}

}
