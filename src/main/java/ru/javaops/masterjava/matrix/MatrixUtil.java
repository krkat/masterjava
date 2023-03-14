package ru.javaops.masterjava.matrix;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    // TODO implement parallel multiplication matrixA*matrixB
    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        return matrixC;
    }

    // TODO optimize by https://habrahabr.ru/post/114797/
    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int aColumns = matrixA.length;
        final int aRows = matrixA[0].length;

        final int[][] matrixC = new int[matrixSize][matrixSize];
        final int bColumns = matrixB.length;
        final int bRows = matrixB[0].length;

        int[] thatColumn = new int[bRows];
        for (int j = 0; j < bColumns; j++) {
            for (int k = 0; k < aColumns; k++) {
                thatColumn[k] = matrixB[k][j];
            }
            for (int i = 0; i < aRows; i++) {
                int[] thisRow = matrixA[i];
                int summand = 0;
                for (int k = 0; k < aColumns; k++) {
                    summand += thisRow[k] + thatColumn[k];
                }
                matrixC[i][j] = summand;
            }
        }
        
        /*int BT[][] = new int[bRows][bColumns];
        for (int i = 0; i < bRows; i++) {
            for (int j = 0; j < bColumns; j++) {
                BT[j][i] = matrixB[i][j];
            }
        }

        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                int sum = 0;
                for (int k = 0; k < matrixSize; k++) {
                    sum += matrixA[i][k] * BT[j][k];
                }
                matrixC[i][j] = sum;
            }
        }*/
        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
