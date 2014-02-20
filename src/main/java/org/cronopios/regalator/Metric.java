package org.cronopios.regalator;
public interface Metric<T> {

	public double compute(T x, T y);

}
