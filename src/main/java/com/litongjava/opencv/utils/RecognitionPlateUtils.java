package com.litongjava.opencv.utils;

import java.io.IOException;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import com.litongjava.opencv.model.DebugInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Ping E Lee
 *
 */
@Slf4j
public class RecognitionPlateUtils {

  /**
   * 进行预处理
   * @param src
   * @param debugInfo
   * @return
   * @throws IOException 
   */
  public static Mat pre(Mat src, DebugInfo debugInfo) throws IOException {
    return v1(src, debugInfo);
  }

  private static Mat v1(Mat src, DebugInfo debugInfo) throws IOException {
    // 提取出车牌区域
    src = RecognitionUtils.extraRecogArea(src, debugInfo);
    return threshold(src, debugInfo);
  }

  /**
   * 
   * @param src
   * @param debugInfo
   * @return
   * @throws IOException 
   */
  private static Mat threshold(Mat src, DebugInfo debugInfo) throws IOException {

    String tempPath = debugInfo.getTempPath();
    String extensionName = debugInfo.getExtensionName();
    String baseName = debugInfo.getBaseName();
    Boolean isSave = debugInfo.getIsSave();

    Boolean isUpload = debugInfo.getIsUpload();
    String uploadHost = debugInfo.getUploadHost();

    // 灰度化
    Mat gray = new Mat();
    Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);

    baseName = MatUtils.getBaseName(baseName, "gray");
    String dstPath = MatUtils.getDstPath(tempPath, baseName, extensionName);
    log.info("save file name:{}", baseName);
    MatUtils.debugToFile(isSave, gray, baseName, extensionName, dstPath, isUpload, uploadHost);

    // 二值化
    Mat threshed = new Mat(gray.rows(), gray.cols(), CvType.CV_8UC1);
    int type = Imgproc.THRESH_BINARY;
    double thresh = 192;
    Imgproc.threshold(gray, threshed, thresh, 255, type);

    baseName = MatUtils.getBaseName(baseName, "threshold-" + type + "-" + thresh);
    dstPath = MatUtils.getDstPath(tempPath, baseName, extensionName);
    log.info("save file name:{}", baseName);
    MatUtils.debugToFile(isSave, threshed, baseName, extensionName, dstPath, isUpload, uploadHost);

    debugInfo.setImagePath(dstPath);

    return threshed;
  }
}
