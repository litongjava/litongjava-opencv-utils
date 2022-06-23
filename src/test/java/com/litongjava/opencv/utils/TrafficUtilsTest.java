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

import com.litongjava.opencv.model.DebugInfo;
import com.litongjava.opencv.model.TrafficLight;

/**
 * @author Ping E Lee
 * 交通灯识别测试
 */
public class TrafficUtilsTest {

  String tempPath = "D:\\opencv-images\\traffic\\temp";

  @Before
  public void before() {
    OpenCVLibraryUtils.init();
  }

  @Test
  public void test() throws IOException {
    List<String> imagePathList = new ArrayList<>();
    imagePathList.add("D:\\opencv-images\\traffic\\red.jpg");
    imagePathList.add("D:\\opencv-images\\traffic\\green.jpg");
    imagePathList.add("D:\\opencv-images\\traffic\\yellow.jpg");
    imagePathList.add("D:\\opencv-images\\traffic\\green-2.jpg");
    imagePathList.add("D:\\opencv-images\\traffic\\green-3.jpg");
    imagePathList.add("D:\\opencv-images\\traffic\\green-4.jpg");
    imagePathList.add("D:\\opencv-images\\traffic\\green-5.jpg");
    imagePathList.add("D:\\opencv-images\\traffic\\green-6.jpg");
    imagePathList.add("D:\\opencv-images\\traffic\\green-7.jpg");

    Map<String, TrafficLight> result = new LinkedHashMap<>();
    for (String imagePath : imagePathList) {
      DebugInfo debugInfo = new DebugInfo(imagePath, true, tempPath);

      byte[] imageBytes = FileUtils.readFileToByteArray(new File(imagePath));
      TrafficLight trafficLight = TrafficLightUtils.index(imageBytes, debugInfo);
      result.put(imagePath, trafficLight);
    }
    result.forEach((k, v) -> {
      System.out.println(k + "=" + v.toString());
    });

    // 比较结果集
    // Map<String, String[]> correctResult = getCorrectResult();
  }

}
