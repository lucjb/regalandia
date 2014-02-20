package org.cronopios.regalator;
public interface SimilarityMeasure<T> {

	public double computeSimilarity(T x, T y);

}
