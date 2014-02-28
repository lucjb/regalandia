package org.cronopios.regalator.ml;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/** To visualize .dot file use graphviz or gephi **/
public class CategoryTreeDotExporterMain {

	private static Map<String, MLCategory> categoriesById;
	
	public static void main(String[] args) throws IOException {
		MLCategoryParser mlCategoryParser = new MLCategoryParser();
		List<MLCategory> allMlCategories = mlCategoryParser.parseMLCategories("all");

		categoriesById = Maps.newLinkedHashMap();
		Set<MLCategory> roots = Sets.newLinkedHashSet();
		for (MLCategory mlCategory : allMlCategories) {
			categoriesById.put(mlCategory.getId(), mlCategory);
			if (mlCategory.getPath_from_root().size() == 1)
				roots.add(mlCategory);
		}
		
		BufferedWriter writer = new BufferedWriter(new FileWriter("foo.dot"));
		writer.write("digraph foo {");
		writer.newLine();
		
		String globalRoot = "root";
		int rootLevel = 1;
		for (MLCategory root : roots) {
			writer.write(globalRoot + " -> " + getNodeId(rootLevel, root) + ";");
			writer.newLine();
			outputDFSCategory(root, rootLevel, writer);
		}
			
		writer.write("};");
		writer.close();
	}
	
	private static void outputDFSCategory(MLCategory node, int level, BufferedWriter writer) throws IOException {
		if (node.getChildren_categories() == null)
			return;
		
		String nodeId = getNodeId(level, node);
		for (MLCategory dummyChild : node.getChildren_categories()) {
			MLCategory child = categoriesById.get(dummyChild.getId());
			if (child == null)
				throw new RuntimeException("Fuck...");
			int childLevel = level + 1;
			String childId = getNodeId(childLevel, child);
			writer.write(nodeId + " -> " + childId + ";");
			writer.newLine();
			outputDFSCategory(child, childLevel, writer);
		}
	}
	
	private static String getNodeId(int level, MLCategory category) {
		return String.format("\"%d-%s\"", level, category.getId());
	}
}
