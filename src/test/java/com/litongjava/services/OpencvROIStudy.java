package com.litongjava.services;



import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

import com.litongjava.opencv.utils.MatUtils;
import com.litongjava.opencv.utils.OpenCVLibraryUtils;

public class OpencvROIStudy {
  
  @Before
  public void before() {
    OpenCVLibraryUtils.init();
  }

  @Test
  public void testInRange() throws FileNotFoundException, IOException {
    String imagePath="D:\\PlateDetect\\person\\greenback.png";
    Mat image = MatUtils.imread(imagePath);
    Mat hsv=new Mat();
    Imgproc.cvtColor(image, hsv, Imgproc.COLOR_BGR2HSV);
    HighGui.imshow("hsv", hsv);
    //提取mask
    Mat mask=new Mat();
    Core.inRange(hsv, new Scalar(35,43,46), new Scalar(77,255,255), mask);
    HighGui.imshow("mask",mask);
    HighGui.waitKey(0);
  }
  @Test
  public void getRoiArea() throws FileNotFoundException, IOException {
    String imagePath="D:\\PlateDetect\\person\\greenback.png";
    Mat image = MatUtils.imread(imagePath);
    Mat hsv=new Mat();
    Imgproc.cvtColor(image, hsv, Imgproc.COLOR_BGR2HSV);
    HighGui.imshow("hsv", hsv);
    //提取mask
    Mat mask=new Mat();
    Core.inRange(hsv, new Scalar(35,43,46), new Scalar(77,255,255), mask);
    HighGui.imshow("mask",mask);

    Mat redBack = new Mat(image.size(), image.type());
    redBack.setTo(new  Scalar(40, 40, 200));
    HighGui.imshow("redback", redBack);
    
    Core.bitwise_not(mask, mask);
    HighGui.imshow("new_mask", mask);
    //mask不为0的迁移过来
    image.copyTo(redBack, mask);
    HighGui.imshow("roi区域提取", redBack);
    
    HighGui.waitKey(0);
  }
}
