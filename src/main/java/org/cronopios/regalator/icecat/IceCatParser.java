package org.cronopios.regalator.icecat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Map;

import org.cronopios.regalator.CanonicalCategoryWeighter;

import au.com.bytecode.opencsv.CSVReader;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class IceCatParser {

	public static void main(String[] args) throws FileNotFoundException {

		Collection<IceCatCategory> parse = new IceCatParser().parse();
		System.out.println(parse.size());
		for (IceCatCategory iceCatCategory : parse) {
			System.out.println(iceCatCategory);
		}
	}

	public Collection<IceCatCategory> parse() throws FileNotFoundException {
		Collection<IceCatCategory> categories = Lists.newArrayList();

		InputStream resourceAsStream = this.getClass().getResourceAsStream(
				"icecat.csv");

		CSVReader csvReader = new CSVReader(new InputStreamReader(
				resourceAsStream));

		String[] line = null;
		Map<String, IceCatCategory> index = Maps.newHashMap();
		try {
			while ((line = csvReader.readNext()) != null) {
				Integer id = Integer.valueOf(line[0]);
				Integer parentCategoryId = Integer.valueOf(line[1]);
				String nameEn = line[2];
				String nameEs = line[3];
				String descriptionEn = line[4];
				String descriptionEs = line[5];
				String lowPic = line[6];
				String thumbnailPic = line[7];
				String uncatid = line[8];
				Integer score = Integer.valueOf(line[9]);
				Boolean searchable = Boolean.valueOf(line[10]);
				Boolean visible = Boolean.valueOf(line[11]);
				Integer level = Integer.valueOf(line[12]);
				IceCatCategory iceCatCategory = new IceCatCategory(id, lowPic,
						score, searchable, thumbnailPic, uncatid, visible,
						descriptionEs, nameEs, parentCategoryId, level);
				categories.add(iceCatCategory);
				index.put(iceCatCategory.getId(), iceCatCategory);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.populateParent(categories, index);
		this.populatePathFromRoot(categories);
		this.populateChildren(categories);
		this.populateWeights(categories);

		return categories;
	}

	private void populateWeights(Collection<IceCatCategory> allMlCategories) {
		CanonicalCategoryWeighter mlCategoryWeighter = new CanonicalCategoryWeighter(
				allMlCategories, 1d / 4d);
		for (IceCatCategory mlCategory : allMlCategories) {
			mlCategory.setWeight(mlCategoryWeighter.weight(mlCategory));
		}
	}

	private void populateChildren(Collection<IceCatCategory> categories) {
		for (IceCatCategory iceCatCategory : categories) {
			if (!iceCatCategory.isRoot()) {
				iceCatCategory.getParent().getChildren().add(iceCatCategory);
			}
		}
	}

	private void populatePathFromRoot(Collection<IceCatCategory> categories) {
		for (IceCatCategory iceCatCategory : categories) {
			IceCatCategory current = iceCatCategory;
			while (!current.isRoot()) {
				iceCatCategory.getPathFromRoot().add(current.getParent());
				current = current.getParent();
			}
			iceCatCategory.setPathFromRoot(Lists.reverse(iceCatCategory
					.getPathFromRoot()));
			iceCatCategory.getPathFromRoot().add(iceCatCategory);
		}
	}

	private void populateParent(Collection<IceCatCategory> categories,
			Map<String, IceCatCategory> index) {
		for (IceCatCategory iceCatCategory : categories) {
			iceCatCategory.setParent(index.get(iceCatCategory
					.getParentCategoryId()));
		}
	}
}
