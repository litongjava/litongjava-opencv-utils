package com.litongjava.opencv.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;

import com.litongjava.opencv.model.DebugInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Ping E Lee
 *
 */
@Slf4j
public class RecognitionUtilsTest {

  String tempPath = "D:\\opencv-images\\plate\\temp";

  @Before
  public void before() {
    OpenCVLibraryUtils.init();
  }

  @Test
  public void test() throws IOException {
    List<String> imagePathList = new ArrayList<>();
    imagePathList.add("D:\\opencv-images\\plate\\plate-001.png");

    for (String imagePath : imagePathList) {
      DebugInfo debugInfo = new DebugInfo(imagePath, true, tempPath);

      byte[] imageBytes = Files.readAllBytes(Paths.get(imagePath));
      Mat src = MatUtils.imread(imageBytes);
      Mat extraRecogArea = RecognitionUtils.extraRecogArea(src, debugInfo);
      HighGui.imshow("extraRecogArea", extraRecogArea);

    }
    HighGui.waitKey();
  }
}
