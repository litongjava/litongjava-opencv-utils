package com.litongjava.opencv.utils;

import java.io.IOException;

import org.opencv.core.Mat;

import com.litongjava.opencv.model.DebugInfo;
import com.litongjava.opencv.model.MaxAreaBo;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Ping E Lee
 *
 */
@Slf4j
public class RecognitionUtils {
  
  /**
   * 获取识别区域
   * @param src
   * @param debugInfo
   * @return
   * @throws IOException
   */
  public static Mat extraRecogArea(Mat src, DebugInfo debugInfo) throws IOException {
    String tempPath = debugInfo.getTempPath();
    String extensionName = debugInfo.getExtensionName();
    String baseName = debugInfo.getBaseName();
    Boolean isSave = debugInfo.getIsSave();

    Boolean isUpload = debugInfo.getIsUpload();
    String uploadHost = debugInfo.getUploadHost();
    // 经过两次裁剪,裁剪出包含图片的边框
    // 裁剪取出最大面积的图像
    MaxAreaBo maxAreaBo = ImgprocUtils.getMaxArea(src, debugInfo);
    double lastAreaMax = maxAreaBo.getAreaMax();
    log.info("areaMax:{},rectMax:{}", lastAreaMax, maxAreaBo.getRectMax());
    
    // 裁剪图像
    Mat lastRectMax = src.submat(maxAreaBo.getRectMax());
    // 保存图片
    String maxAreaName = MatUtils.getBaseName(baseName, "maxArea");
    String maxAreaDstPath = MatUtils.getDstPath(tempPath, maxAreaName, extensionName);
    log.info("save file name:{}", maxAreaName);
    MatUtils.debugToFile(isSave, lastRectMax, maxAreaName, extensionName, maxAreaDstPath, isUpload, uploadHost);

    /**
     * 判断是否进行二次裁剪,查找面积最大的图形,如果图形面积大于指定值,则还有一层边框,需要进行二次裁剪,如果没有则不需要进行多次裁剪
     * 经过多次试验,判断0.3是合适的值
     */

    debugInfo.setImagePath(maxAreaDstPath);
    // 再次提取最大面积的轮廓
    maxAreaBo = ImgprocUtils.getMaxArea(lastRectMax, debugInfo);
    log.info("areaMax:{},rectMax:{}", maxAreaBo.getAreaMax(), maxAreaBo.getRectMax());
    double ratio = maxAreaBo.getAreaMax() / lastAreaMax;
    log.info("ratio:{}", ratio);
    if (ratio > 0.30) {
      lastRectMax = lastRectMax.submat(maxAreaBo.getRectMax());
      maxAreaName = MatUtils.getBaseName(maxAreaName, "maxArea");
      maxAreaDstPath = MatUtils.getDstPath(tempPath, maxAreaName, extensionName);
      log.info("save file name:{}", maxAreaName);
      MatUtils.debugToFile(isSave, lastRectMax, maxAreaName, extensionName, maxAreaDstPath, isUpload, uploadHost);
      debugInfo.setImagePath(maxAreaDstPath);

      // 因为递归效率太低,判断是否需要再次进行裁剪
      maxAreaBo = ImgprocUtils.getMaxArea(lastRectMax, debugInfo);
      log.info("areaMax:{},rectMax:{}", maxAreaBo.getAreaMax(), maxAreaBo.getRectMax());
      ratio = maxAreaBo.getAreaMax() / lastAreaMax;
      log.info("ratio:{}", ratio);
      if (ratio > 0.30) {
        lastRectMax = lastRectMax.submat(maxAreaBo.getRectMax());
        maxAreaName = MatUtils.getBaseName(maxAreaName, "maxArea");
        maxAreaDstPath = MatUtils.getDstPath(tempPath, maxAreaName, extensionName);
        log.info("save file name:{}", maxAreaName);
        MatUtils.debugToFile(isSave, lastRectMax, maxAreaName, extensionName, maxAreaDstPath, isUpload, uploadHost);
        debugInfo.setImagePath(maxAreaDstPath);

        maxAreaBo = ImgprocUtils.getMaxArea(lastRectMax, debugInfo);
        log.info("areaMax:{},rectMax:{}", maxAreaBo.getAreaMax(), maxAreaBo.getRectMax());
        ratio = maxAreaBo.getAreaMax() / lastAreaMax;
        log.info("ratio:{}", ratio);
        if (ratio > 0.30) {
          lastRectMax = lastRectMax.submat(maxAreaBo.getRectMax());
          maxAreaName = MatUtils.getBaseName(maxAreaName, "maxArea");
          maxAreaDstPath = MatUtils.getDstPath(tempPath, maxAreaName, extensionName);
          log.info("save file name:{}", maxAreaName);
          MatUtils.debugToFile(isSave, lastRectMax, maxAreaName, extensionName, maxAreaDstPath, isUpload, uploadHost);
          debugInfo.setImagePath(maxAreaDstPath);
        }
      }
    }
    return lastRectMax;
  }


}
