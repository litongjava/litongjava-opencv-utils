package com.litongjava.opencv.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * 简化版 IOUtils，提供 toByteArray(InputStream) 等方法。
 * 使用 try-with-resources 的调用方负责是否要关闭输入流（MatUtils 中已在外层使用 try-with-resources）。
 */
public final class IOUtils {

  private static final int DEFAULT_BUFFER_SIZE = 8 * 1024;

  private IOUtils() {
    // no-op
  }

  /**
   * 将 InputStream 全部读为 byte[]。
   * 此方法不会在内部关闭传入的流（调用方通常在外部使用 try-with-resources 管理流）。
   *
   * @param input 输入流（可为 FileInputStream 等）
   * @return 完整的字节数组
   * @throws IOException 读写异常
   */
  public static byte[] toByteArray(InputStream input) throws IOException {
    Objects.requireNonNull(input, "input must not be null");
    return toByteArray(input, DEFAULT_BUFFER_SIZE);
  }

  /**
   * 将 InputStream 读为 byte[]，指定缓冲区大小。
   *
   * @param input 输入流
   * @param bufferSize 缓冲区大小（>0）
   * @return 字节数组
   * @throws IOException IO 异常
   */
  public static byte[] toByteArray(InputStream input, int bufferSize) throws IOException {
    Objects.requireNonNull(input, "input must not be null");
    if (bufferSize <= 0) {
      bufferSize = DEFAULT_BUFFER_SIZE;
    }

    try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
      byte[] buffer = new byte[bufferSize];
      int n;
      while ((n = input.read(buffer, 0, buffer.length)) != -1) {
        output.write(buffer, 0, n);
      }
      return output.toByteArray();
    }
  }

  /**
   * 将给定的 byte[] 写入到目标 OutputStream 并刷新（不关闭）。
   *
   * @param data 数据
   * @param out 输出流
   * @throws IOException IO 异常
   */
  public static void write(byte[] data, java.io.OutputStream out) throws IOException {
    if (data == null) {
      return;
    }
    Objects.requireNonNull(out, "out must not be null");
    out.write(data);
    out.flush();
  }
}