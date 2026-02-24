package com.litongjava.opencv.utils;

import nu.pattern.OpenCV;

public class OpenCVLibraryUtils {
  public static void init() {
    // openpnp 会把 native 解压并加载到 JVM
    try {
      OpenCV.loadLocally(); // 或 OpenCV.loadShared();
    } catch (UnsatisfiedLinkError e) {
      throw new RuntimeException("Failed to load OpenCV native library", e);
    }
  }
}