package org.cronopios.regalator.ml;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

public class MLCategory {

	private String id;
	private String name;
	private List<MLCategory> path_from_root;
	private String pathString;
	private Set<MLCategory> children_categories = Sets.newLinkedHashSet();
	private int total_items_in_this_category;

	@Override
	public String toString() {
		StringBuilder ts = new StringBuilder();
		for (MLCategory ancesotor : this.path_from_root) {
			ts.append(ancesotor.name).append(">");
		}
		return ts.toString();

	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MLCategory))
			return false;

		MLCategory other = (MLCategory) obj;
		return this.id.equals(other.id);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<MLCategory> getPath_from_root() {
		return path_from_root;
	}

	public void setPath_from_root(List<MLCategory> path_from_root) {
		this.path_from_root = path_from_root;
	}

	public String getPathString() {
		if (this.pathString == null) {
			this.buildPathString();
		}
		return pathString;
	}

	private void buildPathString() {
		StringBuffer pathStringBuffer = new StringBuffer();
		for (MLCategory pathElement : this.getPath_from_root()) {
			pathStringBuffer.append(pathElement.getName() + ">");
		}
		this.setPathString(pathStringBuffer.toString());
	}

	public void setPathString(String pathString) {
		this.pathString = pathString;
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

	public boolean isFor(String categoryPathElementSubstring) {
		if (this.getName().equals(categoryPathElementSubstring))
			return true;
		if (this.getPath_from_root() != null) {
			List<MLCategory> path_from_root2 = this.getPath_from_root();
			for (MLCategory mlCategory : path_from_root2) {
				if (mlCategory != this
						&& mlCategory.isFor(categoryPathElementSubstring)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isRoot() {
		return this.getPath_from_root().size() == 1;
	}

	public boolean isLeaf() {
		return this.getChildren_categories().isEmpty();
	}

	public MLCategory getParent() {
		if (this.getPath_from_root().isEmpty())
			return null;
		return this.getPath_from_root()
				.get(this.getPath_from_root().size() - 1);
	}
}
