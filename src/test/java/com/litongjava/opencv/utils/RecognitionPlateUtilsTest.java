package com.litongjava.opencv.utils;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.opencv.core.Mat;

import com.litongjava.opencv.model.DebugInfo;

/**
 * @author Ping E Lee
 *
 */
public class RecognitionPlateUtilsTest {

  String tempPath = "D:\\opencv-images\\plate\\temp";
  
  @Before
  public void before() {
    OpenCVLibraryUtils.init();
  }
  
  @Test
  public void test() throws IOException {
    String imagePath="D:\\opencv-images\\plate\\plate-001.png";
    DebugInfo debugInfo = new DebugInfo(imagePath, true, tempPath);
    
    byte[] imageBytes = FileUtils.readFileToByteArray(new File(imagePath));
    Mat src = MatUtils.imread(imageBytes);
    
    RecognitionPlateUtils.pre(src, debugInfo);
    
  }

}
