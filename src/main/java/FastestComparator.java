import java.util.Random;

public final class FastestComparator {


    public int compare(int firstValue, CustomNumberEntity secondValue) {
        Random random = new Random();
        int mSeconds = (random.nextInt(6) + 5) * 1000;
        int secondValueAsNumber = Integer.parseInt(secondValue.getNumber());
        try {
            Thread.sleep(mSeconds);
        } catch (InterruptedException e) {

        }
        return firstValue - secondValueAsNumber;
    }
}
