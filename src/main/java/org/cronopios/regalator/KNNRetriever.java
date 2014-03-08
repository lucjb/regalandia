package org.cronopios.regalator;

import java.util.Collection;
import java.util.List;
import java.util.SortedMap;

public interface KNNRetriever<T> {

	public List<T> retrieve(T x, int k);

	public SortedMap<Double, Collection<T>> retrieveKNearestSpheres(T x, int k);

}
