import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.*;

public class FactorialCalculator {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // ConcurrentHashMap для збереження результатів
        ConcurrentHashMap<Integer, BigInteger> factorialMap = new ConcurrentHashMap<>();

        // Використання ExecutorService для створення пулу потоків
        ExecutorService executor = Executors.newFixedThreadPool(4);

        // Список чисел, для яких обчислюємо факторіали
        List<Integer> numbers = List.of(5, 7, 10, 12, 15, 20);

        // Створення списку Callable для паралельного обчислення
        List<Callable<Void>> tasks = numbers.stream()
                .map(number -> (Callable<Void>) () -> {
                    if (Thread.currentThread().isInterrupted()) {
                        System.out.println("Задача для числа " + number + " була скасована.");
                        return null;
                    }
                    System.out.println("Обчислення факторіала для " + number);
                    BigInteger factorial = calculateFactorial(number);
                    factorialMap.put(number, factorial);
                    return null;
                })
                .toList();

        // Виконання задач та отримання Future
        List<Future<Void>> futures = executor.invokeAll(tasks);

        // Імітація скасування однієї задачі
        if (!futures.isEmpty()) {
            futures.get(0).cancel(true); // Скасовуємо першу задачу
        }

        // Очікування завершення всіх задач
        for (Future<Void> future : futures) {
            if (!future.isCancelled()) {
                future.get(); // Забираємо результат, якщо задача не була скасована
            } else {
                System.out.println("Одна із задач була скасована.");
            }
        }

        // Завершення роботи ExecutorService
        executor.shutdown();

        // Виведення результатів
        System.out.println("\nРезультати обчислення факторіалів:");
        factorialMap.forEach((key, value) -> System.out.println(key + "! = " + value));
    }

    // Метод для обчислення факторіала
    private static BigInteger calculateFactorial(int n) {
        BigInteger result = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result;
    }
}
