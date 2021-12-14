package com.litongjava.opencv.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KmeansUtilsTest {

  @Before
  public void before() {
    OpenCVLibraryUtils.init();
  }

  @Test
  public void testGetBackgroundColor() throws FileNotFoundException, IOException {
    List<String> imagePathList = new ArrayList<>();
    imagePathList.add("D:\\opencv-images\\shape\\temp\\shape-001-maxArea-maxArea.png"); //0.0
    imagePathList.add("D:\\opencv-images\\shape\\temp\\shape-002-maxArea.png"); //0.0
    imagePathList.add("D:\\opencv-images\\shape\\temp\\shape-003-maxArea.jpg"); //1.0
    imagePathList.add("D:\\opencv-images\\shape\\temp\\shape-004-maxArea.jpg"); //0.0
    imagePathList.add("D:\\opencv-images\\shape\\temp\\shape-005-maxArea-maxArea.jpg");//0.0
    imagePathList.add("D:\\opencv-images\\shape\\temp\\shape-006-maxArea-maxArea.jpg");//0.0
    imagePathList.add("D:\\opencv-images\\shape\\temp\\shape-007-maxArea-maxArea.jpg");//1.0
    imagePathList.add("D:\\opencv-images\\shape\\temp\\shape-008-maxArea-maxArea.jpg");//0.0

    for (String imagePath : imagePathList) {
      Mat src = MatUtils.imread(imagePath);
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
//        for (int i = 0; i < rows; i++) {
//          for (int j = 0; j < cols; j++) {
//            double[] ds = labels.get(i, j);
//            System.out.println(Arrays.toString(ds));==>[1.0]
//          }
//        }

      // 背景与图像二值化
      // Mat mask = Mat.zeros(src.size(), CvType.CV_8UC1);
      int index = src.rows() * 5 + 5; // 获取点（5，5）作为背景色
      double[] cindex = labels.get(index, 0);
      System.out.println(imagePath + "=" + cindex[0]);
    }
  }

}
