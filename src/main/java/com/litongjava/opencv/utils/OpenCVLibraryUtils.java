package com.litongjava.opencv.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import org.opencv.core.Core;

public class OpenCVLibraryUtils {
  public static void init() {
    initLibary();
  }

  private static void initLibary() {
    File file = new File("lib");
    String absolutePath = file.getAbsolutePath();
    if (!file.exists()) {
      file.mkdirs();
      System.out.println("mkdirs:" + absolutePath);
    }
    try {
      addLibDir(absolutePath);
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
  }

  public static void addLibDir(String s) throws IOException {
    try {
      Field field = java.lang.ClassLoader.class.getDeclaredField("usr_paths");
      field.setAccessible(true);
      String[] paths = (String[]) field.get(null);
      for (int i = 0; i < paths.length; i++) {
        if (s.equals(paths[i])) {
          return;
        }
      }
      String[] tmp = new String[paths.length + 1];
      System.arraycopy(paths, 0, tmp, 0, paths.length);
      tmp[paths.length] = s;
      field.set(null, tmp);
    } catch (IllegalAccessException e) {
      throw new IOException("Failed to get permissions to set library path");
    } catch (NoSuchFieldException e) {
      throw new IOException("Failed to get field handle to set library path");
    }
  }
}