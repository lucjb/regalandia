package org.cronopios.regalator;

import java.util.Collection;
import java.util.List;

public interface CanonicalCategory {

	public String getId();

	public String getName();

	public boolean isRoot();

	public boolean isLeaf();

	public Collection<? extends CanonicalCategory> getChildren();

	public List<? extends CanonicalCategory> getPathFromRoot();

	public CanonicalCategory getParent();

	public double weight();

	public boolean isFor(String categoryPathElementSubstring);

}
