package org.procmatrix.helloworld.experiments;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Load1GB {
    public static void main(String[] args) throws IOException {
        Path path = Paths.get("./matrix.data");

        System.out.println(System.currentTimeMillis() + " start");

        byte[] bytes = Files.readAllBytes(path);

        System.out.println(System.currentTimeMillis() + " read");

        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();

        int[] matrix1 = new int[intBuffer.remaining()];
        intBuffer.get(matrix1);

        System.out.println(System.currentTimeMillis() + " done");
    }
}
