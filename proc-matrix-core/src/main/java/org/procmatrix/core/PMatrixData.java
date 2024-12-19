package org.procmatrix.core;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class PMatrixData {
    private final int width;
    private final int height;
    private final DataAccess dataAccess;

    private static final Random random = new Random();

    private PMatrixData(final int width, final int height, final int[] data) {
        this.width = width;
        this.height = height;
        this.dataAccess = new IntArrayAccess(data);
    }

    private PMatrixData(final int width, final int height, final IntBuffer data) {
        this.width = width;
        this.height = height;
        this.dataAccess = new IntBufferAccess(data);
    }

    public static PMatrixData of(final int width, final int height, final int[] data) {
        checkArgument(width >= 1, "width should be 1 or more");
        checkArgument(height >= 1, "height should be 1 or more");
        checkNotNull(data, "data array should not be null");
        checkArgument(data.length == width * height, "data length should match width * height");

        return new PMatrixData(width, height, data);
    }

    public static PMatrixData random(final int width, final int height) {
        checkArgument(width >= 1, "width should be 1 or more");
        checkArgument(height >= 1, "height should be 1 or more");

        final int[] data = new int[width*height];
        for (int i = 0; i < data.length; i++) {
            data[i] = random.nextInt();
        }
        return new PMatrixData(width, height, data);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public DataAccess getData() {
        return dataAccess;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PMatrixData that = (PMatrixData) o;
        return width == that.width && height == that.height && dataAccess.areValuesEqual(that.dataAccess);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(width, height);
        result = 31 * result + dataAccess.valuesHash();
        return result;
    }

    public byte[] asBytes() {
        return dataAccess.asBytes();
    }

    public static PMatrixData fromBytes(final byte[] bytes) {
        checkNotNull(bytes, "bytes should not be null");

        final ByteBuffer byteBuffer = ByteBuffer.wrap(bytes, 0, bytes.length);
        final IntBuffer intBuffer = byteBuffer.asIntBuffer();

        final int width = intBuffer.get(0);
        final int height = intBuffer.get(1);
        final int expectedLength = width * height;

        checkArgument(expectedLength == intBuffer.remaining() - 2, "data in file looks corrupted");

        return new PMatrixData(width, height, intBuffer);
    }

    public String toPrintedString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Matrix ").append(width).append(" x ").append(height).append("\n");
        for (int row = 0; row < height; row++) {
            sb.append(" | ");
            for (int col = 0; col < width; col++) {
                sb.append(center(getData().get(row, col), 8));
            }
            sb.append(" | \n");
        }
        return sb.toString();
    }

    private static String center(final int v, final int len) {
        final String s = String.valueOf(v);
        final int remaining = len - s.length();
        return (" ".repeat(remaining / 2)) + s + (" ".repeat(remaining - (remaining / 2)));
    }

    public interface DataAccess {

        int length();

        int getByIndex(int index);

        int get(int row, int col);

        byte[] asBytes();

        int valuesHash();

        default boolean areValuesEqual(DataAccess dataAccess) {
            for (int i = 0; i < length(); i++) {
                if (getByIndex(i) != dataAccess.getByIndex(i)) {
                    return false;
                }
            }
            return true;
        }
    }

    private class IntArrayAccess implements DataAccess {
        private final int[] data;

        IntArrayAccess(final int[] data) {
            this.data = data;
        }

        @Override
        public int length() {
            return data.length;
        }

        @Override
        public int get(int row, int col) {
            return data[row * getWidth() + col];
        }

        @Override
        public int getByIndex(int index) {
            return data[index];
        }

        @Override
        public byte[] asBytes() {
            final ByteBuffer byteBuffer = ByteBuffer.allocate(2 * 4 + data.length * 4);
            final IntBuffer intBuffer = byteBuffer.asIntBuffer();
            intBuffer.put(getWidth());
            intBuffer.put(getHeight());
            intBuffer.put(data);

            return byteBuffer.array();
        }

        @Override
        public int valuesHash() {
            return Arrays.hashCode(data);
        }
    }

    private class IntBufferAccess implements DataAccess {
        private final IntBuffer data;

        IntBufferAccess(final IntBuffer data) {
            this.data = data;
        }

        @Override
        public int length() {
            return data.remaining() - 2;
        }

        @Override
        public int get(int row, int col) {
            return data.get(row * getWidth() + col + 2);
        }

        @Override
        public int getByIndex(final int index) {
            return data.get(index + 2);
        }

        @Override
        public byte[] asBytes() {
            final ByteBuffer byteBuffer = ByteBuffer.allocate(2 * 4 + length() * 4);
            final IntBuffer intBuffer = byteBuffer.asIntBuffer();
            intBuffer.put(data);
            data.position(0);

            return byteBuffer.array();
        }

        @Override
        public int valuesHash() {
            int hash = 0;
            for (int i = 0; i < length(); i++) {
                hash += getByIndex(i);
            }
            return hash;
        }
    }
}
