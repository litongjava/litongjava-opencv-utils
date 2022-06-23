package com.litongjava.opencv.utils;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * @author Ping E Lee
 *
 */
public class HoughCirclesTest {

  @Before
  public void before() {
    OpenCVLibraryUtils.init();
  }

  /**
   * 检测图片中的圆形
   * @throws IOException 
   * @throws FileNotFoundException 
   */
  @Test
  public void test() throws FileNotFoundException, IOException {
    String srcPath = "D:\\opencv-images\\traffic\\temp\\green-2-black.jpg";
    Mat src = MatUtils.imread(srcPath);

    // 转为灰度图片
    Mat gray = new Mat();
    Imgproc.cvtColor(src, gray, Imgproc.COLOR_RGB2GRAY);
    // 显示图片
    //HighGui.imshow("gray", gray);
    //HighGui.waitKey();
    MatUtils.debugToFile(gray,srcPath,"green-2-black.jpg","02-gray.jpg");
    
    //圆形检测
    Mat circles = new Mat();
    Imgproc.HoughCircles(gray,circles,Imgproc.HOUGH_GRADIENT,1,150,100,30,50,90);
    MatUtils.debugToFile(circles,srcPath,"green-2-black.jpg","03-circles.jpg");
    
    System.out.println(circles.size());
  }
}
