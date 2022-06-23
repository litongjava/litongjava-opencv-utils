package com.litongjava.opencv.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import com.litongjava.opencv.constatns.HsvConstants;
import com.litongjava.opencv.model.DebugInfo;
import com.litongjava.opencv.model.TrafficLight;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TrafficLightUtils {

  public static TrafficLight index(byte[] imageBytes, DebugInfo debugInfo) throws IOException {
    return v1(imageBytes, debugInfo);
  }

  /**
   * 1.提取红色,检测轮廓,roi遮罩
   * 2.提取绿色,检测轮廓,roi遮罩
   * 3.提取黄色,检测轮廓,roi遮罩
   * 4.计算亮度值
   * @param imageBytes
   * @param debugInfo
   * @return
   * @throws IOException 
   */
  private static TrafficLight v3(byte[] imageBytes, DebugInfo debugInfo) throws IOException {

    // 读取文件
    Mat src = MatUtils.imread(imageBytes);

    // 1 灰度图像 3 彩色图像
    int channels = src.channels();
    int rows = src.rows();
    int cols = src.cols();
    if (debugInfo.getIsLog()) {
      log.info("channels:{},rows:{},cols:{}", channels, rows, cols);
    }
    if (channels == 1) {
      log.error("请输出彩色图像");
      return new TrafficLight();
    }

    Boolean isSave = debugInfo.getIsSave();
    String baseName = debugInfo.getBaseName();
    String extensionName = debugInfo.getExtensionName();
    String tempPath = debugInfo.getTempPath();

    Boolean isUpload = debugInfo.getIsUpload();
    String uploadHost = debugInfo.getUploadHost();

    // 判断白色图像的面积,如果大于某个值这说明是白底图像,不进行背景替换,否则将背景替换成白色
    // double persent = ShapeUtils.getWhiteRatio(src);
    // if (persent < 0.10) {
    // log.info("进行背景转换");
    // // 将背景替换成白色
    // Mat backgrand2Wite = KmeansUtils.backgrand2White(src);
    // // 保存到文件
    // String whiteName = MatUtils.getBaseName(baseName, "white");
    // String whiteDstPath = MatUtils.getDstPath(tempPath, whiteName,
    // extensionName);
    // log.info("save file name:{}", whiteDstPath);
    // MatUtils.debugToFile(isSave, backgrand2Wite, whiteName, extensionName,
    // whiteDstPath, isUpload, uploadHost);
    // src=backgrand2Wite.clone();
    // }

    Mat light = convertLight(src, debugInfo);

    // 进行图像增强
    Mat usm = ImgprocUtils.usm(light);
    String usmName = MatUtils.getBaseName(baseName, "usm");
    String usmDstPath = MatUtils.getDstPath(tempPath, usmName, extensionName);
    log.info("save file name:{}", usmName);
    MatUtils.debugToFile(isSave, usm, usmName, extensionName, usmDstPath, isUpload, uploadHost);

    src = usm.clone();

    // 转成hsv
    Mat hsv = new Mat();
    Imgproc.cvtColor(src, hsv, Imgproc.COLOR_BGR2HSV);

    String hsvFilename = MatUtils.getBaseName(baseName, "hsv");
    String hsvDstPath = MatUtils.getDstPath(tempPath, hsvFilename, extensionName); // 保存到文件
    log.info("save file name:{}", hsvFilename);
    MatUtils.debugToFile(isSave, hsv, hsvFilename, extensionName, hsvDstPath, isUpload, uploadHost);

    // 提取颜色
    Mat redMask = ColorDivisionUtils.colorDivision(hsv, HsvConstants.lower_red, HsvConstants.upper_red);
    Mat greenMask = ColorDivisionUtils.colorDivision(hsv, HsvConstants.lower_green, HsvConstants.upper_green);
    Mat yellowMask = ColorDivisionUtils.colorDivision(hsv, HsvConstants.lower_yellow, HsvConstants.upper_yellow);

    Core.bitwise_or(redMask, greenMask, greenMask);
    Core.bitwise_or(greenMask, yellowMask, yellowMask);

    String maskFilename = MatUtils.getBaseName(baseName, "mask");
    String maskDstPath = MatUtils.getDstPath(tempPath, maskFilename, extensionName);
    MatUtils.debugToFile(isSave, yellowMask, hsvFilename, extensionName, maskDstPath, isUpload, uploadHost);

    // 查找圆形轮廓
    Mat hierarchy = new Mat();
    List<MatOfPoint> contours = new ArrayList<>();
    Imgproc.findContours(yellowMask, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
    log.info("contours.size():{}", contours.size());

    List<MatOfPoint> circleContours = ContoursUtils.extraCircleContours(src, contours);
    log.info("circleContours.size():{}", circleContours.size());

    Mat maskCircle = Mat.zeros(src.size(), CvType.CV_8UC3);
    for (int i = 0; i < circleContours.size(); i++) {
      Imgproc.drawContours(maskCircle, contours, i, new Scalar(255, 255, 255), -1, 8, new Mat(), 0);
    }
    String maskCircleFilename = MatUtils.getBaseName(baseName, "mask-circle");
    String maskCircleDstPath = MatUtils.getDstPath(tempPath, maskCircleFilename, extensionName);
    MatUtils.debugToFile(isSave, maskCircle, maskCircleFilename, extensionName, maskCircleDstPath, isUpload, uploadHost);

    // 提取出颜色信息
    Mat dst = new Mat();
    src.copyTo(dst, maskCircle);

    // 保存图片
    String dstFilename = MatUtils.getBaseName(baseName, "traffic-light");
    String dstDstPath = MatUtils.getDstPath(tempPath, dstFilename, extensionName);
    MatUtils.debugToFile(isSave, dst, dstFilename, extensionName, dstDstPath, isUpload, uploadHost);

    return new TrafficLight();
  }

  /**
   * 识别交通灯颜色
   * @param src
   * @param hsv
   * @param debugInfo
   * @throws IOException
   */
  private static TrafficLight recognizeLigth(String colorSuffix, int colorId, Scalar lowerb, Scalar upperb, Mat src, Mat hsv, DebugInfo debugInfo)
      throws IOException {
    Boolean isSave = debugInfo.getIsSave();
    String baseName = debugInfo.getBaseName();
    String extensionName = debugInfo.getExtensionName();
    String tempPath = debugInfo.getTempPath();

    Boolean isUpload = debugInfo.getIsUpload();
    String uploadHost = debugInfo.getUploadHost();

    // 提取颜色
    Mat redMask = ColorDivisionUtils.colorDivision(hsv, lowerb, upperb);
    // 保存分离后的图像
    String colorBaseName = MatUtils.getBaseName(baseName, colorSuffix);
    String dstPath = MatUtils.getDstPath(tempPath, colorBaseName, extensionName);
    log.info("save file name:{}", colorBaseName);
    MatUtils.debugToFile(isSave, redMask, colorBaseName, extensionName, dstPath, isUpload, uploadHost);

    // 查找轮廓
    Mat hierarchy = new Mat();
    List<MatOfPoint> contours = new ArrayList<>();
    Imgproc.findContours(redMask, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
    // 画出轮廓
    ImgprocUtils.drawContours(src, contours, debugInfo);

    int contourSize = contours.size();
    log.info("contourSize:{}", contourSize);

    if (ContoursUtils.isCircle(contours)) {
      return new TrafficLight(colorId);
    }
    return new TrafficLight();
  }

  /**
   * 转为HSV,然后提取红绿黄
   * @param imageBytes
   * @param debugInfo
   * @return
   * @throws IOException 
   */
  private static TrafficLight v2(byte[] imageBytes, DebugInfo debugInfo) throws IOException {
    // 读取文件
    Mat src = MatUtils.imread(imageBytes);
    // 1 灰度图像 3 彩色图像
    int channels = src.channels();
    int rows = src.rows();
    int cols = src.cols();
    if (debugInfo.getIsLog()) {
      log.info("channels:{},rows:{},cols:{}", channels, rows, cols);
    }
    if (channels == 1) {
      log.error("请输出彩色图像");
      return new TrafficLight();
    }
    Boolean isSave = debugInfo.getIsSave();
    String baseName = debugInfo.getBaseName();
    String extensionName = debugInfo.getExtensionName();
    String tempPath = debugInfo.getTempPath();

    Boolean isUpload = debugInfo.getIsUpload();
    String uploadHost = debugInfo.getUploadHost();

    // 判断白色图像的面积,如果大于某个值这说明是白底图像,不进行背景替换,否则将背景替换成白色
    double persent = ShapeUtils.getWhiteRatio(src);
    if (persent < 0.10) {
      // 将背景替换成白色
      Mat backgrand2Wite = KmeansUtils.backgrand2White(src);
      // 保存到文件
      if (debugInfo.getIsSave()) {
        String whiteName = MatUtils.getBaseName(baseName, "white");
        String whiteDstPath = MatUtils.getDstPath(tempPath, whiteName, extensionName);
        log.info("save file name:{}", whiteDstPath);
        MatUtils.debugToFile(isSave, backgrand2Wite, whiteName, extensionName, whiteDstPath, isUpload, uploadHost);
      }
      src = backgrand2Wite.clone();
    }
    return null;
  }

  /**
   * 使用加权算法将彩色图像转为灰色图像
   * 统计转换后图像的亮度值
   * 
   * 已知问题
   * 1.如果绿色和红色条幅显示在图片中会识别成红色,暂时没有解决办法
   * @param imageBytes
   * @param debugInfo
   * @return
   * @throws IOException 
   */
  private static TrafficLight v1(byte[] imageBytes, DebugInfo debugInfo) throws IOException {
    // 读取文件
    Mat src = MatUtils.imread(imageBytes);
    // 1 灰度图像 3 彩色图像
    int channels = src.channels();
    if (channels == 1) {
      log.error("请输出彩色图像");
      return new TrafficLight();
    }
    String baseName = debugInfo.getBaseName();
    String tempPath = debugInfo.getTempPath();
    Boolean isSave = debugInfo.getIsSave();
    Boolean isUpload = debugInfo.getIsUpload();
    String uploadHost = debugInfo.getUploadHost();

    String extensionName = debugInfo.getExtensionName();

    // 加权亮度,小于加权转为黑色
    Mat light = convertLight(src, debugInfo);
    // Mat light =KmeansUtils.backgrand2White(src);
    // 保存图片
    String lightName = baseName + "-light";
    String lightDstPath = tempPath + File.separator + lightName + "." + extensionName;
    MatUtils.debugToFile(isSave, light, lightName, extensionName, lightDstPath, isUpload, uploadHost);

    return recognizeColor(light);
  }

  /**
   * 识别颜色
   * @param light
   * @return
   */
  private static TrafficLight recognizeColor(Mat src) {
    int rows = src.rows();
    int cols = src.cols();

    int colorNum[] = new int[3];
    for (int y = 0; y < rows; y++) {
      for (int x = 0; x < cols; x++) {
        // 按为提取颜色默认是argb,
        double[] value = src.get(y, x);
        int b = (int) value[0];// & 0xff;
        int g = (int) value[1];// & 0xff;
        int r = (int) value[2];// & 0xff;

        if (r > 240 && b < 220 && g < 220) {
          colorNum[0]++; // 红色
        } else if (r < 220 && b < 220 && g > 240) {
          colorNum[1]++; // 绿色
        } else if (r > 240 && g > 240 && b < 220) {
          colorNum[2]++; // 黄色
        }
      }
    }

    if (colorNum[0] > colorNum[1] && colorNum[0] > colorNum[2]) {
      return new TrafficLight(1);
    } else if (colorNum[1] > colorNum[0] && colorNum[1] > colorNum[2]) {
      return new TrafficLight(2);
    } else if (colorNum[2] > colorNum[0] && colorNum[2] > colorNum[1]) {
      return new TrafficLight(3);
    }

    return new TrafficLight();
  }

  /**
   * 加权亮度,小于加权转为黑色
   * @param debugInfo
   * @param src
   * @param rows
   * @param cols
   * @return 
   * @throws IOException
   */
  public static Mat convertLight(Mat src, DebugInfo debugInfo) throws IOException {
    // 将图片中非亮度部分设置为黑色
    int rows = src.rows();
    int cols = src.cols();
    Mat light = new Mat(new Size(cols, rows), src.type()); // 黑色
    int maxBright = 0;
    for (int y = 0; y < rows; y++) {
      for (int x = 0; x < cols; x++) {
        // 按为提取颜色默认是argb,
        double[] value = src.get(y, x);
        int b = (int) value[0];// & 0xff;
        int g = (int) value[1];// & 0xff;
        int r = (int) value[2];// & 0xff;

        // 加权平均值对应公式,得到明亮度,即灰度值
        int bright = (int) (0.3 * r + 0.59 * g + 0.11 * b);
        maxBright = maxBright > bright ? maxBright : bright;
        // if (debugInfo.getIsLog()) {
        // log.info("y:{},x:{},bright:{}", y,x,bright);
        // }
        // 当明亮度低于128的时置为黑色,当高于128是设置为原有色,在查找轮廓时,因为颜色太亮,导致没有找到轮廓
        // 当明亮度低于128的时为元有色,当高于128是设置为白色

        if (bright < 256 / 2) {
          // if (bright < 200) {
          // 设置为原有色,默认是黑色
          // light.put(y, x, value);
        } else {
          light.put(y, x, value);
        }
      }
    }
    log.info("maxBright:{}", maxBright);

    Boolean isSave = debugInfo.getIsSave();
    String baseName = debugInfo.getBaseName();
    String extensionName = debugInfo.getExtensionName();
    String tempPath = debugInfo.getTempPath();

    Boolean isUpload = debugInfo.getIsUpload();
    String uploadHost = debugInfo.getUploadHost();
    String blackName = MatUtils.getBaseName(baseName, "light");
    String blackDstPath = MatUtils.getDstPath(tempPath, blackName, extensionName);
    // 保存到文件
    MatUtils.debugToFile(isSave, light, blackName, extensionName, blackDstPath, isUpload, uploadHost);
    return light;
  }

}