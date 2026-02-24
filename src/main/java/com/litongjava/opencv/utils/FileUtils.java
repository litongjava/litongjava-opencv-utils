package com.litongjava.opencv.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

/**
 * 简化版 FileUtils，基于 java.nio.file 实现。
 * 只包含 MatUtils 所需的 writeByteArrayToFile 及若干常用辅助方法。
 */
public final class FileUtils {

  private FileUtils() {
    // no-op
  }

  /**
   * 将字节数组写入文件。如果父目录不存在则自动创建。
   *
   * @param file 目标文件
   * @param data 字节数组
   * @throws IOException IO 错误
   */
  public static void writeByteArrayToFile(File file, byte[] data) throws IOException {
    Objects.requireNonNull(file, "file must not be null");
    Objects.requireNonNull(data, "data must not be null");

    Path path = file.toPath();
    Path parent = path.getParent();
    if (parent != null && !Files.exists(parent)) {
      Files.createDirectories(parent);
    }
    // 使用 WRITE, CREATE, TRUNCATE_EXISTING 确保覆盖原文件
    Files.write(path, data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
  }

  /**
   * 将字节数组写入文件（方便返回 Path）。
   *
   * @param pathStr 目标路径字符串
   * @param data 字节数组
   * @return 写入文件的 Path
   * @throws IOException IO 错误
   */
  public static Path writeByteArrayToFile(String pathStr, byte[] data) throws IOException {
    Objects.requireNonNull(pathStr, "pathStr must not be null");
    File file = new File(pathStr);
    writeByteArrayToFile(file, data);
    return file.toPath();
  }

  /**
   * 复制文件（简单封装）。
   *
   * @param src 源文件
   * @param dest 目标文件
   * @throws IOException IO 错误
   */
  public static void copyFile(File src, File dest) throws IOException {
    Objects.requireNonNull(src, "src must not be null");
    Objects.requireNonNull(dest, "dest must not be null");
    Path destParent = dest.toPath().getParent();
    if (destParent != null && !Files.exists(destParent)) {
      Files.createDirectories(destParent);
    }
    Files.copy(src.toPath(), dest.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
  }

  /**
   * 删除文件或空目录。
   *
   * @param file 要删除的文件
   * @return 如果删除成功则为 true
   * @throws IOException IO 错误
   */
  public static boolean deleteQuietly(File file) throws IOException {
    if (file == null) {
      return false;
    }
    return Files.deleteIfExists(file.toPath());
  }
}