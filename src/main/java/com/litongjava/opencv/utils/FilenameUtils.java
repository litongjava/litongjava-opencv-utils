package com.litongjava.opencv.utils;

import java.util.Optional;

/**
 * 简化版 FilenameUtils
 */
public final class FilenameUtils {

  private FilenameUtils() {
  }

  /**
   * 获取扩展名（不包含 .）
   */
  public static String getExtension(String filename) {
    if (filename == null) {
      return "";
    }

    String name = getName(filename);
    int lastDot = name.lastIndexOf('.');
    if (lastDot == -1 || lastDot == 0) {
      return "";
    }
    return Optional.of(name.substring(lastDot + 1)).orElse("");
  }

  /**
   * 获取文件名（包含扩展名） "/a/b/c.txt" -> "c.txt"
   */
  public static String getName(String filename) {
    if (filename == null) {
      return null;
    }
    int lastSeparator = Math.max(filename.lastIndexOf('/'), filename.lastIndexOf('\\'));
    if (lastSeparator == -1) {
      return filename;
    }
    return filename.substring(lastSeparator + 1);
  }

  /**
   * 移除扩展名 "a.tar.gz" -> "a.tar"
   */
  public static String removeExtension(String filename) {
    if (filename == null) {
      return null;
    }
    int lastSeparator = Math.max(filename.lastIndexOf('/'), filename.lastIndexOf('\\'));
    int lastDot = filename.lastIndexOf('.');
    if (lastDot == -1 || (lastSeparator != -1 && lastDot < lastSeparator + 1)) {
      return filename;
    }
    return filename.substring(0, lastDot);
  }

  /**
   * 获取基础名（文件名去掉扩展名）
   *
   * "/a/b/c.jpg" -> "c" "c.jpg" -> "c" "a.tar.gz" -> "a.tar" ".bashrc" ->
   * ".bashrc"
   */
  public static String getBaseName(String filename) {
    if (filename == null) {
      return null;
    }

    String name = getName(filename);
    if (name == null || name.isEmpty()) {
      return "";
    }

    int lastDot = name.lastIndexOf('.');
    if (lastDot == -1 || lastDot == 0) {
      return name;
    }

    return name.substring(0, lastDot);
  }
}