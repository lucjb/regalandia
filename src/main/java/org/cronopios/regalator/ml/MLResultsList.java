package org.cronopios.regalator.ml;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class MLResultsList {

	private List<MLItem> results;

	private MLPaging paging;

	public List<MLItem> getResults() {
		return results;
	}

	public void setResults(List<MLItem> results) {
		this.results = results;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public MLPaging getPaging() {
		return paging;
	}

	public void setPaging(MLPaging paging) {
		this.paging = paging;
	}

}
