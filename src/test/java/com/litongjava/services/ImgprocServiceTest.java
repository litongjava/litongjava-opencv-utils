package com.litongjava.services;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;

import com.litongjava.opencv.model.DebugInfo;
import com.litongjava.opencv.utils.ImgprocUtils;
import com.litongjava.opencv.utils.MatUtils;
import com.litongjava.opencv.utils.OpenCVLibraryUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImgprocServiceTest {

  
  String imagePath = "D:\\opencv-images\\shape\\Shape.png";
  
  

  @Before
  public void before() {
    OpenCVLibraryUtils.init();
  }

  @Test
  public void testFindContoursAndDraw() throws FileNotFoundException, IOException {
    String tempPath = "temp/testFindContoursAndDraw";
    DebugInfo debugInfo = new DebugInfo(imagePath, true,tempPath);
    Mat src = MatUtils.imread(imagePath);
    File file = new File(tempPath);
    if (!file.exists()) {
      file.mkdirs();
    }
    log.info("tempPath:{}", file.getAbsolutePath());
    Mat dst=ImgprocUtils.findContoursAndDraw(src, debugInfo);
    
    HighGui.imshow("dst", dst);
    HighGui.waitKey();
    HighGui.destroyAllWindows();
  }
  
  @Test
  public void testExtraMaxArea() throws FileNotFoundException, IOException {
    String tempPath = "temp/testExtraMaxArea";
    DebugInfo debugInfo = new DebugInfo(imagePath, true,tempPath);
    Mat src = MatUtils.imread(imagePath);
    File file = new File(tempPath);
    if (!file.exists()) {
      file.mkdirs();
    }
    log.info("tempPath:{}", file.getAbsolutePath());
    Mat dst=ImgprocUtils.extraMaxArea(src, debugInfo);
    
    HighGui.imshow("dst", dst);
    HighGui.waitKey();
    HighGui.destroyAllWindows();
  }

}
