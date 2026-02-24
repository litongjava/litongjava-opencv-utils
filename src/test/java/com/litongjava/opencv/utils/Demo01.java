package com.litongjava.opencv.utils;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;

public class Demo01 {
  public static void main(String[] args) throws FileNotFoundException, IOException {
    // 加载 native
    OpenCVLibraryUtils.init();

    Mat src = MatUtils.imread("F:\\opencv-images\\person\\flower.png");
    if (src == null || src.empty()) {
      System.out.println("empty");
      return;
    }

    HighGui.imshow("input", src);
    HighGui.waitKey(0);
    HighGui.destroyAllWindows();
  }
}