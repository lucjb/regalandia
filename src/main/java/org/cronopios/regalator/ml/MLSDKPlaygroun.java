package org.cronopios.regalator.ml;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mercadolibre.sdk.Meli;
import com.mercadolibre.sdk.MeliException;
import com.ning.http.client.FluentStringsMap;
import com.ning.http.client.Response;

public class MLSDKPlaygroun {

	public static void main(String[] args) throws MeliException, IOException {
		Multimap<String, String> catArts = HashMultimap.create();

		Meli meli = new Meli((Integer) 0, "");

		int[] sellerIds = new int[] { 29591208, 5157944, 14648404, 148645467,
				135125616, 59231372, 304925, 54295232, 203079, 77852900,
				15280737, 47316577, 77065824, 131662738, 12811571, 140948195,
				24882862, 95680760, 83918312, 3255473, 35370149, 39144101,
				51810624, 76301487, 53059479, 153418288, 95337832, 86110765,
				127176707, 97562082, 47442520, 55101916, 54310549, 82564432,
				171023, 90062782, 21884494, 27138001, 96372774, 110242209,
				42932597, 78714086, 26321374, 74609318, 52791362, 22004047,
				59408460, 54951059, 18596327 };
		// int[] sellerIds = new int[] { 27138001, 96372774 };

		JsonParser parser = new JsonParser();
		Gson gson = new Gson();
		for (int i = 0; i < sellerIds.length; i++) {
			List<MLItem> allItems = Lists.newArrayList();
			String sellerId = String.valueOf(sellerIds[i]);

			MLResultsList mlResultsList = fetch(meli, parser, gson, sellerId, 0);
			allItems.addAll(mlResultsList.getResults());
			int total = mlResultsList.getPaging().getTotal();

			for (int j = 1; allItems.size() < total; j++) {
				mlResultsList = fetch(meli, parser, gson, sellerId, j);
				allItems.addAll(mlResultsList.getResults());
			}

			System.out.println(allItems.size() + " total " + total);
			for (MLItem mlItem : allItems) {
				String id = mlItem.getId();
				Response response = meli.get("/items/" + id);
				String responseBody = response.getResponseBody();
				JsonElement root = parser.parse(responseBody);
				JsonObject rootObject = root.getAsJsonObject();
				MLItem item = gson.fromJson(rootObject, MLItem.class);
				System.out.println(item.getCategory_id());
				catArts.put(item.getCategory_id(), id);
			}
		}
		BufferedWriter out = new BufferedWriter(new FileWriter(new File(
				"catsItems.csv")));
		Set<String> keySet = catArts.keySet();
		for (String catId : keySet) {
			StringBuffer line = new StringBuffer();
			Collection<String> collection = catArts.get(catId);
			line.append(catId).append(",");
			for (String itemId : collection) {
				line.append(itemId).append(",");
			}
			line.deleteCharAt(line.length() - 1);
			line.append("\n");
			out.write(line.toString());
		}
		out.flush();
		out.close();

		System.out.println(catArts.values().size() + " items in "
				+ catArts.keySet().size() + " categories from "
				+ sellerIds.length + " sellers.");
	}

	private static MLResultsList fetch(Meli meli, JsonParser parser, Gson gson,
			String sellerId, int offset) throws MeliException, IOException {
		FluentStringsMap params = new FluentStringsMap();
		params.add("seller_id", sellerId);
		params.add("limit", "200");
		params.add("offset", String.valueOf(offset));

		Response response = meli.get("/sites/MLA/search", params);
		String responseBody = response.getResponseBody();

		JsonElement root = parser.parse(responseBody);
		JsonObject rootObject = root.getAsJsonObject();
		MLResultsList mlResultsList = gson.fromJson(rootObject,
				MLResultsList.class);
		return mlResultsList;
	}

	private static void showImages(List<MLItem> results) throws IOException {
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		int i = 0;

		System.out.println(results.size());
		Dimension size = new Dimension(800, 600);
		f.setSize(size);
		f.setPreferredSize(size);
		for (MLItem mlItem : results) {
			if (i > 100)
				break;
			BufferedImage image = ImageIO.read(new URL(mlItem.getThumbnail()));
			JLabel label = new JLabel(new ImageIcon(image));
			f.getContentPane().setLayout(new FlowLayout(FlowLayout.LEFT));
			f.getContentPane().add(label);
			i++;
		}
		f.pack();
		f.setLocation(200, 200);
		f.setVisible(true);
	}
}
