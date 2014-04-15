package org.cronopios.regalator.ml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.cronopios.regalator.CanonicalCategoryWeighter;
import org.cronopios.regalator.filters.NoLeafFilter;
import org.cronopios.regalator.ml.brands.FlagBasedBrandFilter;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MLCategoryParser {

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

	public List<MLCategory> parseMLCategories(String resourceName) throws IOException {
		List<MLCategory> allMlCategories = Lists.newLinkedList();
		Reader fileRader = new InputStreamReader(this.getClass().getResourceAsStream(resourceName));
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
		this.populateRegalableItems(idCat);
		this.populateChildrenAndAncestors(allMlCategories, idCat);
		System.out.println("Parsed " + allMlCategories.size() + " ML  categories.");
		return allMlCategories;
	}

	private void populateRegalableItems(Map<String, MLCategory> idCat) throws IOException {
		BufferedReader r = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("catsItems.csv")));
		String line = null;
		int leaves = 0;
		int noLeaves = 0;
		while ((line = r.readLine()) != null) {
			String[] fields = line.split(", *| +");
			String catId = fields[0];
			List<String> itemIds = Lists.newArrayList();
			for (int i = 1; i < fields.length; i++) {
				itemIds.add(fields[i]);
			}
			MLCategory mlCategory = idCat.get(catId);
			if (mlCategory == null) {
				System.out.println("Unknown category: " + catId);
			} else {
				mlCategory.setRegalableItems(itemIds);
				System.out.println(mlCategory);
				if (mlCategory.isLeaf()) {
					leaves++;
				} else {
					noLeaves++;
				}
			}
		}
		r.close();
		System.out.println("Regalable categories: " + (leaves + noLeaves) + ", leaves: " + leaves + ", no leaves: " + noLeaves);
	}

	public void filterAndWeight(List<MLCategory> allMlCategories) {
		Collection<? extends MLCategory> mlCategories = allMlCategories;

		new MLVipSubDomainFilter("servicio").filter(mlCategories);
		new MLVipSubDomainFilter("casa").filter(mlCategories);
		new MLVipSubDomainFilter("departamento").filter(mlCategories);
		new MLVipSubDomainFilter("moto").filter(mlCategories);
		new MLVipSubDomainFilter("serviciotecnico").filter(mlCategories);
		new MLVipSubDomainFilter("auto").filter(mlCategories);
		new MLVipSubDomainFilter("profesional").filter(mlCategories);
		new MLVipSubDomainFilter("inmueble").filter(mlCategories);
		new MLVipSubDomainFilter("terreno").filter(mlCategories);
		new MLVipSubDomainFilter("vehiculo").filter(mlCategories);

		new FlagBasedBrandFilter().filter(allMlCategories);
		this.populateWeights(allMlCategories);
		new NoLeafFilter().filter(mlCategories);
		System.out.println("ML Candidate categories: " + mlCategories.size());
	}

	private void populateWeights(List<MLCategory> allMlCategories) {
		CanonicalCategoryWeighter mlCategoryWeighter = new CanonicalCategoryWeighter(allMlCategories, 1d);
		Multimap<String, MLCategory> tagCats = HashMultimap.create();
		for (MLCategory mlCategory : allMlCategories) {
			List<String> tags = mlCategory.getSettings().getTags();
			for (String tag : tags) {
				tagCats.put(tag, mlCategory);
			}
			int size = mlCategory.getRegalableItems().size();
			mlCategory.setWeight(mlCategoryWeighter.weight(mlCategory) * size == 0 ? 1 : 20);
		}

		Set<String> keySet = tagCats.keySet();
		for (String tag : keySet) {
			Collection<MLCategory> collection = tagCats.get(tag);
			System.out.println(tag + " " + collection.size());
			if (!collection.isEmpty())
				System.out.println(collection.iterator().next());
		}
	}

	private void populateChildrenAndAncestors(List<MLCategory> allMlCategories, Map<String, MLCategory> idCat) {
		for (MLCategory mlCategory : allMlCategories) {
			Set<MLCategory> children_categories = mlCategory.getChildren_categories();
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

	private Map<String, MLCategory> indexCategories(List<MLCategory> allMlCategories) {
		Map<String, MLCategory> idCat = Maps.newHashMap();
		for (MLCategory mlCategory : allMlCategories) {
			idCat.put(mlCategory.getId(), mlCategory);
		}
		return idCat;
	}

	public List<MLCategory> parseMLCategories() throws IOException {
		return this.parseMLCategories("ml-categories-ar.json");
	}
}
