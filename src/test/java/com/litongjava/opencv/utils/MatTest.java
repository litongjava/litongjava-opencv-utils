package com.litongjava.opencv.utils;

import org.junit.Before;
import org.junit.Test;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
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
    //从Mat的250,0向后在row上放置数据 
    //row,col
    mat.put(250, 0, line);

    HighGui.imshow("原图", mat);
    HighGui.waitKey();
  }
}
