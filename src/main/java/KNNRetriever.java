import java.util.List;

public interface KNNRetriever<T> {

	public List<T> retrieve(T x, int k);
}
