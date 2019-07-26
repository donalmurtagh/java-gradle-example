import java.util.List;

public interface NumberFinder {
	
	boolean contains(int valueToFind, List<CustomNumberEntity> list);
	
	List<CustomNumberEntity> readFromFile(String filePath);
}

