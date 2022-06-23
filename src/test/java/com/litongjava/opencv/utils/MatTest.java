package com.litongjava.opencv.utils;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;

public class MatTest {

  @Before
  public void before() {
    OpenCVLibraryUtils.init();
  }

  @Test
  public void testMatPut() {
    Mat mat = new Mat(500, 500, CvType.CV_8UC3);

    byte[] line = new byte[mat.channels() * mat.width()];

    for (int i = 0; i < line.length; i++) {
      line[i] = 125;
    }
    // 从Mat的250,0向后在row上放置数据
    // row,col
    mat.put(250, 0, line);

    HighGui.imshow("原图", mat);
    HighGui.waitKey();
  }

  @Test
  public void forEach1() {
    Mat mat = new Mat(new Size(3, 3), CvType.CV_8UC3, new Scalar(5, 4, 3, 0));

    System.out.println("rows = " + mat.rows());
    System.out.println("cols = " + mat.cols());
    System.out.println("channels = " + mat.channels());
    System.out.println("total = " + mat.total());
    for (int i = 0; i < mat.rows(); i++) {
      for (int j = 0; j < mat.cols(); j++) {
        byte[] values = new byte[mat.channels()];
        // mat.ptr 方法已经废弃
        // mat).ptr(i, j).get(values);
        // System.out.println(Arrays.toString(values));
      }
      System.out.println();
    }
  }

  
  @Test
  public void forEach2() {
    Mat mat = new Mat(new Size(3, 3), CvType.CV_8UC3, new Scalar(5, 4, 3, 0));

    System.out.println("rows = " + mat.rows());
    System.out.println("cols = " + mat.cols());
    System.out.println("channels = " + mat.channels());
    System.out.println("total = " + mat.total());
    for (int i = 0; i < mat.rows(); i++) {
      for (int j = 0; j < mat.cols(); j++) {
        double[] values = mat.get(i, j);
        System.out.print(Arrays.toString(values) + " ");
      }
      System.out.println();
    }
  }

}
