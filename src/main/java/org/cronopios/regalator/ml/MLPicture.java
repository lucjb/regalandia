package org.cronopios.regalator.ml;

public class MLPicture {

	private String id;
	private String url;

	public MLPicture(String id, String url) {
		super();
		this.setId(id);
		this.setUrl(url);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
