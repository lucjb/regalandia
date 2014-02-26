package org.cronopios.regalator.ml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.cronopios.regalator.ml.brands.VocabularyParser;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MLCategoryParser {

	public static void main(String[] args) throws IOException {
		VocabularyParser vocabularyParser = new VocabularyParser();
		Set<String> vocabulary = vocabularyParser.parseVocabulary("vocabulary.txt");

		MLCategoryParser mlCategoryParser = new MLCategoryParser();

		List<MLCategory> allMlCategories = mlCategoryParser.parseMLCategories("all");

		// exportCategories(allMlCategories);

		int discarded = 0;
		for (MLCategory mlCategory : allMlCategories) {
			if (mlCategory.isLeaf()) {
				boolean isBrand = isBrand(vocabulary, mlCategory);
				if (isBrand) {
					discarded++;
					System.out.println(mlCategory);
				}
			}
		}
		System.out.println(discarded);

	}

	private static boolean isBrand(Set<String> vocabulary, MLCategory mlCategory) {
		String name = mlCategory.getName();
		String lowerCase = name.toLowerCase();
		String[] split = lowerCase.split(" ");
		boolean isBrand = false;
		for (String string : split) {
			boolean inVocabulary = vocabulary.contains(string);
			if (!inVocabulary) {
				isBrand = true;
			}
		}
		return isBrand;
	}

	private static void exportCategories(List<MLCategory> allMlCategories) throws IOException {
		Writer fileWriter = new FileWriter(new File("mlcattree.txt"));
		for (MLCategory mlCategory : allMlCategories) {
			if (mlCategory.isRoot()) {
				System.out.println(mlCategory + " " + mlCategory.getChildren_categories().size() + " " + mlCategory.getTotal_items_in_this_category());
				print(mlCategory, fileWriter);
			}
		}
		fileWriter.flush();
		fileWriter.close();
	}

	private static void print(MLCategory mlCategory, Writer writer) throws IOException {
		String string = mlCategory.toString();
		writer.write(string + "\n");
		Set<MLCategory> children_categories = mlCategory.getChildren_categories();
		for (MLCategory child : children_categories) {
			print(child, writer);
		}

	}

	public List<MLCategory> parseMLCategories(String fileName) throws FileNotFoundException {
		List<MLCategory> allMlCategories = Lists.newLinkedList();
		FileReader fileRader = new FileReader(new File(fileName));
		JsonParser parser = new JsonParser();
		JsonElement root = parser.parse(fileRader);
		JsonObject rootObject = root.getAsJsonObject();
		Set<Entry<String, JsonElement>> entrySet = rootObject.entrySet();
		Gson gson = new Gson();
		for (Entry<String, JsonElement> entry : entrySet) {
			JsonObject asJsonObject = entry.getValue().getAsJsonObject();
			MLCategory mlCat = gson.fromJson(asJsonObject, MLCategory.class);
			allMlCategories.add(mlCat);
		}
		Map<String, MLCategory> idCat = Maps.newHashMap();
		for (MLCategory mlCategory : allMlCategories) {
			idCat.put(mlCategory.getId(), mlCategory);
		}

		for (MLCategory mlCategory : allMlCategories) {
			Set<MLCategory> children_categories = mlCategory.getChildren_categories();
			Set<MLCategory> childrenWithChildren = Sets.newLinkedHashSet();
			for (MLCategory child : children_categories) {
				MLCategory childWithChildren = idCat.get(child.getId());
				childrenWithChildren.add(childWithChildren);
			}
			mlCategory.setChildren_categories(childrenWithChildren);
		}
		return allMlCategories;
	}
}
