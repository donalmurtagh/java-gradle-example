import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class NumberFinderTest {

    private NumberFinder numberFinder;
    private List<CustomNumberEntity> numbers;

    @Before
    public void setUp() {
        numberFinder = new NumberFinderImpl();
        numbers = numberFinder.readFromFile("src/test/resources/sample.json");
    }

    @Test
    public void readJsonFile() {
        int expectedSize = 9;
        assertEquals(expectedSize, numbers.size());

        // check the first and last numbers and one in the middle
        assertEquals("67", numbers.get(0).getNumber());
        assertNull(numbers.get(2).getNumber());
        assertEquals("3",numbers.get(expectedSize - 1).getNumber());
    }

    @Test(expected = RuntimeException.class)
    public void readAnInvalidJsonFile() {
        numberFinder.readFromFile("no-such-file.json");
    }

    @Test
    public void testMatchedNumbersAreFound() {
        boolean allNumbersFound = Stream.of(67, 45, -3, 12, 100, 3)
                .allMatch(integer -> numberFinder.contains(integer, numbers));
        assertTrue(allNumbersFound);
    }

    @Test
    public void tesUnmatchedNumberIsNotFound() {
        assertFalse(numberFinder.contains(99, numbers));
    }
}
