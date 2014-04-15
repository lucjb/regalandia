package org.cronopios.regalator.ml;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.ListUtils;
import org.cronopios.regalator.AbstractCanonicalCategory;
import org.cronopios.regalator.CanonicalCategory;

import com.google.common.collect.Sets;

public class MLCategory extends AbstractCanonicalCategory {

	private List<MLCategory> path_from_root;
	private Set<MLCategory> children_categories = Sets.newLinkedHashSet();
	private int total_items_in_this_category;
	private String picture;
	private MLSettings settings;
	private List<String> regalableItems = ListUtils.EMPTY_LIST;

	@Override
	public String toString() {
		return super.toString() + " regalables: " + this.getRegalableItems().size();
	}

	public List<MLCategory> getPath_from_root() {
		return path_from_root;
	}

	public void setPath_from_root(List<MLCategory> path_from_root) {
		this.path_from_root = path_from_root;
	}

	public int getTotal_items_in_this_category() {
		return total_items_in_this_category;
	}

	public void setTotal_items_in_this_category(int total_items_in_this_category) {
		this.total_items_in_this_category = total_items_in_this_category;
	}

	public Set<MLCategory> getChildren_categories() {
		return children_categories;
	}

	public void setChildren_categories(Set<MLCategory> children_categories) {
		this.children_categories = children_categories;
	}

	public MLCategory getParent() {
		if (this.isRoot())
			return null;
		return this.getPath_from_root().get(this.getPath_from_root().size() - 2);
	}

	@Override
	public Collection<? extends CanonicalCategory> getChildren() {
		return this.getChildren_categories();
	}

	@Override
	public List<? extends CanonicalCategory> getPathFromRoot() {
		return this.getPath_from_root();
	}

	@Override
	public double weight() {
		return super.weight();
	}

	private String ancestorsPicture = "";

	@Override
	public String getImageURL() {
		if (this.getAncestorsPicture() == "") {
			this.setAncestorsPicture(null);
			List<MLCategory> path_from_root2 = this.getPath_from_root();
			for (int i = path_from_root2.size() - 1; i > -1; i--) {
				String p = path_from_root2.get(i).getPicture();
				if (p != null) {
					this.setAncestorsPicture(p);
					return this.getAncestorsPicture();
				}
			}
		}
		return this.getAncestorsPicture();
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public MLSettings getSettings() {
		return settings;
	}

	public void setSettings(MLSettings settings) {
		this.settings = settings;
	}

	public String getAncestorsPicture() {
		return ancestorsPicture;
	}

	public void setAncestorsPicture(String ancestorsPicture) {
		this.ancestorsPicture = ancestorsPicture;
	}

	public List<String> getRegalableItems() {
		return regalableItems;
	}

	public void setRegalableItems(List<String> regalableItems) {
		this.regalableItems = regalableItems;
	}

}
