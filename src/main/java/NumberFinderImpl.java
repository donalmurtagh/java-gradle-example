import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class NumberFinderImpl implements NumberFinder {

    private final FastestComparator fastestComparator = new FastestComparator();

    @Override
    public boolean contains(int valueToFind, List<CustomNumberEntity> list) {

        // keep track of CustomNumberEntity that have already been compared unsuccessfully, so we can reduce the number
        // of times we need to invoke FastestComparator
        Collection<CustomNumberEntity> unmatchedNumbers = new HashSet<>();

        // try to process the stream elements in parallel to mitigate the thread-sleeping in FastestComparator
        return list.parallelStream()
                .anyMatch(customNumberEntity -> {
                    String numberValue = customNumberEntity.getNumber();

                    try {
                        // null will never match because you can't pass a null value for valueToFind
                        boolean isMatch = numberValue != null &&
                                !unmatchedNumbers.contains(customNumberEntity) &&
                                fastestComparator.compare(valueToFind, customNumberEntity) == 0;

                        if (!isMatch) {
                            unmatchedNumbers.add(customNumberEntity);
                        }
                        return isMatch;

                    } catch (NumberFormatException ex) {
                        // FastestComparator will throw this exception if the CustomNumberEntity can't be converted to
                        // a number, but this means it can't match valueToFind, so we can return false
                        unmatchedNumbers.add(customNumberEntity);
                        return false;
                    }
                });
    }

    @Override
    public List<CustomNumberEntity> readFromFile(String filePath) {

        try (FileReader reader = new FileReader(filePath)) {
            Collection<?> numbersArray = (Collection) new JSONParser().parse(reader);

            return numbersArray.stream()
                    .map(numberObject -> {
                        JSONObject jsonObject = (JSONObject) numberObject;
                        Object numberPropertyValue = jsonObject.get("number");
                        String number = numberPropertyValue != null ? numberPropertyValue.toString() : null;
                        return new CustomNumberEntity(number);
                    })
                    .collect(toList());

        } catch (IOException | ParseException e) {
            // The NumberFinder interface doesn't declare any checked exceptions, and I'm assuming I shouldn't
            // modify it, so my only choices are to rethrow them as unchecked exceptions or ignore them
            throw new RuntimeException("Failed to parse JSON file at location: " + filePath, e);
        }
    }
}
