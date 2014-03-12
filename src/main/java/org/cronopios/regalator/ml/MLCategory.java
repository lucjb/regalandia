package org.cronopios.regalator.ml;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.cronopios.regalator.AbstractCanonicalCategory;
import org.cronopios.regalator.CanonicalCategory;

import com.google.common.collect.Sets;

public class MLCategory extends AbstractCanonicalCategory {

	private List<MLCategory> path_from_root;
	private Set<MLCategory> children_categories = Sets.newLinkedHashSet();
	private int total_items_in_this_category;

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
		return this.getPath_from_root()
				.get(this.getPath_from_root().size() - 2);
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
		return super.weight() * this.getTotal_items_in_this_category();
	}

}
