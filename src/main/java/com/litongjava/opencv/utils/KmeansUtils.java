package com.litongjava.opencv.utils;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;

import lombok.extern.slf4j.Slf4j;

/**
 * 使用 kmeas聚类获取背景色并将背景色设置为白色
 * 
 * @author
 *
 */
@Slf4j
public class KmeansUtils {

  public static Mat backgrand2White(Mat src) {
    // 将二维图像数据线性化
    Mat data = new Mat();
    for (int i = 0; i < src.rows(); i++) {// 像素点线性排列
      for (int j = 0; j < src.cols(); j++) {
        // Vec3b point = src.at<Vec3b>(i, j);
        // 包含是三个点的数据[99.0, 97.0, 97.0]
        double[] ds = src.get(i, j);
        // System.out.println(Arrays.toString(ds));
        // Mat tmp = (Mat_<float>(1, 3) << point[0], point[1], point[2]);
        // 通过C++代码调试发现,type的值是5
        Mat tmp = new Mat(1, 3, CvType.CV_32F);
        tmp.put(0, 0, ds);
        data.push_back(tmp);
      }
    }

    // 使用K-means聚类
    int numCluster = 4;
    Mat labels = new Mat();
    TermCriteria criteria = new TermCriteria(TermCriteria.EPS + TermCriteria.COUNT, 10, 0.1);
    // Imgproc.KMEANS_PP_CENTERS
    // CyType.KMEANS_PP_CENTERS
    Core.kmeans(data, numCluster, labels, criteria, 4, Core.KMEANS_PP_CENTERS);

    int rows = labels.rows(); // ==>43840
    int cols = labels.cols(); // ==>1
    log.info("rows:{},cols:{}", rows, cols);
    // 遍历数据
//    for (int i = 0; i < rows; i++) {
//      for (int j = 0; j < cols; j++) {
//        double[] ds = labels.get(i, j);
//        System.out.println(Arrays.toString(ds));==>[1.0]
//      }
//    }

    // 背景与图像二值化
    Mat mask = Mat.zeros(src.size(), CvType.CV_8UC1);
    int index = src.rows() * 5 + 5; // 获取点（5，5）作为背景色
//    int cindex = labels.at<int>(index);
    double[] cindex = labels.get(index, 0);
//    log.info("ds:{}",ds);
    /*
     * 提取背景特征
     */
    for (int row = 0; row < src.rows(); row++) {
      for (int col = 0; col < src.cols(); col++) {
        index = row * src.cols() + col;
        // int label = labels.at<int>(index);
        double[] label = labels.get(index, 0);
        if (label[0] == cindex[0]) { // 背景
          // mask.at<uchar>(row, col) = 0;
          mask.put(row, col, 0);
        } else {
          mask.put(row, col, 255);
        }
      }
    }
    // 腐蚀 + 高斯模糊：图像与背景交汇处高斯模糊化
    Mat k = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3), new Point(-1, -1));
    Imgproc.erode(mask, mask, k);
    Imgproc.GaussianBlur(mask, mask, new Size(3, 3), 0, 0);

    // 更换背景色以及交汇处融合处理
//    RNG rng(12345);
//    Vec3b color;  //设置的背景色
    double[] color = { 255, 255, 255 };
//    color[0] = 255;// rng.uniform(0, 255);
//    color[1] = 255;// rng.uniform(0, 255);
//    color[2] = 255;// rng.uniform(0, 255);
    Mat result = new Mat(src.size(), src.type());

    double w = 0.0; // 融合权重
    double b = 0, g = 0, r = 0;
    double b1 = 0, g1 = 0, r1 = 0;
    double b2 = 0, g2 = 0, r2 = 0;
//
    for (int row = 0; row < src.rows(); row++) {
      for (int col = 0; col < src.cols(); col++) {
        // int m = mask.at<uchar>(row, col);
        double[] m = mask.get(row, col);
        if (m[0] == 255) {
          // result.at<Vec3b>(row, col) = src.at<Vec3b>(row, col); // 前景
          result.put(row, col, src.get(row, col));
        } else if (m[0] == 0) {
          // result.at<Vec3b>(row, col) = color; // 背景
          result.put(row, col, color);
        } else {/* 融合处理部分 */
          w = m[0] / 255.0;
//          b1 = src.at<Vec3b>(row, col)[0];
//          g1 = src.at<Vec3b>(row, col)[1];
//          r1 = src.at<Vec3b>(row, col)[2];
          b1 = src.get(row, col)[0];

          b2 = color[0];
          g2 = color[1];
          r2 = color[2];

          b = b1 * w + b2 * (1.0 - w);
          g = g1 * w + g2 * (1.0 - w);
          r = r1 * w + r2 * (1.0 - w);

//          result.at<Vec3b>(row, col)[0] = b;
//          result.at<Vec3b>(row, col)[1] = g;
//          result.at<Vec3b>(row, col)[2] = r;
//           result.at<Vec3b>(row, col)[0]
          result.put(row, col, b, g, r);
        }
      }
    }
    return result;
  }
}
