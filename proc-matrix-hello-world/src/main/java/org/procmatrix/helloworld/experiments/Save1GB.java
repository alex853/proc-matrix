package org.procmatrix.helloworld.experiments;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.*;

public class Save1GB {
    public static void main(String[] args) throws IOException {
        int[] matrix1 = new int[15000*15000];
        for (int i = 0; i < matrix1.length; i++) {
            matrix1[i] = i;
        }

        System.out.println(System.currentTimeMillis() + " prep");

        ByteBuffer byteBuffer = ByteBuffer.allocate(matrix1.length * 4);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(matrix1);
        ByteArrayInputStream is = new ByteArrayInputStream(byteBuffer.array());

        System.out.println(System.currentTimeMillis() + " save");

        Path path = Paths.get("./matrix.data");
        Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);

        System.out.println(System.currentTimeMillis() + " done");
    }
}
