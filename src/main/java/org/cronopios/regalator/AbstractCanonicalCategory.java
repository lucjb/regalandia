package org.cronopios.regalator;

import java.util.List;

public abstract class AbstractCanonicalCategory implements CanonicalCategory {

	private double weight;
	private String id;
	private String name;

	@Override
	public String toString() {
		StringBuilder ts = new StringBuilder();
		for (CanonicalCategory ancestor : this.getPathFromRoot()) {
			ts.append(ancestor.getName()).append(">");
		}
		return ts.toString();
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AbstractCanonicalCategory))
			return false;

		AbstractCanonicalCategory other = (AbstractCanonicalCategory) obj;
		return this.id.equals(other.id);
	}

	@Override
	public double weight() {
		return this.getWeight();
	}

	public boolean isFor(String categoryPathElementSubstring) {
		if (this.getName().equals(categoryPathElementSubstring))
			return true;
		if (this.getPathFromRoot() != null) {
			List<? extends CanonicalCategory> path_from_root2 = this
					.getPathFromRoot();
			for (CanonicalCategory cat : path_from_root2) {
				if (cat != this && cat.isFor(categoryPathElementSubstring)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isRoot() {
		return this.getPathFromRoot().size() == 1;
	}

	public boolean isLeaf() {
		return this.getChildren().isEmpty();
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getTitle() {
		return this.getName();
	}

}
