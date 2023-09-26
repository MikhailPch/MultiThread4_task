
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    static BlockingQueue<String> queueA = new ArrayBlockingQueue<>(100);
    static BlockingQueue<String> queueB = new ArrayBlockingQueue<>(100);
    static BlockingQueue<String> queueC = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) throws InterruptedException {
        int numberOfTexts = 10_000;
        int textLength = 100_000;
        String textChar = "abc";

        new Thread(() -> {
            for (int i = 0; i < numberOfTexts; i++) {
                try {
                    queueA.put(generateText(textChar, textLength));
                    queueB.put(generateText(textChar, textLength));
                    queueC.put(generateText(textChar, textLength));
                } catch (InterruptedException e) {
                    return;
                }
            }
        }).start();

        new Thread(() -> {
            counter('a', numberOfTexts, queueA);
        }).start();
        new Thread(() -> {
            counter('b', numberOfTexts, queueB);
        }).start();
        new Thread(() -> {
            counter('c', numberOfTexts, queueC);
        }).start();
    }


    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static void counter(char ch, int length, BlockingQueue<String> queue) {
        long maxCount = 0;
        for (int i = 0; i < length; i++) {
            try {
                long count = queue.take().chars().filter(c -> c == ch).count();
                if (count > maxCount) {
                    maxCount = count;
                }
            } catch (InterruptedException e) {
                return;
            }
        }
        System.out.printf("Максимальное число повторений символа '%s' в одном тексте - %d \n", ch, maxCount);
    }
}