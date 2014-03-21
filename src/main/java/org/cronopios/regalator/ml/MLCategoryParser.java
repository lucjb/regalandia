package org.cronopios.regalator.ml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.cronopios.regalator.CanonicalCategoryWeighter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MLCategoryParser {

	private static void exportCategories(List<MLCategory> allMlCategories)
			throws IOException {
		Writer fileWriter = new FileWriter(new File("mlcattree.txt"));
		for (MLCategory mlCategory : allMlCategories) {
			if (mlCategory.isRoot()) {
				System.out.println(mlCategory + " "
						+ mlCategory.getChildren_categories().size() + " "
						+ mlCategory.getTotal_items_in_this_category());
				print(mlCategory, fileWriter);
			}
		}
		fileWriter.flush();
		fileWriter.close();
	}

	private static void print(MLCategory mlCategory, Writer writer)
			throws IOException {
		String string = mlCategory.toString();
		writer.write(string + "\n");
		Set<MLCategory> children_categories = mlCategory
				.getChildren_categories();
		for (MLCategory child : children_categories) {
			print(child, writer);
		}

	}

	public List<MLCategory> parseMLCategories(String fileName)
			throws FileNotFoundException {

		List<MLCategory> allMlCategories = Lists.newLinkedList();
		Reader fileRader = new InputStreamReader(this.getClass()
				.getResourceAsStream("ml-categories-ar.json"));
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
		Map<String, MLCategory> idCat = this.indexCategories(allMlCategories);

		this.populateChildrenAndAncestors(allMlCategories, idCat);
		this.populateWeights(allMlCategories);
		return allMlCategories;
	}

	private void populateWeights(List<MLCategory> allMlCategories) {
		CanonicalCategoryWeighter mlCategoryWeighter = new CanonicalCategoryWeighter(
				allMlCategories, 3d / 4d);
		for (MLCategory mlCategory : allMlCategories) {
			mlCategory.setWeight(mlCategoryWeighter.weight(mlCategory));
		}
	}

	private void populateChildrenAndAncestors(List<MLCategory> allMlCategories,
			Map<String, MLCategory> idCat) {
		for (MLCategory mlCategory : allMlCategories) {
			Set<MLCategory> children_categories = mlCategory
					.getChildren_categories();
			Set<MLCategory> childrenWithChildren = Sets.newLinkedHashSet();
			for (MLCategory child : children_categories) {
				MLCategory childWithChildren = idCat.get(child.getId());
				childrenWithChildren.add(childWithChildren);
			}
			mlCategory.setChildren_categories(childrenWithChildren);

			List<MLCategory> path_from_root = mlCategory.getPath_from_root();
			List<MLCategory> ancestorsWithAncestors = Lists.newArrayList();
			for (MLCategory ancestor : path_from_root) {
				MLCategory ancestorWithAncestors = idCat.get(ancestor.getId());
				ancestorsWithAncestors.add(ancestorWithAncestors);
			}
			mlCategory.setPath_from_root(ancestorsWithAncestors);
		}
	}

	private Map<String, MLCategory> indexCategories(
			List<MLCategory> allMlCategories) {
		Map<String, MLCategory> idCat = Maps.newHashMap();
		for (MLCategory mlCategory : allMlCategories) {
			idCat.put(mlCategory.getId(), mlCategory);
		}
		return idCat;
	}
}
