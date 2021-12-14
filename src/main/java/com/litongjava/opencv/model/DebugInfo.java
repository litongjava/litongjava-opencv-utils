package com.litongjava.opencv.model;

import org.apache.commons.io.FilenameUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 相关调信息
 * @author Ping
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DebugInfo {
  private String imagePath="";
  private String filename="";
  private String baseName="";
  private String extensionName="";
  private String tempPath="";
  private Boolean isSave = false;
  private Boolean isUpload=false;
  private String uploadHost="";

  public DebugInfo(String imagePath, boolean isSave) {
    this.setImagePath(imagePath);
    this.isSave = isSave;
  }

  public DebugInfo(String imagePath, boolean debug, String tempPath) {
    this.setImagePath(imagePath);
    this.tempPath = tempPath;
    this.isSave = debug;
  }

  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
    // 计算各个属性
    this.filename = FilenameUtils.getName(imagePath);
    this.baseName = FilenameUtils.getBaseName(filename);
    this.extensionName = FilenameUtils.getExtension(filename);

  }
}
