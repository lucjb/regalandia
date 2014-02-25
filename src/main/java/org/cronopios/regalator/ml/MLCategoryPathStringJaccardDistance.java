package org.cronopios.regalator.ml;

import org.apache.commons.lang3.StringUtils;
import org.cronopios.regalator.Metric;

public class MLCategoryPathStringJaccardDistance implements Metric<MLCategory> {

	@Override
	public double compute(MLCategory x, MLCategory y) {
		String xPathString = x.getPathString();
		String yPathString = y.getPathString();
		double levenshteinDistance = StringUtils.getLevenshteinDistance(xPathString, yPathString);
		double max = Math.max(xPathString.length(), yPathString.length());
		return levenshteinDistance / max;
	}

}
