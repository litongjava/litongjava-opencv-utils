package com.litongjava.opencv.utils;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * @author Ping E Lee
 *
 */
public class ContoursUtils {

  public static Mat extraCircleArea(Mat src, List<MatOfPoint> contours) {
    // 创建一个空白的图像
    Mat mask = Mat.zeros(src.size(), CvType.CV_8UC3);
    // for (size_t t = 0; t < contours.size(); t++) {
    for (int i = 0; i < contours.size(); i++) {
      // double area = contourArea(contours[t]);
      double area = Imgproc.contourArea(contours.get(i));
      if(area<100) {
        continue;
      }
      // Rect rect = boundingRect(contours[t]);
      Rect rect = Imgproc.boundingRect(contours.get(i));
      // float ratio = float(rect.width) / float(rect.height);
      float ratio = (float) (rect.width) / (float) (rect.height);

      // 基本上相当于一个矩形,或者圆形
      if (ratio < 1.1 && ratio > 0.9) {
        // drawContours(resultImage, contours, t, Scalar(0, 0, 255), -1, 8,
        // Mat(), 0, Point());
        Imgproc.drawContours(mask, contours, i, new Scalar(255, 255, 255), -1, 8, new Mat(), 0, new Point());
      }
      // }
    }
    Mat result = new Mat();
    src.copyTo(result, mask);
    return result;
  }
  
  public static List<MatOfPoint> extraCircleContours(Mat src, List<MatOfPoint> contours) {
    
    List<MatOfPoint> retval=new ArrayList<>();
    // for (size_t t = 0; t < contours.size(); t++) {
    for (int i = 0; i < contours.size(); i++) {
      // double area = contourArea(contours[t]);
      double area = Imgproc.contourArea(contours.get(i));
      if(area<100) {
        continue;
      }
      // Rect rect = boundingRect(contours[t]);
      Rect rect = Imgproc.boundingRect(contours.get(i));
      // float ratio = float(rect.width) / float(rect.height);
      float ratio = (float) (rect.width) / (float) (rect.height);

      // 基本上相当于一个矩形,或者圆形
      if (ratio < 1.1 && ratio > 0.9) {
        // drawContours(resultImage, contours, t, Scalar(0, 0, 255), -1, 8,
        // Mat(), 0, Point());
        retval.add(contours.get(i));
      }
      // }
    }
    return retval;
  }
  
  public static boolean isCircle(List<MatOfPoint> contours) {
    //判断轮廓是否为正方形或者圆形

    // 判断轮廓是否为圆形
    for (int i = 0; i < contours.size(); i++) {
      // double area = contourArea(contours[t]);
       double area = Imgproc.contourArea(contours.get(i));
       if(area<50) {
         return false;
       }

      // Rect rect = boundingRect(contours[t]);
      Rect rect = Imgproc.boundingRect(contours.get(i));
      // float ratio = float(rect.width) / float(rect.height);
      float ratio = (float) (rect.width) / (float) (rect.height);

      // 基本上相当于一个矩形,或者圆形
      if (ratio < 1.1 && ratio > 0.9) {
        // 返回红色
        return true;
      }
      // }
    }
    return false;
  }
  

}
