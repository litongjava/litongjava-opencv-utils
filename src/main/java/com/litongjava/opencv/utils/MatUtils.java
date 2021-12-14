package com.litongjava.opencv.utils;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MatUtils {

  public static Mat imread(String imagePath) throws FileNotFoundException, IOException {
    return imread(imagePath, Imgcodecs.IMREAD_COLOR);
  }

  public static Mat imread(String imagePath, int flags) throws FileNotFoundException, IOException {
    try (FileInputStream fileInputStream = new FileInputStream(imagePath)) {
      // 1.将文件转为bytes
      byte[] byteArray = IOUtils.toByteArray(fileInputStream);
      return imRead(byteArray, flags);
    }

  }

  public static Mat imread(byte[] imageBytes) {
    return imRead(imageBytes, Imgcodecs.IMREAD_COLOR);
  }

  public static Mat imRead(byte[] byteArray, int flags) {
    // 2.将bytes转为MatOfByte
    MatOfByte matOfByte = new MatOfByte(byteArray);
    // 3.将MatOfByte解码为Mat
    Mat mat = Imgcodecs.imdecode(matOfByte, flags);
    return mat;
  }

  /**
   * 1.先将 mat 写入 MatOfByte 2.再将 MatOfByte 3.转换 byte[] 再将 byte[] 写入文件
   * 
   * @param dstPath
   * @param dst
   * @param extensionName
   * @throws IOException
   */
  public static void imwrite(String dstPath, Mat dst, String extensionName) throws IOException {
    MatOfByte mob = new MatOfByte();
    Imgcodecs.imencode("." + extensionName, dst, mob);
    byte[] imageByte = mob.toArray();
    FileUtils.writeByteArrayToFile(new File(dstPath), imageByte);
  }

  public static void imwrite(String dstPath, Mat dst) throws IOException {
    String extensionName = FilenameUtils.getExtension(dstPath);
    imwrite(dstPath, dst, extensionName);
  }

  /**
   * 转换图像
   */
  public static BufferedImage mat2BufferImage(Mat mat) {
    // 获取宽,高,图片类型
    int width = mat.cols();
    int height = mat.rows();
    int type = mat.channels() == 1 ? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_3BYTE_BGR;

    int dataSize = width * height * (int) mat.elemSize();
    byte[] data = new byte[dataSize];
    mat.get(0, 0, data);

    if (type == BufferedImage.TYPE_3BYTE_BGR) {
      for (int i = 0; i < dataSize; i += 3) {
        byte blue = data[i + 0];
        data[i + 0] = data[i + 2];
        data[i + 2] = blue;
      }
    }
    // 创建bufferedImage并将数据写入BufferedImage
    BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
    WritableRaster raster = image.getRaster();
    raster.setDataElements(0, 0, width, height, data);
    return image;
  }

  public static void debugToFile(Mat mat, String imagePath, String oldName, String newName) throws IOException {
    log.info("wirte to file:{}", newName);
    String dstPath = imagePath.replace(oldName, newName);
    MatUtils.imwrite(dstPath, mat);
  }

  /**
   * 保存存mat文件到目录
   * 
   * @param hsv
   * @param tempPath
   * @param saveName
   * @throws IOException
   */
  public static void debugToFileToFolder(Mat mat, String tempPath, String saveName) throws IOException {
    log.info("save:{}", saveName);
    String dstPath = tempPath + File.separatorChar + saveName;
    MatUtils.imwrite(dstPath, mat);
  }

  /**
   * 
   * @param gray
   * @param grayDstPath
   * @throws IOException
   */
  public static void debugToFile(Mat mat, String dstPath, String baseName) throws IOException {
    log.info("save:{}", baseName);
    MatUtils.imwrite(dstPath, mat);
  }

  public static String getDstPath(String tempPath, String baseName, String extensionName) {
    return tempPath + File.separator + baseName + "." + extensionName;
  }

  public static String getBaseName(String baseName, String suffix) {
    return baseName + "-" + suffix;
  }

  public static void debugToFile(Boolean isSave, Mat mat, String name, String extensionName, String savePath,
      Boolean isUpload, String uploadHost) throws IOException {
    if (isSave) {
      // 保存文件
      imwrite(savePath, mat, extensionName);
    }
    if (isUpload) {
      MatOfByte mob = new MatOfByte();
      Imgcodecs.imencode("." + extensionName, mat, mob);
      byte[] imageByte = mob.toArray();
      log.info("upload file name:{}", name);
      upload(imageByte, extensionName, uploadHost);
    }
  }

  public static String upload( byte[] imageByte,String extensionName, String uploadHost) {
    String requestURL = "http://" + uploadHost + "/upload/folder";
    long currentTimeMillis = System.currentTimeMillis();
    log.info("upload filename:{}",currentTimeMillis);
    return FileUploadUtils.upload(imageByte, requestURL, "file", currentTimeMillis + "." + extensionName);
  }

public static int bSums(Mat src)
{
  //Imgproc.ite
  //HighGui.it
  
  int counter = 0;
//  //迭代器访问像素点
//  Mat_<uchar>::iterator it = src.begin<uchar>();
//  Mat_<uchar>::iterator itend = src.end<uchar>();  
//  for (; it!=itend; ++it)
//  {
//    if((*it)>0) counter+=1;//二值化后，像素点是0或者255
//  }     
  return counter;
}

}
