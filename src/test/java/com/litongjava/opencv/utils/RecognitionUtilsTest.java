package com.litongjava.opencv.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

import com.litongjava.opencv.model.DebugInfo;
import com.litongjava.opencv.model.TrafficLight;

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

      byte[] imageBytes = FileUtils.readFileToByteArray(new File(imagePath));
      Mat src = MatUtils.imread(imageBytes);
      Mat extraRecogArea = RecognitionUtils.extraRecogArea(src, debugInfo);
      HighGui.imshow("extraRecogArea", extraRecogArea);

    }
    HighGui.waitKey();
  }
}
