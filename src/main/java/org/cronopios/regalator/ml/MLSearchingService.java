package org.cronopios.regalator.ml;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.apache.commons.collections.ListUtils;
import org.cronopios.regalator.CanonicalCategory;
import org.cronopios.regalator.GiftItem;
import org.cronopios.regalator.GiftItemSearchingService;

import com.google.common.collect.Lists;
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
	private Random random = new Random(0);
	private Gson gson = new Gson();

	public MLSearchingService() {
	}

	public MLResultsList search(String queryString) throws MeliException, IOException {
		FluentStringsMap params = new FluentStringsMap();
		params.add("q", queryString);
		Response response = this.getMeli().get("/sites/MLA/search", params);
		String responseBody = response.getResponseBody();

		JsonElement root = this.getParser().parse(responseBody);
		JsonObject rootObject = root.getAsJsonObject();
		MLResultsList mlResultsList = gson.fromJson(rootObject, MLResultsList.class);
		return mlResultsList;
	}

	@Override
	public List<? extends GiftItem> search(CanonicalCategory category) {
		try {
			MLResultsList search = this.searchCategory(category);
			List<MLItem> results = search.getResults();
			while (results.isEmpty() && !category.isRoot()) {
				search = this.searchCategory(category.getParent());
				results = search.getResults();
				category = category.getParent();
			}
			if (results.isEmpty())
				return ListUtils.EMPTY_LIST;
			MLItem mlItem = results.get(random.nextInt(results.size()));

			Response response = meli.get("/items/" + mlItem.getId());
			String responseBody = response.getResponseBody();
			JsonElement root = parser.parse(responseBody);
			JsonObject rootObject = root.getAsJsonObject();
			mlItem = gson.fromJson(rootObject, MLItem.class);

			System.out.println("gift item drawn");
			return Lists.newArrayList(mlItem);
		} catch (MeliException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ListUtils.EMPTY_LIST;
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

	public MLResultsList searchCategory(CanonicalCategory category) throws MeliException, IOException {
		FluentStringsMap params = new FluentStringsMap();
		params.add("category", category.getId());
		params.add("condition", "new");
		params.add("limit", "200");
		params.add("power_seller", "true");
		Response response = this.getMeli().get("/sites/MLA/search", params);
		String responseBody = response.getResponseBody();
		JsonElement root = this.getParser().parse(responseBody);
		JsonObject rootObject = root.getAsJsonObject();
		Gson gson = new Gson();
		MLResultsList mlResultsList = gson.fromJson(rootObject, MLResultsList.class);
		return mlResultsList;
	}
}
