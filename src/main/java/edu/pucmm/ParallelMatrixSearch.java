package edu.pucmm;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author me@fredpena.dev
 * @created 02/06/2025 - 20:46
 * @student me@randygermosen.com
 * @pucmm_id 1013-4707
 */
public class ParallelMatrixSearch {

    private static final int MATRIX_SIZE = 1000;
    private static final int THREAD_COUNT = 4;
    private static final int[][] matrix = new int[MATRIX_SIZE][MATRIX_SIZE];
    private static final int TARGET = 256; // Número a buscar
    private static final AtomicBoolean found = new AtomicBoolean(false);

    public static void main(String[] args) {
        // Inicializar la matriz con valores aleatorios
        fillMatrixRandom();
        System.out.println("**********Programa Parallel Matrix Search************");
        /* BUSQUEDA SECUENCIAL + TIEMPO DE EJECUCION */
        long startTime = System.nanoTime();
        boolean sequentialResult = sequentialSearch();
        long endTime = System.nanoTime();
        if (sequentialResult)
            System.out.println("Resultado de Busqueda: " + (sequentialResult) + " Tiempo búsqueda secuencial: "
                    + ((endTime - startTime) / 1_000_000) + "ms");
        else
            System.out.println("Busqueda secuencial realizada y no encontrada");

        // Reset de flag para siguiente busqueda
        found.set(false);

        /* BUSQUEDA PARALELA + TIEMPO DE EJECUCION */
        startTime = System.nanoTime();
        boolean parallelResult = parallelSearch();
        endTime = System.nanoTime();
        if (parallelResult)
            System.out.println("Resultado de Busqueda: " + (parallelResult) + " Tiempo búsqueda paralela: "
                    + ((endTime - startTime) / 1_000_000) + "ms");
        else
            System.out.println("Busqueda paralela realizada y no encontrada");
    }

    // Se agrego retorno a booleano para conocer el resultado de la busqueda
    private static boolean sequentialSearch() {
        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                if (matrix[i][j] == TARGET) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean parallelSearch() {
        // Se crean hilos iguales a THREAD_COUND
        Thread[] threads = new Thread[THREAD_COUNT];
        int rowsPerThread = MATRIX_SIZE / THREAD_COUNT;

        for (int i = 0; i < THREAD_COUNT; i++) {
            final int startRow = i * rowsPerThread;
            final int endRow = (i == THREAD_COUNT - 1) ? MATRIX_SIZE : (i + 1) * rowsPerThread;

            threads[i] = new Thread(() -> {
                for (int row = startRow; row < endRow && !found.get(); row++) {
                    for (int col = 0; col < MATRIX_SIZE && !found.get(); col++) {
                        if (matrix[row][col] == TARGET) {
                            found.set(true);
                            break;
                        }
                    }
                }
            });
            threads[i].start();
        }

        // Esperar a que todos los hilos terminen
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return found.get();
    }

    private static void fillMatrixRandom() {
        Random rand = new Random();
        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                matrix[i][j] = rand.nextInt(1000); // Rango arbitrario
            }
        }
    }
}