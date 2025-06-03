package edu.pucmm;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author me@fredpena.dev
 * @created 02/06/2025 - 20:46
 * @student me@randygermosen.com
 * @pucmm_id 1013-4707
 */
public class ProducerConsumer {
    private static final int QUEUE_CAPACITY = 10;
    private static final int PRODUCER_COUNT = 2;
    private static final int CONSUMER_COUNT = 2;
    private static final int PRODUCE_COUNT = 100;
    private static final ConcurrentHashMap<Thread, Integer> consumptionCount = new ConcurrentHashMap<>();

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
        // ExecutorService para lanzar los hilos
        ExecutorService executor = Executors.newFixedThreadPool(PRODUCER_COUNT + CONSUMER_COUNT);

        System.out.println("**********Programa Producer Consumer************");

        long startTime = System.nanoTime();

        /* INICIALIZANDO PRODUCTORES */
        for (int i = 0; i < PRODUCER_COUNT; i++) {
            executor.execute(new Producer(queue));
        }

        /* INICIALIZANDO CONSUMIDORES */
        for (int i = 0; i < CONSUMER_COUNT; i++) {
            executor.execute(new Consumer(queue));
        }

        // Se espera a que terminen los productores
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        long endTime = System.nanoTime();
        System.out.println("Tiempo total de procesamiento: " + ((endTime - startTime) / 1_000_000) + "ms");

        // RESULTADOS DE CONSUMO
        System.out.println("\nElementos consumidos por cada hilo:");
        consumptionCount.forEach((thread, count) -> {
            System.out.println(thread.getName() + ": " + count + " elementos");
        });
    }

    static class Producer implements Runnable {
        private final BlockingQueue<Integer> queue;
        private final Random random = new Random();

        Producer(BlockingQueue<Integer> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < PRODUCE_COUNT / PRODUCER_COUNT; i++) {
                    int number = random.nextInt(1000);
                    queue.put(number);
                    // Se simula un tiempo de produccion
                    Thread.sleep(10);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    static class Consumer implements Runnable {
        private final BlockingQueue<Integer> queue;
        private int sum = 0;
        private int count = 0;

        Consumer(BlockingQueue<Integer> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Integer number = queue.poll(100, TimeUnit.MILLISECONDS);
                    // Si se agota el tiempo de espera break
                    if (number == null) {
                        break;
                    }
                    sum += number;
                    count++;
                }

                // Se registra el consumo
                consumptionCount.put(Thread.currentThread(), count);
                System.out.println(Thread.currentThread().getName() + " suma total: " + sum);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}