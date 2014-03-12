package org.cronopios.regalator.icecat;

import java.util.Collection;
import java.util.List;

import org.cronopios.regalator.AbstractCanonicalCategory;

import com.google.common.collect.Lists;

public class IceCatCategory extends AbstractCanonicalCategory {

	private String description;
	private String lowPic;
	/**
	 * "Score" attribute in the response reflects the category usage statistic.
	 * The higher number means the higher usage level.
	 */
	private int score;
	/**
	 * 1 This category may be used for product lookup in product list lookup
	 * request 0 This category is not made searchable (in our own product finder
	 * tools)
	 */
	private boolean searchable;
	private String thumbnailPic;
	private String uncatid;
	private boolean visible;
	private String parentCategoryId;
	private int level = -1;

	private IceCatCategory parent;
	private Collection<IceCatCategory> children = Lists.newArrayList();
	private List<IceCatCategory> pathFromRoot = Lists.newArrayList();

	public IceCatCategory() {
	}

	public IceCatCategory(int id, String lowPic, int score, boolean searchable,
			String thumbnailPic, String uncatid, boolean visible,
			String description, String name, int parentCategoryId, int level) {
		super();
		this.setId(String.valueOf(id));
		this.setName(name);
		this.lowPic = lowPic;
		this.score = score;
		this.searchable = searchable;
		this.thumbnailPic = thumbnailPic;
		this.uncatid = uncatid;
		this.visible = visible;
		this.description = description;
		this.parentCategoryId = String.valueOf(parentCategoryId);
		this.level = level;
	}

	public String getLowPic() {
		return lowPic;
	}

	public void setLowPic(String lowPic) {
		this.lowPic = lowPic;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public boolean isSearchable() {
		return searchable;
	}

	public void setSearchable(boolean searchable) {
		this.searchable = searchable;
	}

	public String getThumbnailPic() {
		return thumbnailPic;
	}

	public void setThumbnailPic(String thumbnailPic) {
		this.thumbnailPic = thumbnailPic;
	}

	public String getUncatid() {
		return uncatid;
	}

	public void setUncatid(String uncatid) {
		this.uncatid = uncatid;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public IceCatCategory getParent() {
		return parent;
	}

	public void setParent(IceCatCategory parent) {
		this.parent = parent;
	}

	public Collection<IceCatCategory> getChildren() {
		return children;
	}

	public void setChildren(Collection<IceCatCategory> children) {
		this.children = children;
	}

	public List<IceCatCategory> getPathFromRoot() {
		return pathFromRoot;
	}

	public void setPathFromRoot(List<IceCatCategory> pathFromRoot) {
		this.pathFromRoot = pathFromRoot;
	}

	public String getParentCategoryId() {
		return parentCategoryId;
	}

	public void setParentCategoryId(String parentCategoryId) {
		this.parentCategoryId = parentCategoryId;
	}

	@Override
	public boolean isRoot() {
		return this.getParent() == null;
	}

	@Override
	public double weight() {
		return super.weight() * this.getScore();
	}
}
