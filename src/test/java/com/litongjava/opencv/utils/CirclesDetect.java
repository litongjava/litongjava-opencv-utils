package com.litongjava.opencv.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Ping E Lee
 *
 */
@Slf4j
public class CirclesDetect {

  @Before
  public void before() {
    OpenCVLibraryUtils.init();
  }

  @Test
  public void test() throws FileNotFoundException, IOException {
    // 读取图片
    String srcPath = "D:\\opencv-images\\traffic\\temp\\green-2-black.jpg";
    Mat src = MatUtils.imread(srcPath);

    // 修改为小图片
    int width = src.cols();
    int height = src.rows();

    Size dsize = new Size(width / 4, height / 4);
    Imgproc.resize(src, src, dsize);

    // 灰度化
    Mat gray = new Mat();
    Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
    HighGui.imshow("gray", gray);

    // 二值化
    // threshold(src, binary, 0, 255, THRESH_BINARY | THRESH_OTSU);
    // imshow("binary image", binary);
    Mat binary = new Mat(gray.rows(), gray.cols(), CvType.CV_8UC1);
    Imgproc.threshold(gray, binary, 0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
    HighGui.imshow("binary", binary);

    // 形态学变化
    // Mat kernel = getStructuringElement(MORPH_RECT, Size(3, 3), Point(-1,
    // -1));
    // morphologyEx(binary, dst, MORPH_CLOSE, kernel, Point(-1, -1));
    // imshow("close image", dst);
    Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3), new Point(-1, -1));
    Mat dst = new Mat();
    Imgproc.morphologyEx(binary, dst, Imgproc.MORPH_CLOSE, kernel, new Point(-1, -1));
    HighGui.imshow("dst", dst);

    // 查找轮廓
    // vector<vector<Point>> contours;
    // vector<Vec4i> hireachy;
    // findContours(dst, contours, hireachy, RETR_TREE, CHAIN_APPROX_SIMPLE,
    // Point());
    Mat hierarchy = new Mat();
    List<MatOfPoint> contours = new ArrayList<>();
    Imgproc.findContours(dst, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point());

    // 创建一个空白的图像
    Mat resultImage = Mat.zeros(src.size(), CvType.CV_8UC3);
    // for (size_t t = 0; t < contours.size(); t++) {
    for (int i = 0; i < contours.size(); i++) {
      // double area = contourArea(contours[t]);
      double area = Imgproc.contourArea(contours.get(i));
      if (area < 100)
        continue;

      // Rect rect = boundingRect(contours[t]);
      Rect rect = Imgproc.boundingRect(contours.get(i));
      // float ratio = float(rect.width) / float(rect.height);
      float ratio = (float) (rect.width) / (float) (rect.height);

      // 基本上相当于一个矩形
      if (ratio < 1.1 && ratio > 0.9) {
        // drawContours(resultImage, contours, t, Scalar(0, 0, 255), -1, 8,
        // Mat(), 0, Point());
        //画出轮廓
        Imgproc.drawContours(resultImage, contours, i, new Scalar(255, 255, 255), -1, 8, new Mat(), 0, new Point());
        // printf("circle area : %f\n", area);
        log.info("circle area:{}", area);

        // printf("circle length : %f\n", arcLength(contours[t], true));
        MatOfPoint2f matOfPoint2f = new MatOfPoint2f();
        MatOfPoint contour = contours.get(i);
        contour.convertTo(matOfPoint2f, CvType.CV_32F);
        log.info("circle length : {}", Imgproc.arcLength(matOfPoint2f, true));
        
        int x = rect.x + rect.width / 2;
        int y = rect.y + rect.height / 2;
        Point cc = new Point(x, y);
        Imgproc.circle(resultImage, cc, 2, new Scalar(0, 0, 255), 2, 8, 0);
      }
      // }
    }
    HighGui.imshow("resultImage", resultImage);
    HighGui.waitKey();
  }
}
