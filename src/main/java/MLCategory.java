import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class MLCategory {

	private String id;
	private String name;
	private List<MLCategory> path_from_root;

	@Override
	public String toString() {
		StringBuilder ts = new StringBuilder();
		for (MLCategory ancesotor : this.path_from_root) {
			ts.append(ancesotor.name).append(">");
		}
		return ts.toString();

	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MLCategory))
			return false;

		MLCategory other = (MLCategory) obj;
		return this.id.equals(other.id);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<MLCategory> getPath_from_root() {
		return path_from_root;
	}

	public void setPath_from_root(List<MLCategory> path_from_root) {
		this.path_from_root = path_from_root;
	}

}
