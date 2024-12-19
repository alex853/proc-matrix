package org.procmatrix.helloworld.experiments;

public class Calc1GB {
    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis() + " init");
        int[] matrix1 = new int[15000*15000];
        int[] matrix2 = new int[15000*15000];
        int[] matrixS = new int[15000*15000];

        System.out.println(System.currentTimeMillis() + " calc");

        for (int i = 0; i < 15000*15000; i++) {
            matrixS[i] = matrix1[i] + matrix2[i];
        }

        System.out.println(System.currentTimeMillis() + " done");
    }
}
