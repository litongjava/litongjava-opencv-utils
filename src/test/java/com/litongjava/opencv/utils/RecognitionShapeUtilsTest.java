package com.litongjava.opencv.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import com.litongjava.opencv.constatns.HsvConstants;
import com.litongjava.opencv.model.DebugInfo;
import com.litongjava.opencv.model.Shape;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RecognitionShapeUtilsTest {

  String tempPath = "D:\\opencv-images\\shape\\temp";

  @Before
  public void before() {
    OpenCVLibraryUtils.init();
  }

  @Test
  public void testRecognizeShape() throws IOException {
    List<String> imagePathList = new ArrayList<>();
    imagePathList.add("D:\\opencv-images\\shape\\shape-001.png");
    imagePathList.add("D:\\opencv-images\\shape\\shape-003.jpg");
    imagePathList.add("D:\\opencv-images\\shape\\shape-004.jpg");
    imagePathList.add("D:\\opencv-images\\shape\\shape-005.jpg");
    imagePathList.add("D:\\opencv-images\\shape\\shape-006.jpg");
    imagePathList.add("D:\\opencv-images\\shape\\shape-007.jpg");
    imagePathList.add("D:\\opencv-images\\shape\\shape-008.jpg");
    imagePathList.add("D:\\opencv-images\\shape\\shape-009.jpg");
    Map<String, String[]> result = new HashMap<>();
    for (String imagePath : imagePathList) {
      DebugInfo debugInfo = new DebugInfo(imagePath, true, tempPath);
      //读取文件
      byte[] imageBytes = FileUtils.readFileToByteArray(new File(imagePath));
      List<Shape> shapList = RecognitionShapeUtils.index(imageBytes, debugInfo);
      String[] formatToArray = RecognitionShapeUtils.formatToArray(shapList);
      result.put(imagePath, formatToArray);
    }
    result.forEach((k, v) -> {
      System.out.println(k + "=" + Arrays.toString(v));
    });

    // 比较结果集
    Map<String, String[]> correctResult = getCorrectResult();

  }

  private Map<String, String[]> getCorrectResult() {
    List<String> imagePathList = new ArrayList<>();
    imagePathList.add("D:\\opencv-images\\shape\\shape-001.png");
    imagePathList.add("D:\\opencv-images\\shape\\shape-002.png");
    imagePathList.add("D:\\opencv-images\\shape\\shape-003.jpg");
    imagePathList.add("D:\\opencv-images\\shape\\shape-004.jpg");
    imagePathList.add("D:\\opencv-images\\shape\\shape-005.jpg");
    imagePathList.add("D:\\opencv-images\\shape\\shape-006.jpg");
    imagePathList.add("D:\\opencv-images\\shape\\shape-007.jpg");
    imagePathList.add("D:\\opencv-images\\shape\\shape-008.jpg");

    return null;
  }

  @Test
  public void testIndex() throws IOException {
//  String imagePath = "D:\\opencv-images\\shape\\shape-night.jpg";
    // String imagePath = "D:\\opencv-images\\shape\\Shape-1639277030908.jpg";
    // String imagePath="D:\\opencv-images\\shape\\Shape.png";
    //String imagePath = "D:\\opencv-images\\shape\\shape-009.jpg";
    //String imagePath = "D:\\opencv-images\\shape\\shape-009.jpg";
    String imagePath="D:\\opencv-images\\2021省赛比赛内容\\20211216150750.jpg";
    DebugInfo debugInfo = new DebugInfo(imagePath, true, tempPath);

    byte[] imageBytes = FileUtils.readFileToByteArray(new File(imagePath));
    List<Shape> shapList = RecognitionShapeUtils.index(imageBytes, debugInfo);
    String[] formatToArray = RecognitionShapeUtils.formatToArray(shapList);
    for (String string : formatToArray) {
      System.out.println(string);
    }

  }

  /**
   * 将图像转为HSV
   * 
   * @throws IOException
   * @throws FileNotFoundException
   */
  @Test
  public void testToHsve() throws FileNotFoundException, IOException {
    String imagePath = "D:\\opencv-images\\shape\\shape-night.jpg";
    String hsvImagePath = "D:\\opencv-images\\shape\\shape-night-hsv.jpg";
    Mat src = MatUtils.imread(imagePath);
    Mat hsv = new Mat();
    Imgproc.cvtColor(src, hsv, Imgproc.COLOR_BGR2HSV);
    MatUtils.imwrite(hsvImagePath, hsv);
  }

  /**
   * 遍历图像并输出
   * 
   * @throws IOException
   * @throws FileNotFoundException
   */
  @Test
  public void getHist() throws FileNotFoundException, IOException {
    // String imagePath =
    // "D:\\opencv-images\\shape\\shape-night\\shape-night-maxArea-hsv-black.jpg";
    // String imagePath =
    // "D:\\opencv-images\\shape\\shape-night\\shape-night-maxArea-hsv-red.jpg";
//    String imagePath = "D:\\opencv-images\\shape\\shape-night\\shape-night-maxArea-hsv-orange.jpg";
//    String imagePath = "D:\\opencv-images\\shape\\shape-night\\shape-night-maxArea-hsv-yellow.jpg";
//    String imagePath = "D:\\opencv-images\\shape\\shape-night\\shape-night-maxArea-hsv-cyan.jpg";
//    String imagePath = "D:\\opencv-images\\shape\\shape-night\\shape-night-maxArea-hsv-blue.jpg";
    String imagePath = "D:\\opencv-images\\shape\\shape-night\\shape-night-maxArea-hsv-purple.jpg";
    Mat src = MatUtils.imread(imagePath, Imgcodecs.IMREAD_GRAYSCALE);

    Mat thresholded = new Mat(src.rows(), src.cols(), CvType.CV_8UC1);
    int type = Imgproc.THRESH_BINARY | Imgproc.THRESH_TRIANGLE;
    log.info("type:{}", type);
    Imgproc.threshold(src, thresholded, 0, 255, type);

    // 再次二值化

    int width = thresholded.rows();
    int height = thresholded.cols();
    log.info("width:{},heigth:{}", width, height);

    System.out.println("开始输出");
    int count = 0;
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        double[] ds = thresholded.get(i, j);
        if (ds[0] != 0d) {
          count++;
        }
      }
    }
    // 红色 两个图形 3585
    // 黑色 2682
    // 不适合的图像输出的458
    // 黄色 28505
    // 绿色 2464
    // 青色 177
    // 蓝色 2464
    // 紫色 3312
    System.out.println(count);

    HighGui.imshow("src", src);
    HighGui.waitKey();
    HighGui.destroyAllWindows();
  }

  /**
   * 提取背景色为白色
   * 
   * @throws IOException
   * @throws FileNotFoundException
   */
  @Test
  public void extraMask() throws FileNotFoundException, IOException {
    String imagePath = "D:\\opencv-images\\shape\\Shape-1639277030908\\Shape-1639277030908-maxArea.jpg";
    String tempPath = "D:\\opencv-images\\shape\\Shape-1639277030908\\Shape-1639277030908-maxArea";
    DebugInfo debugInfo = new DebugInfo(imagePath, true, tempPath);

    Boolean isSave = debugInfo.getIsSave();
    String baseName = debugInfo.getBaseName();
    String extensionName = debugInfo.getExtensionName();

    Boolean isUpload = debugInfo.getIsUpload();
    String uploadHost = debugInfo.getUploadHost();

    Mat src = MatUtils.imread(imagePath);
    // 灰度化
    Mat gray = new Mat();
    Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);

    String grayName = baseName + "-gray";
    String grayDstPath = tempPath + File.separator + grayName + "." + extensionName;
    log.info("save file name:{}", grayName);
    MatUtils.debugToFile(isSave, gray, grayName, extensionName, grayDstPath, isUpload, uploadHost);

    // 均值滤波
    Size ksize = new Size(5, 5);
    Mat blur = new Mat();
    Imgproc.blur(gray, blur, ksize);

    String blurName = MatUtils.getBaseName(grayName, "blur");
    String blurDstPath = MatUtils.getDstPath(tempPath, blurName, extensionName);
    log.info("save file name:{}", blurName);
    MatUtils.debugToFile(isSave, blur, blurName, extensionName, blurDstPath, isUpload, uploadHost);

    // 二值化
    Mat threshed = new Mat(blur.rows(), blur.cols(), CvType.CV_8UC1);
    int type = Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU;
    Imgproc.threshold(blur, threshed, 0, 255, type);

    Mat whithBack = Mat.zeros(src.size(), src.type());
    // java不支持Scalar运算符重载
    // redBack = Scalar(40, 40, 200);
    // 只能遍历了,遍历也设置不了
    // 使用not测试
    Core.bitwise_not(whithBack, whithBack);

    Mat mask = threshed.clone();
    // 反向
    Core.bitwise_not(mask, mask);
    // mask不为0的迁移过来
    Core.copyTo(src, whithBack, mask);

    // image.copyTo(redBack, mask);

    HighGui.imshow("threshed", threshed);
    HighGui.imshow("whithBack", whithBack);
    HighGui.waitKey();
    HighGui.destroyAllWindows();
  }

  /**
   * 检测五角星的中心
   * 
   * @throws IOException
   * @throws FileNotFoundException
   */
  @Test
  public void testCenter() throws FileNotFoundException, IOException {
    String imagePath = "D:\\opencv-images\\shape\\tempp\\shape-001-maxArea-maxArea-hsv-red.png";
    Mat src = MatUtils.imread(imagePath);
    // 灰度,平滑,二值
    Mat gray = new Mat();
    Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
    Mat blur = new Mat();
    Imgproc.blur(gray, blur, new Size(5, 5));
    Mat threshed = new Mat(blur.rows(), blur.cols(), CvType.CV_8UC1);
    int type = Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU;
    Imgproc.threshold(blur, threshed, 0, 255, type);

    // 检测轮廓
    Mat hierarchy = new Mat();
    List<MatOfPoint> contours = new ArrayList<>();
    Imgproc.findContours(threshed, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
    int contourCount = contours.size();
    log.info("轮廓数量:{}", contourCount);
    List<Moments> muList = new ArrayList<Moments>();
    List<Point> mcList = new ArrayList<Point>();
    // 计算图像矩
    for (MatOfPoint contour : contours) {
      Moments moments = Imgproc.moments(contour, false);
      muList.add(moments);
      // 计算中心点
      double x = moments.m10 / moments.m00;
      double y = moments.m01 / moments.m00;
      mcList.add(new Point(x, y));
    }
    // 一个画布
    Mat drawing = Mat.zeros(threshed.size(), CvType.CV_8UC3);
    for (int i = 0; i < contours.size(); i++) {
      // 计算中心点并绘制
      // 绘制轮廓
      Scalar color = new Scalar(0, 0, 255);
      Imgproc.drawContours(drawing, contours, -1, color);
      Imgproc.circle(drawing, mcList.get(i), 4, color, -1, 8, 0);
    }

    HighGui.imshow("drawing", drawing);
    HighGui.waitKey();
    HighGui.destroyAllWindows();
  }

  @Test
  public void testIsContourConvex() throws IOException {
    String imagePath = "D:\\opencv-images\\shape\\tempp\\1639306697133-maxArea-maxArea-hsv-green.jpg";
    DebugInfo debugInfo = new DebugInfo(imagePath, false);
    Mat src = MatUtils.imread(imagePath);
    List<MatOfPoint> contours = ImgprocUtils.findContours(src, debugInfo);
    for (int i = 0; i < contours.size(); i++) {
      boolean contourConvex = Imgproc.isContourConvex(contours.get(i));
      log.info("第i:{}个轮廓,是否为凸形状:{}", i, contourConvex);
    }
  }

  @Test
  public void testConvexity() throws FileNotFoundException, IOException {
    // String imagePath =
    // "D:\\opencv-images\\shape\\tempp\\shape-001-maxArea-maxArea-hsv-red.png";
    // Mat [ 9*1*CV_32SC1, isCont=true, isSubmat=false, nativeObj=0x1122bf0,
    // dataAddr=0x10f1380 ]
    // Mat [ 19*1*CV_32SC1, isCont=true, isSubmat=false, nativeObj=0x11624b0,
    // dataAddr=0x1135180 ]
    String imagePath = "D:\\opencv-images\\shape\\tempp\\1639306697133-maxArea-maxArea-hsv-green.jpg";
    DebugInfo debugInfo = new DebugInfo(imagePath, false);
    Mat src = MatUtils.imread(imagePath);
    List<MatOfPoint> contours = ImgprocUtils.findContours(src, debugInfo);
    int size = contours.size();
    List<MatOfInt> approxList = new ArrayList<>();
    List<MatOfPoint> pointList = new ArrayList<MatOfPoint>();
    for (int i = 0; i < size; i++) {
      MatOfInt approx = new MatOfInt();
      Imgproc.convexHull(contours.get(i), approx);
      System.out.println(approx);
//      int width = approx.cols();
//      int height = approx.rows();
//      for (int j = 0; j < width; j++) {
//        for (int k = 0; k < height; k++) {
//          double[] ds = approx.get(j, k);
//          Imgproc.circle(src, new Point(ds), 4, new Scalar(0, 0, 255), -1, 8, 0);
//        }
//      }
      // MatOfPoint matOfPoint = new MatOfPoint();
      // convertIndexesToPoints(matOfPoint, approx);
      // pointList.add(matOfPoint);
    }

    Imgproc.drawContours(src, pointList, 0, new Scalar(0, 0, 255));

    HighGui.imshow("src", src);
    HighGui.waitKey();
    HighGui.destroyAllWindows();
  }

  @Test
  public void convexityDefects() throws FileNotFoundException, IOException {
    String imagePath = "D:\\opencv-images\\shape\\tempp\\shape-001-maxArea-maxArea-hsv-red.png";
    DebugInfo debugInfo = new DebugInfo(imagePath, false);
    Mat src = MatUtils.imread(imagePath);
    List<MatOfPoint> contours = ImgprocUtils.findContours(src, debugInfo);
    int size = contours.size();
    for (int i = 0; i < size; i++) {
      MatOfPoint contour = contours.get(i);
      MatOfInt approx = new MatOfInt();
      Imgproc.convexHull(contour, approx);
      MatOfInt4 convexityDefects = new MatOfInt4();
      Imgproc.convexityDefects(contour, approx, convexityDefects);
      System.out.println(convexityDefects);
    }
    HighGui.imshow("src", src);
    HighGui.waitKey();
    HighGui.destroyAllWindows();
  }

  public static MatOfPoint convertIndexesToPoints(MatOfPoint contour, MatOfInt indexes) {
    int[] arrIndex = indexes.toArray();
    Point[] arrContour = contour.toArray();
    Point[] arrPoints = new Point[arrIndex.length];

    for (int i = 0; i < arrIndex.length; i++) {
      arrPoints[i] = arrContour[arrIndex[i]];
    }

    MatOfPoint hull = new MatOfPoint();
    hull.fromArray(arrPoints);
    return hull;
  }

  @Test
  public void getWhiteSize() throws FileNotFoundException, IOException {
    List<String> imagePathList = new ArrayList<>();
    imagePathList.add("D:\\opencv-images\\shape\\temp\\shape-001-maxArea-maxArea.png"); //0.11
    imagePathList.add("D:\\opencv-images\\shape\\temp\\shape-002-maxArea.png"); //0.46
    imagePathList.add("D:\\opencv-images\\shape\\temp\\shape-003-maxArea.jpg"); //0.0
    imagePathList.add("D:\\opencv-images\\shape\\temp\\shape-004-maxArea.jpg"); //0.0
    imagePathList.add("D:\\opencv-images\\shape\\temp\\shape-005-maxArea-maxArea.jpg");//0.01
    imagePathList.add("D:\\opencv-images\\shape\\temp\\shape-006-maxArea-maxArea.jpg");//0.0
    imagePathList.add("D:\\opencv-images\\shape\\temp\\shape-007-maxArea-maxArea.jpg");//1.0
    imagePathList.add("D:\\opencv-images\\shape\\temp\\shape-008-maxArea-maxArea.jpg");//0.0
    
    for (String imagePath : imagePathList) {
      Mat src = MatUtils.imread(imagePath);
      Mat temphsv = new Mat();
      Imgproc.cvtColor(src, temphsv, Imgproc.COLOR_BGR2HSV);
      // 提取hsv中的白色
      Mat colorDivision = ColorDivisionUtils.colorDivision(temphsv, HsvConstants.lower_white,
          HsvConstants.upper_white);

      Mat thresholded = new Mat(colorDivision.rows(), colorDivision.cols(), CvType.CV_8UC1);
      int type = Imgproc.THRESH_BINARY | Imgproc.THRESH_TRIANGLE;
//      log.info("type:{}", type);
      Imgproc.threshold(colorDivision, thresholded, 0, 255, type);
      
      //HighGui.imshow("colorDivision", colorDivision);
      HighGui.imshow("src", src);
      //HighGui.imshow("temphsv", temphsv);
      HighGui.imshow("thresholded", thresholded);
      int count = Core.countNonZero(thresholded);
      int total = thresholded.rows() * thresholded.cols();

      // 计算白色点在图形中占据的比值
      double persent = new BigDecimal((float) count / total).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
      System.out.println(persent);
    }
  
    HighGui.waitKey();
    HighGui.destroyAllWindows();

  }
}
