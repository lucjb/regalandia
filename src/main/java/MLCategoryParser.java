import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MLCategoryParser {

	public List<MLCategory> parseMLCategories(String fileName)
			throws FileNotFoundException {
		List<MLCategory> allMlCategories = Lists.newLinkedList();
		FileReader fileRader = new FileReader(new File(fileName));
		JsonParser parser = new JsonParser();
		JsonElement root = parser.parse(fileRader);
		JsonObject rootObject = root.getAsJsonObject();
		Set<Entry<String, JsonElement>> entrySet = rootObject.entrySet();
		Gson gson = new Gson();
		for (Entry<String, JsonElement> entry : entrySet) {
			String catId = entry.getKey();
			JsonObject asJsonObject = entry.getValue().getAsJsonObject();
			MLCategory mlCat = gson.fromJson(asJsonObject, MLCategory.class);
			allMlCategories.add(mlCat);
		}
		return allMlCategories;
	}
}
