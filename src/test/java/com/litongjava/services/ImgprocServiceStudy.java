package com.litongjava.services;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.litongjava.opencv.utils.MatUtils;
import com.litongjava.opencv.utils.OpenCVLibraryUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImgprocServiceStudy {
  
  @Before
  public void before() {
    OpenCVLibraryUtils.init();
  }

  @Test
  public void test() throws FileNotFoundException, IOException {
    //String imagePath = "D:\\opencv-images\\shape\\temp\\1638104164474-max_area-max_area-hsv-black-threshold-0-255-16.png";
    //String imagePath="D:\\opencv-images\\shape\\temp\\1638104164474-max_area-max_area-hsv-blue.png";
    String imagePath="D:\\opencv-images\\shape\\temp\\1638104164474-max_area-max_area-hsv-green-threshold-0-255-16.png";
    Mat mat = MatUtils.imread(imagePath, Imgcodecs.IMREAD_GRAYSCALE);
    Rect rect = Imgproc.boundingRect(mat);
    log.info("Rect:{}",rect);
    Scalar color = new Scalar(0, 0, 255);
    //灰度图是不可能显示颜色,
    Imgproc.rectangle(mat, rect, color);
    HighGui.imshow("rect", mat);
    //截取出图片
    Mat submat = mat.submat(rect);
    HighGui.imshow("submat", submat);
    //将矩形框的区域截取出来
    HighGui.waitKey();
    HighGui.destroyAllWindows();
  }

}
