package com.litongjava.utils;

import org.junit.Before;
import org.junit.Test;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

import com.litongjava.opencv.utils.OpenCVLibraryUtils;

public class CVTest {

  @Before
  public void load() {
    OpenCVLibraryUtils.init();
  }

  @Test
  public void imRead() {
    String testImg = "D:\\PlateDetect\\比赛测试\\车牌\\新能源车牌\\1.jpg";
    Mat inMat = Imgcodecs.imread(testImg);
    if (inMat.empty()) {
      System.out.println("empyty");
      return;
    }
    HighGui.imshow("车牌", inMat);
    HighGui.waitKey(0);// 按任意键关闭窗口
  }
}
