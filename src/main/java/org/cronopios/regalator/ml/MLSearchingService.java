package org.cronopios.regalator.ml;

import java.io.IOException;
import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.cronopios.regalator.CanonicalCategory;
import org.cronopios.regalator.GiftItem;
import org.cronopios.regalator.GiftItemSearchingService;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mercadolibre.sdk.Meli;
import com.mercadolibre.sdk.MeliException;
import com.ning.http.client.FluentStringsMap;
import com.ning.http.client.Response;

public class MLSearchingService implements GiftItemSearchingService {
	private Meli meli = new Meli((Integer) 0, "");
	private JsonParser parser = new JsonParser();

	public MLSearchingService() {
	}

	public MLResultsList search(String queryString) throws MeliException, IOException {
		FluentStringsMap params = new FluentStringsMap();
		params.add("q", queryString);
		Response response = this.getMeli().get("/sites/MLA/search", params);
		String responseBody = response.getResponseBody();

		JsonElement root = this.getParser().parse(responseBody);
		JsonObject rootObject = root.getAsJsonObject();
		Gson gson = new Gson();
		MLResultsList mlResultsList = gson.fromJson(rootObject, MLResultsList.class);
		return mlResultsList;
	}

	public List<? extends GiftItem> search(CanonicalCategory category) {
		String queryString = this.queryStringForCategory(category);
		try {
			return this.search(queryString).getResults();
		} catch (MeliException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ListUtils.EMPTY_LIST;
	}

	private String queryStringForCategory(CanonicalCategory category) {
		StringBuffer buffer = new StringBuffer();
		for (CanonicalCategory node : category.getPathFromRoot()) {
			buffer.append(node.getName() + " ");
		}
		String queryString = buffer.toString().trim();
		return queryString;
	}

	public Meli getMeli() {
		return meli;
	}

	public void setMeli(Meli meli) {
		this.meli = meli;
	}

	public JsonParser getParser() {
		return parser;
	}

	public void setParser(JsonParser parser) {
		this.parser = parser;
	}

	public MLResultsList searchCategory(MLCategory mlCategory) throws MeliException, IOException {
		String queryString = this.queryStringForCategory(mlCategory);
		return this.search(queryString);
	}
}
