package com.litongjava.opencv.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import com.litongjava.opencv.model.DebugInfo;
import com.litongjava.opencv.model.MaxAreaBo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImgprocUtils {

  public static void boundingRect() {
  }

  /**
   * 查找轮廓并返回
   * 
   * @param src
   * @param debugInfo
   * @return
   * @throws IOException
   */
  public static List<MatOfPoint> findContours(Mat src, DebugInfo debugInfo) throws IOException {
    String baseName = debugInfo.getBaseName();
    String tempPath = debugInfo.getTempPath();
    Boolean isSave = debugInfo.getIsSave();
    Boolean isUpload = debugInfo.getIsUpload();
    String uploadHost = debugInfo.getUploadHost();

    String extesnionName = debugInfo.getExtensionName();

    // 灰度化
    Mat gray = new Mat();
    Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);

    String grayName = baseName + "-gray";
    String grayDstPath = tempPath + File.separator + grayName + "." + extesnionName;
    MatUtils.debugToFile(isSave, gray, grayName, extesnionName, grayDstPath, isUpload, uploadHost);
    // 均值滤波
    Size ksize = new Size(5, 5);
    Mat blur = new Mat();
    Imgproc.blur(gray, blur, ksize);

    String blurName = baseName + "-blur";
    String blurDstPath = tempPath + File.separator + blurName + "." + extesnionName;
    MatUtils.debugToFile(isSave, blur, blurName, extesnionName, blurDstPath, isUpload, uploadHost);

    // 二值化
    Mat threshed = new Mat(blur.rows(), blur.cols(), CvType.CV_8UC1);
    int type = Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU;
    Imgproc.threshold(blur, threshed, 0, 255, type);

    String threshedName = blurName + "-threshed_" + 0 + "_" + 255 + "_" + type;
    String threshedDstPath = tempPath + File.separator + threshedName + "." + extesnionName;
    MatUtils.debugToFile(isSave, threshed, threshedName, extesnionName, threshedDstPath, isUpload, uploadHost);

    // 查找轮廓
    Mat hierarchy = new Mat();
    List<MatOfPoint> contours = new ArrayList<>();
    Imgproc.findContours(threshed, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
    int contourSize = contours.size();
    log.info("contourSize:{}", contourSize);
    return contours;
  }

  /**
   * 查找图片中的轮廓并绘制出来
   * 
   * @return
   * @throws IOException
   */
  public static Mat findContoursAndDraw(Mat src, DebugInfo debugInfo) throws IOException {
    Boolean isSave = debugInfo.getIsSave();
    String tempPath = debugInfo.getTempPath();
    String baseName = debugInfo.getBaseName();
    String extensionName = debugInfo.getExtensionName();

    Boolean isUpload = debugInfo.getIsUpload();
    String uploadHost = debugInfo.getUploadHost();

    List<MatOfPoint> contours = findContours(src, debugInfo);
    // 画出轮廓
    Imgproc.drawContours(src, contours, -1, new Scalar(0, 0, 255));
    String drawContoursName = baseName + "-contours";
    String drawContoursDstPath = tempPath + File.separator + drawContoursName + "." + extensionName;
    MatUtils.debugToFile(isSave, src, drawContoursName, extensionName, drawContoursDstPath, isUpload, uploadHost);
    return src;
  }

  /**
   * 提取最大面积的轮廓
   * 
   * @param src
   * @param debugInfo
   * @return
   * @throws IOException
   */
  public static Mat extraMaxArea(Mat src, DebugInfo debugInfo) throws IOException {
    MaxAreaBo maxArea = getMaxArea(src, debugInfo);
    // 输出信息
    log.info("areaMax:{},rectMax:{}", maxArea.getAreaMax(), maxArea.getRectMax());
    Mat submat = src.submat(maxArea.getRectMax());
    // 不需要写入文件,调用程序会自动写入文件
    return submat;
  }

  /**
   * 提取最大面积的轮廓
   * 
   * @param src
   * @param maxArea
   * @param debugInfo
   * @return
   * @throws IOException
   */
  public static Mat extraMaxArea(Mat src, MaxAreaBo maxArea, DebugInfo debugInfo) throws IOException {
    // 裁剪图像
    Mat submat = src.submat(maxArea.getRectMax());
    // 不需要写入文件,调用程序会自动写入文件
    return submat;
  }

  /**
   * 查找面积最大的轮廓
   * 
   * @param src
   * @param debugInfo
   * @return
   * @throws IOException
   */
  public static MaxAreaBo getMaxArea(Mat src, DebugInfo debugInfo) throws IOException {
    Boolean isSave = debugInfo.getIsSave();
    String baseName = debugInfo.getBaseName();
    String extensionName = debugInfo.getExtensionName();
    String tempPath = debugInfo.getTempPath();

    Boolean isUpload = debugInfo.getIsUpload();
    String uploadHost = debugInfo.getUploadHost();

    // 灰度化
    Mat gray = new Mat();
    Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);

    String grayName = baseName + "-gray";
    String grayDstPath = tempPath + File.separator + grayName + "." + extensionName;
    log.info("save file name:{}", grayName);
    MatUtils.debugToFile(isSave, gray, grayName, extensionName, grayDstPath, isUpload, uploadHost);

    // 均值滤波
    Size ksize = new Size(5, 5);
    Mat blur = new Mat();
    Imgproc.blur(gray, blur, ksize);

    String blurName = MatUtils.getBaseName(grayName, "blur");
    String blurDstPath = MatUtils.getDstPath(tempPath, blurName, extensionName);
    log.info("save file name:{}", blurName);
    MatUtils.debugToFile(isSave, blur, blurName, extensionName, blurDstPath, isUpload, uploadHost);

    // 二值化
    Mat threshed = new Mat(blur.rows(), blur.cols(), CvType.CV_8UC1);
    int type = Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU;
    //int type = Imgproc.THRESH_BINARY + Imgproc.THRESH_TRIANGLE;
    Imgproc.threshold(blur, threshed, 0, 255, type);

    String thresholdName = MatUtils.getBaseName(grayName, "threshold");
    String thresholdDstPath = MatUtils.getDstPath(tempPath, thresholdName, extensionName);
    log.info("save file name:{}", thresholdName);
    MatUtils.debugToFile(isSave, threshed, thresholdName, extensionName, thresholdDstPath, isUpload, uploadHost);

    // 查找轮廓
    Mat hierarchy = new Mat();
    List<MatOfPoint> contours = new ArrayList<>();
    Imgproc.findContours(threshed, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
    int contourSize = contours.size();
    log.info("contourSize:{}", contourSize);

    // 新图片画出轮廓并保存
    Mat contoursMat = src.clone();
    Imgproc.drawContours(contoursMat, contours, -1, new Scalar(0, 0, 255));
    String drawContoursName = thresholdName + "-contours";
    String drawContoursDstPath = tempPath + File.separator + drawContoursName + "." + extensionName;
    log.info("save file name:{}", drawContoursName);
    MatUtils.debugToFile(isSave, contoursMat, drawContoursName, extensionName, drawContoursDstPath, isUpload,
        uploadHost);

    // 找最大面积轮廓
    double areaMax = 0;
    Rect rectMax = null;
    int width = gray.cols();
    int heigt = gray.rows();

    for (MatOfPoint cnt : contours) {
      Rect rect = Imgproc.boundingRect(cnt);
      int x = rect.x;
      int y = rect.y;
      double contourArea = Imgproc.contourArea(cnt);
      if (x < 5 || y < 5 || (width - 5 <= x && x <= width) || (heigt - 5 <= y && y <= heigt)) {
        log.info("过滤边框:{}", rect);
        continue;
      }
      if (contourArea > areaMax) {
        areaMax = contourArea;
        rectMax = rect;
      }
    }
    return new MaxAreaBo(areaMax, rectMax);
  }

  public static void boundingRectAndSubmat(Mat thresholded, DebugInfo debugInfo) throws IOException {
    Rect rect = Imgproc.boundingRect(thresholded);
    Boolean isSave = debugInfo.getIsSave();
    String tempPath = debugInfo.getTempPath();
    String baseName = debugInfo.getBaseName();
    String extensionName = debugInfo.getExtensionName();

    Boolean isUpload = debugInfo.getIsUpload();
    String uploadHost = debugInfo.getUploadHost();

    log.info("区域:{}", rect);
    if (isSave) {
      // 裁剪出区域的图像并输出
      Mat submat = thresholded.submat(rect);

      String boundingRectName = MatUtils.getBaseName(baseName, "boundingRectSubmat");
      String dstPath = MatUtils.getDstPath(tempPath, baseName, extensionName);
      log.info("boundingRectName:{}", boundingRectName);
      if (submat.empty()) {
        log.info("空图像:{}", boundingRectName);
      } else {
        // HighGui.imshow(boundingRectName, submat);
        MatUtils.debugToFile(isSave, submat, boundingRectName, extensionName, dstPath, isUpload, uploadHost);
      }
    }
  }

  /**
   * 将背景色设置为白色
   * 
   * @throws IOException
   */
  public static Mat backToWhite(Mat src, DebugInfo debugInfo) throws IOException {
    Boolean isSave = debugInfo.getIsSave();
    String baseName = debugInfo.getBaseName();
    String extensionName = debugInfo.getExtensionName();
    String tempPath = debugInfo.getTempPath();

    Boolean isUpload = debugInfo.getIsUpload();
    String uploadHost = debugInfo.getUploadHost();

    // 灰度化
    Mat gray = new Mat();
    Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);

    String grayName = baseName + "-gray";
    String grayDstPath = tempPath + File.separator + grayName + "." + extensionName;
    log.info("save file name:{}", grayName);
    MatUtils.debugToFile(isSave, gray, grayName, extensionName, grayDstPath, isUpload, uploadHost);

    // 均值滤波
    Size ksize = new Size(5, 5);
    Mat blur = new Mat();
    Imgproc.blur(gray, blur, ksize);

    String blurName = MatUtils.getBaseName(grayName, "blur");
    String blurDstPath = MatUtils.getDstPath(tempPath, blurName, extensionName);
    log.info("save file name:{}", blurName);
    MatUtils.debugToFile(isSave, blur, blurName, extensionName, blurDstPath, isUpload, uploadHost);

    // 二值化
    Mat threshed = new Mat(blur.rows(), blur.cols(), CvType.CV_8UC1);
    int type = Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU;
    Imgproc.threshold(blur, threshed, 0, 255, type);

    Mat whithBack = Mat.zeros(src.size(), src.type());
    // java不支持Scalar运算符重载
    // redBack = Scalar(40, 40, 200);
    // 只能遍历了,遍历也设置不了
    // 使用not测试
    Core.bitwise_not(whithBack, whithBack);

    Mat mask = threshed.clone();
    // 反向
    Core.bitwise_not(mask, mask);
    // mask不为0的迁移过来
    Core.copyTo(src, whithBack, mask);

    return whithBack;
  }

  /**
   * 使用usm对图片进行锐化增强
   * 
   * @param src
   * @return
   */
  public static Mat usm(Mat src) {
    Mat blur = new Mat();
    Imgproc.GaussianBlur(src, blur, new Size(0, 0), 25);
    Mat usm = new Mat();
    Core.addWeighted(src, 1.5, blur, -0.5, 0, usm);

//    Mat roi = new Mat(new Size(src.cols(), src.rows()), CvType.CV_8UC3, new Scalar(255, 255, 255));
//    Mat dst = new Mat();
//    Core.addWeighted(usm, 1.275, roi, 0.00015, 0, dst);
    return usm;
  }

}
