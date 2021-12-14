package com.litongjava.opencv.utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import com.litongjava.opencv.constatns.HsvConstants;
import com.litongjava.opencv.model.DebugInfo;

/**
 * 形状变换
 * 
 * @author Ping
 *
 */
//@Slf4j
public class ShapeUtils {

  /**
   * 返回图像轮廓
   * 
   * @param morphology 经过形态变换的二值化矩阵
   * @param debugInfo
   * @throws IOException
   */
  public static List<Mat> getShapeContours(Mat morphology, DebugInfo debugInfo) throws IOException {
    Boolean isSave = debugInfo.getIsSave();
    String tempPath = debugInfo.getTempPath();
    String baseName = debugInfo.getBaseName();
    String extensionName = debugInfo.getExtensionName();

    Boolean isUpload = debugInfo.getIsUpload();
    String uploadHost = debugInfo.getUploadHost();

    List<Mat> retval = new ArrayList<>();

    // 查找二进制图像的轮廓
    Mat hierarchy = new Mat();
    List<MatOfPoint> contours = new ArrayList<>();
    Imgproc.findContours(morphology, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

    for (int i = 0; i < contours.size(); i++) {
      MatOfPoint contour = contours.get(i);
      double contourArea = Imgproc.contourArea(contour);
      // log.info("contourArea:{}", contourArea);
      // 过滤面积比较小和比较大的轮廓
      if (contourArea < 50 || contourArea > 30000) {
        // log.info("过滤面积:{}", contourArea);
        continue;
      }
      Rect boundingRect = Imgproc.boundingRect(contour);
      // 过滤边框
      int height = morphology.rows();
      int width = morphology.cols();
      int x = boundingRect.x;
      int y = boundingRect.y;
      if (x < 10 || y < 10 || (x >= width - 10 && x <= width) || (y >= height - 10 && y <= height)) {
        // log.info("过滤边框:{}", boundingRect);
        continue;
      }

      // 直接识别图形并不十分的准确,先绘制一个边框再识别边框更准确
      MatOfPoint2f approxCurve = getShape(contour);
      // 在空白图形上画出图像
      ArrayList<MatOfPoint> list = new ArrayList<>();
      list.add(new MatOfPoint(approxCurve.toArray()));
      Mat approxCurveImage = Mat.zeros(morphology.size(), CvType.CV_8UC3);
      Imgproc.drawContours(approxCurveImage, list, 0, new Scalar(0, 0, 255));
      // HighGui.imshow(i + "", approxCurveImage);
      retval.add(approxCurveImage);

      // 保存图像
      String contourName = MatUtils.getBaseName(baseName, "contour_" + i);
      String dstPath = MatUtils.getDstPath(tempPath, contourName, extensionName);
      MatUtils.debugToFile(isSave, approxCurveImage, contourName, extensionName, dstPath, isUpload, uploadHost);
    }
    return retval;
  }

  /**
   * 返回图像形状的多边形曲线
   * 
   * @param contour
   * @return
   */
  public static MatOfPoint2f getShape(MatOfPoint contour) {
    MatOfPoint2f matOfPoint2f = new MatOfPoint2f();
    contour.convertTo(matOfPoint2f, CvType.CV_32F);
    // 计算弧长
    double arcLength = Imgproc.arcLength(matOfPoint2f, true);
    // log.info("arcLength:{}", arcLength);
    // 以指定的精确到近似多边形曲线
    MatOfPoint2f approxCurve = new MatOfPoint2f();
    // 使用0.03可以检测到菱形
    Imgproc.approxPolyDP(matOfPoint2f, approxCurve, 0.003 * arcLength, true);
//    log.info("approxCurve:{}", approxCurve);
    return approxCurve;
  }

  /**
   * 查找最外部的轮廓
   * 
   * @param debugInfo
   */
  public static List<MatOfPoint> findExternalContours(Mat src, DebugInfo debugInfo) {
    // 再次进行灰度化
    Mat grayExternal = new Mat();
    Imgproc.cvtColor(src, grayExternal, Imgproc.COLOR_RGB2GRAY);
    // 再次进行二值化
    Mat thresholdedExternal = new Mat(grayExternal.rows(), grayExternal.cols(), CvType.CV_8UC1);
    int type = Imgproc.THRESH_BINARY | Imgproc.THRESH_TRIANGLE;
    Imgproc.threshold(grayExternal, thresholdedExternal, 0, 255, type);
    // 闭运算
    Mat morphologyExternal = new Mat(thresholdedExternal.rows(), thresholdedExternal.cols(), CvType.CV_8UC1);
    Mat structuringElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
    Imgproc.morphologyEx(thresholdedExternal, morphologyExternal, Imgproc.MORPH_CLOSE, structuringElement,
        new Point(-1, -1), 1);

    // 仅检测最外部轮廓
    Mat hierarchyExternal = new Mat();
    List<MatOfPoint> contoursExternal = new ArrayList<>();
    Imgproc.findContours(morphologyExternal, contoursExternal, hierarchyExternal, Imgproc.RETR_EXTERNAL,
        Imgproc.CHAIN_APPROX_SIMPLE);
    return contoursExternal;
  }

  public static double getWhiteRatio(Mat src) {
    Mat temphsv = new Mat();
    Imgproc.cvtColor(src, temphsv, Imgproc.COLOR_BGR2HSV);
    // 提取hsv中的白色
    Mat colorDivision = RecognitionShapeUtils.colorDivision("白色", temphsv, HsvConstants.lower_white,
        HsvConstants.upper_white);

    Mat thresholded = new Mat(colorDivision.rows(), colorDivision.cols(), CvType.CV_8UC1);
    int type = Imgproc.THRESH_BINARY | Imgproc.THRESH_TRIANGLE;
    Imgproc.threshold(colorDivision, thresholded, 0, 255, type);
    int count = Core.countNonZero(thresholded);
    int total = thresholded.rows() * thresholded.cols();

    // 计算白色点在图形中占据的比值
    double persent = new BigDecimal((float) count / total).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    return persent;
  }
}
