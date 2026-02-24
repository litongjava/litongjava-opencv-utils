package com.litongjava.opencv.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.litongjava.opencv.model.DebugInfo;
import com.litongjava.opencv.model.Shape;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RecognitionShapeUtilsTest {

  String tempPath = "D:\\opencv-images\\shape\\temp";

  @Before
  public void before() {
    OpenCVLibraryUtils.init();
  }

  @Test
  public void testRecognizeShape() throws IOException {
    List<String> imagePathList = new ArrayList<>();
     imagePathList.add("D:\\opencv-images\\shape\\shape-001.png");
    imagePathList.add("D:\\opencv-images\\shape\\shape-002.jpg");
    imagePathList.add("D:\\opencv-images\\shape\\shape-003.jpg");
     imagePathList.add("D:\\opencv-images\\shape\\shape-004.jpg");
     imagePathList.add("D:\\opencv-images\\shape\\shape-005.jpg");
     imagePathList.add("D:\\opencv-images\\shape\\shape-006.jpg");
     imagePathList.add("D:\\opencv-images\\shape\\shape-007.jpg");
     imagePathList.add("D:\\opencv-images\\shape\\shape-008.jpg");
     imagePathList.add("D:\\opencv-images\\shape\\shape-009.jpg");
    Map<String, String[]> result = new LinkedHashMap<>();
    for (String imagePath : imagePathList) {
      DebugInfo debugInfo = new DebugInfo(imagePath, true, tempPath);
      // 读取文件
      byte[] imageBytes = Files.readAllBytes(Paths.get(imagePath));
      List<Shape> shapList = RecognitionShapeUtils.index(imageBytes, debugInfo);
      String[] formatToArray = RecognitionShapeUtils.formatToArray(shapList);
      result.put(imagePath, formatToArray);
    }
    result.forEach((k, v) -> {
      System.out.println(k + "=" + Arrays.toString(v));
    });

    // 比较结果集
    Map<String, String[]> correctResult = getCorrectResult();

  }

  private Map<String, String[]> getCorrectResult() {
    List<String> imagePathList = new ArrayList<>();
    imagePathList.add("D:\\opencv-images\\shape\\shape-001.png");
    imagePathList.add("D:\\opencv-images\\shape\\shape-002.png");
    imagePathList.add("D:\\opencv-images\\shape\\shape-003.jpg");
    imagePathList.add("D:\\opencv-images\\shape\\shape-004.jpg");
    imagePathList.add("D:\\opencv-images\\shape\\shape-005.jpg");
    imagePathList.add("D:\\opencv-images\\shape\\shape-006.jpg");
    imagePathList.add("D:\\opencv-images\\shape\\shape-007.jpg");
    imagePathList.add("D:\\opencv-images\\shape\\shape-008.jpg");

    return null;
  }
}
