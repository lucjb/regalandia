package org.cronopios.regalator.ml;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

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
		Meli meli = new Meli((Integer) 0, "");
		FluentStringsMap params = new FluentStringsMap();
		String q = "ayudantes terapeuticos";
		if (args.length > 0)
			q = args[0];

		params.add("q", q);
		Response response = meli.get("/sites/MLA/search", params);
		String responseBody = response.getResponseBody();

		JsonParser parser = new JsonParser();
		JsonElement root = parser.parse(responseBody);
		JsonObject rootObject = root.getAsJsonObject();
		Gson gson = new Gson();
		MLResultsList mlResultsList = gson.fromJson(rootObject, MLResultsList.class);
		List<MLItem> results = mlResultsList.getResults();
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		int i = 0;
		for (MLItem mlItem : results) {
			if (i > 10)
				break;
			BufferedImage image = ImageIO.read(mlItem.getThumbnail());
			JLabel label = new JLabel(new ImageIcon(image));
			f.getContentPane().setLayout(new FlowLayout());
			f.getContentPane().add(label);
			i++;
		}
		f.pack();
		f.setLocation(200, 200);
		f.setVisible(true);
	}
}
