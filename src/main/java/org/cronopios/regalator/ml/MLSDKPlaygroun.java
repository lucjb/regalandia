package org.cronopios.regalator.ml;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.commons.lang3.RandomStringUtils;

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
		// Meli meli = new Meli((Integer) 0, "");
		// FluentStringsMap params = new FluentStringsMap();
		// String q = "ayudantes terapeuticos";
		// if (args.length > 0)
		// q = args[0];
		//
		// params.add("q", q);
		// Response response = meli.get("/sites/MLA/search", params);
		// String responseBody = response.getResponseBody();
		//
		// JsonParser parser = new JsonParser();
		// JsonElement root = parser.parse(responseBody);
		// JsonObject rootObject = root.getAsJsonObject();
		// Gson gson = new Gson();
		// MLResultsList mlResultsList = gson.fromJson(rootObject,
		// MLResultsList.class);
		// List<MLItem> results = mlResultsList.getResults();

		List<MLCategory> cats = new MLCategoryParser().parseMLCategories();
		Random r = new Random(System.currentTimeMillis());
		MLCategory mlCategory = cats.get(r.nextInt(cats.size()));

		System.out.println(mlCategory);
		MLResultsList mlResultsList = new MLSearchingService().searchCategory(mlCategory);
		List<MLItem> results = mlResultsList.getResults();
		
		showImages(results);
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
			BufferedImage image = ImageIO.read(mlItem.getThumbnail());
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
