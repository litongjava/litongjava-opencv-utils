package com.litongjava.opencv.utils;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TermCriteriaTest {

  @Before
  public void before() {
    OpenCVLibraryUtils.init();
  }

  /**
   * 测试使用k-means聚类获取图片背景
   * 
   * @throws IOException
   * @throws FileNotFoundException
   */
  @Test
  public void testTermCriteria() throws FileNotFoundException, IOException {
    String imgePath = "D:\\opencv-images\\shape\\tempp\\1639306697133-maxArea-maxArea.jpg";
    Mat src = MatUtils.imread(imgePath);
    if (src.empty()) {
      System.out.println("could not load image...\n");
      return;
    }
    Mat result = KmeansUtils.backgrand2White(src);

    HighGui.imshow("origin", src);
    // Mat data type is not compatible: 5
    // HighGui.imshow("data", data);
    HighGui.imshow("final", result);
    HighGui.waitKey();
    HighGui.destroyAllWindows();
  }
}
