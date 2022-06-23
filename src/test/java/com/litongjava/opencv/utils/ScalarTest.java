package com.litongjava.opencv.utils;

import org.junit.Before;
import org.junit.Test;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;

/**
 * @author Ping E Lee
 *
 */
public class ScalarTest {

  @Before
  public void before() {
    OpenCVLibraryUtils.init();
  }

  @Test
  public void testColor() {
    Mat mat1 = new Mat(new Size(100, 100), CvType.CV_8UC3, new Scalar(255, 0, 0, 0)); //蓝色
    Mat mat2 = new Mat(new Size(100, 100), CvType.CV_8UC3, new Scalar(0, 255, 0, 0)); //绿色
    Mat mat3 = new Mat(new Size(100, 100), CvType.CV_8UC3, new Scalar(0, 0, 255, 0)); //红色
    Mat mat4 = new Mat(new Size(100, 100), CvType.CV_8UC3, new Scalar(0, 0, 0, 0)); //黑色
    Mat mat5 = new Mat(new Size(100, 100), CvType.CV_8UC3, new Scalar(255, 255, 255, 0)); //白色
    Mat mat6 = new Mat(new Size(100, 100), CvType.CV_8UC3); //黑色
//    HighGui.imshow("mat1", mat1);
//    HighGui.imshow("mat2", mat2);
//    HighGui.imshow("mat3", mat3);
//    HighGui.imshow("mat4", mat4);
//    HighGui.imshow("mat5", mat5);
    HighGui.imshow("mat5", mat6);
    HighGui.waitKey();
  }
}
