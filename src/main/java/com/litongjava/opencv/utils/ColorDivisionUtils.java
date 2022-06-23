package com.litongjava.opencv.utils;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import com.litongjava.opencv.constatns.HsvConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Ping E Lee
 *
 */
@Slf4j
public class ColorDivisionUtils {

  /**
   * 顔色分割
   * 
   * @param color
   * @param hsv
   * @param lowerb
   * @param upperb
   * @return
   */
  public static Mat colorDivision(Mat hsv, Scalar lowerb, Scalar upperb) {
    Mat mask = new Mat();
    Core.inRange(hsv, lowerb, upperb, mask);
    if (lowerb.equals(HsvConstants.lower_red)) {
      log.info("检测红色,再次进行检测");
      Mat mask2 = new Mat();
      Core.inRange(hsv, HsvConstants.lower_red_2, HsvConstants.upper_red_2, mask2);
      Core.add(mask, mask2, mask);
    }
    return mask;
  }
}
