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

    Map<String, TrafficLight> result = new LinkedHashMap<>();
    for (String imagePath : imagePathList) {
      DebugInfo debugInfo = new DebugInfo(imagePath, true, tempPath);

      byte[] imageBytes = FileUtils.readFileToByteArray(new File(imagePath));
      Mat src = MatUtils.imread(imageBytes);
      Mat extraRecogArea = RecognitionUtils.extraRecogArea(src, debugInfo);
      HighGui.imshow("extraRecogArea", extraRecogArea);

    }
    HighGui.waitKey();
    // result.forEach((k, v) -> {
    // System.out.println(k + "=" + v.toString());
    // });

    // 比较结果集
    // Map<String, String[]> correctResult = getCorrectResult();
  }

  @Test
  public void toHsv() throws IOException {
    String imagePath = "D:\\opencv-images\\plate\\temp\\plate-001-maxArea-maxArea.png";
    byte[] imageBytes = FileUtils.readFileToByteArray(new File(imagePath));
    Mat src = MatUtils.imread(imageBytes);

    Mat hsv = new Mat();
    Imgproc.cvtColor(src, hsv, Imgproc.COLOR_BGR2HSV);

    MatUtils.debugToFileToFolder(hsv, tempPath, "hsv.png");
  }

  @Test
  public void ehance() throws IOException {
    String imagePath = "D:\\opencv-images\\plate\\temp\\plate-001-maxArea-maxArea.png";
    DebugInfo debugInfo = new DebugInfo(imagePath, true, tempPath);
    Boolean isSave = debugInfo.getIsSave();
    String baseName = debugInfo.getBaseName();
    String extensionName = debugInfo.getExtensionName();
    String tempPath = debugInfo.getTempPath();

    Boolean isUpload = debugInfo.getIsUpload();
    String uploadHost = debugInfo.getUploadHost();

    byte[] imageBytes = FileUtils.readFileToByteArray(new File(imagePath));
    Mat src = MatUtils.imread(imageBytes);
    // 判断白色图像的面积,如果大于某个值这说明是白底图像,不进行背景替换,否则将背景替换成白色
    double persent = ShapeUtils.getWhiteRatio(src);
    if (persent < 0.10) {
      // 将背景替换成白色
      Mat backgrand2Wite = KmeansUtils.backgrand2White(src);
      // 保存到文件
      String whiteName = MatUtils.getBaseName(baseName, "white");
      String whiteDstPath = MatUtils.getDstPath(tempPath, whiteName, extensionName);
      log.info("save file name:{}", whiteDstPath);
      MatUtils.debugToFile(isSave, backgrand2Wite, whiteName, extensionName, whiteDstPath, isUpload, uploadHost);
      src = backgrand2Wite.clone();
    }

    // 进行图像增强
    Mat usm = ImgprocUtils.usm(src);
    String usmName = MatUtils.getBaseName(baseName, "usm");
    String usmDstPath = MatUtils.getDstPath(tempPath, usmName, extensionName);
    log.info("save file name:{}", usmName);
    MatUtils.debugToFile(isSave, usm, usmName, extensionName, usmDstPath, isUpload, uploadHost);
    
    // 灰度化
    Mat gray = new Mat();
    Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
    // 保存灰度化结果
    String grayName = baseName + "-gray";
    String grayDstPath = tempPath + File.separator + grayName + "." + extensionName;
    MatUtils.debugToFile(isSave, gray, grayName, extensionName, grayDstPath, isUpload, uploadHost);

    // 均值滤波
    Size ksize = new Size(5, 5);
    Mat blur = new Mat();
    Imgproc.blur(gray, blur, ksize);

    String blurName = baseName + "-blur";
    String blurDstPath = tempPath + File.separator + blurName + "." + extensionName;
    MatUtils.debugToFile(isSave, blur, blurName, extensionName, blurDstPath, isUpload, uploadHost);

    // 二值化
    Mat threshed = new Mat(blur.rows(), blur.cols(), CvType.CV_8UC1);
    int type = Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU;
    Imgproc.threshold(blur, threshed, 0, 255, type);

    String threshedName = blurName + "-threshed_" + 0 + "_" + 255 + "_" + type;
    String threshedDstPath = tempPath + File.separator + threshedName + "." + extensionName;
    MatUtils.debugToFile(isSave, threshed, threshedName, extensionName, threshedDstPath, isUpload, uploadHost);
  }

  @Test
  public void threshold() throws IOException {
    String imagePath = "D:\\opencv-images\\plate\\temp\\plate-001-maxArea-maxArea.png";
    byte[] imageBytes = FileUtils.readFileToByteArray(new File(imagePath));
    Mat src = MatUtils.imread(imageBytes);

    Mat gray = new Mat();
    Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);

    Mat threshed = new Mat(gray.rows(), gray.cols(), CvType.CV_8UC1);
    int type = Imgproc.THRESH_BINARY;
    for (int i = 0; i < 255; i++) {
      double thresh = i;
      Imgproc.threshold(gray, threshed, thresh, 255, type);
      MatUtils.debugToFileToFolder(threshed, tempPath, "threshed-0-" + thresh + ".png");
    }
  }

}
