package com.litongjava.opencv.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.opencv.core.Mat;

import com.litongjava.opencv.model.DebugInfo;

import nu.pattern.OpenCV;

/**
 * @author Ping E Lee
 *
 */
public class RecognitionPlateUtilsTest {

  String tempPath = "D:\\opencv-images\\plate\\temp";
  
  @Before
  public void before() {
    OpenCV.loadLocally();
  }
  
  @Test
  public void test() throws IOException {
    String imagePath="F:\\opencv-images\\plate\\plate-001.png";
    DebugInfo debugInfo = new DebugInfo(imagePath, true, tempPath);
    
    byte[] imageBytes = Files.readAllBytes(Paths.get(imagePath));
    Mat src = MatUtils.imread(imageBytes);
    RecognitionPlateUtils.pre(src, debugInfo); 
  }
}
