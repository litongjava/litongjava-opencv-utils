package com.litongjava.opencv.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import com.litongjava.opencv.constatns.HsvConstants;
import com.litongjava.opencv.model.DebugInfo;
import com.litongjava.opencv.model.MaxAreaBo;
import com.litongjava.opencv.model.Shape;
import com.litongjava.opencv.model.ShapeShape;
import com.litongjava.opencv.model.ShapeType;

import lombok.extern.slf4j.Slf4j;

/**
 * 识别形状
 * 
 * @author Ping
 *
 */
@Slf4j
public class RecognitionShapeUtils {

  public static List<Shape> index(byte[] imageBytes, DebugInfo debugInfo) throws FileNotFoundException, IOException {
    if (debugInfo == null) {
      debugInfo = new DebugInfo();
    }
    if (debugInfo.getTempPath() == null || debugInfo.getTempPath().equals("")) {
      String tempPath = "temp";
      debugInfo.setTempPath(tempPath);
    }

    if (debugInfo.getIsSave()) {
      File file = new File(debugInfo.getTempPath());
      if (!file.exists()) {
        file.mkdirs();
      }
      log.info("tempPath:{}", file.getAbsolutePath());
    }
    return recgnizeV1(imageBytes, debugInfo);
  }

  /**
   * 第一个版本的图像识别
   * 
   * @param imageBytes
   * 
   * @param recognize
   * @return
   * @throws IOException
   * @throws FileNotFoundException
   */
  public static List<Shape> recgnizeV1(byte[] imageBytes, DebugInfo debugInfo)
      throws FileNotFoundException, IOException {
    String tempPath = debugInfo.getTempPath();
    String extensionName = debugInfo.getExtensionName();
    String baseName = debugInfo.getBaseName();
    Boolean isSave = debugInfo.getIsSave();

    Boolean isUpload = debugInfo.getIsUpload();
    String uploadHost = debugInfo.getUploadHost();

    // 读取文件
    Mat src = MatUtils.imread(imageBytes);

    // 经过两次裁剪,裁剪出包含图片的边框
    // 裁剪取出最大面积的图像
    MaxAreaBo maxAreaBo = ImgprocUtils.getMaxArea(src, debugInfo);
    log.info("areaMax:{},rectMax:{}", maxAreaBo.getAreaMax(), maxAreaBo.getRectMax());
    double lastAreaMax = maxAreaBo.getAreaMax();
    // 裁剪图像
    Mat lastRectMax = src.submat(maxAreaBo.getRectMax());
    // 保存图片
    String maxAreaName = MatUtils.getBaseName(baseName, "maxArea");
    String maxAreaDstPath = MatUtils.getDstPath(tempPath, maxAreaName, extensionName);
    log.info("save file name:{}", maxAreaName);
    MatUtils.debugToFile(isSave, lastRectMax, maxAreaName, extensionName, maxAreaDstPath, isUpload, uploadHost);

    /**
     * 判断是否进行二次裁剪,查找面积最大的图形,如果图形面积大于指定值,则表名还有一层边框,需要进行二次裁剪,如果没有则不需要进行,多次裁剪
     * 经过多次试验,判断0.3是一个比较不错的值
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

    // 将背景色设置为白色
//    Mat whiteBackMat = KmeansUtils.backgrand2Wite(extraMaxArea);
//    String whiteBackMatName = MatUtils.getBaseName(maxAreaName, "whiteBack");
//    String whiteBackMateDstPath = MatUtils.getDstPath(tempPath, whiteBackMatName, extensionName);
//    log.info("save file name:{}", whiteBackMatName);
//    MatUtils.debugToFile(isSave, whiteBackMat, whiteBackMatName, extensionName, whiteBackMateDstPath, isUpload,
//        uploadHost);
//    debugInfo.setImagePath(whiteBackMateDstPath);
    // 设置背景色为白色,对白色图片处理效果不好,改用其他方案

//    Mat usmMat = ImgprocUtils.usm(lastRectMax);
//    String usmMatName = MatUtils.getBaseName(maxAreaName, "usm");
//    String usmMateDstPath = MatUtils.getDstPath(tempPath, usmMatName, extensionName);
//    log.info("save file name:{}", usmMateDstPath);
//    MatUtils.debugToFile(isSave, usmMat, usmMatName, extensionName, usmMateDstPath, isUpload, uploadHost);
//    debugInfo.setImagePath(usmMateDstPath);

    return recognizeShape(lastRectMax, debugInfo);
//    return null;
  }

  /**
   * 
   * @param src
   * @param debugInfo
   * @param tempPath  临时文件保存目录
   * @throws IOException
   */
  public static List<Shape> recognizeShape(Mat src, DebugInfo debugInfo) throws IOException {
    Boolean isSave = debugInfo.getIsSave();
    String baseName = debugInfo.getBaseName();
    String extensionName = debugInfo.getExtensionName();
    String tempPath = debugInfo.getTempPath();

    Boolean isUpload = debugInfo.getIsUpload();
    String uploadHost = debugInfo.getUploadHost();

    // 判断白色图像的面积,如果大于某个值这说明是白底图像,不进行背景替换,否则将背景替换成白色
    double persent = ShapeUtils.getWhiteRatio(src);
    if (persent < 0.10) {
      // 将背景替换成白色
      Mat backgrand2Wite = KmeansUtils.backgrand2White(src);
      // 保存到文件
      String whiteName = MatUtils.getBaseName(baseName, "white");
      String whiteDstPath = MatUtils.getDstPath(tempPath, whiteName, extensionName);
      log.info("save file name:{}", whiteDstPath);
      MatUtils.debugToFile(isSave, backgrand2Wite, whiteName, extensionName, whiteDstPath, isUpload, uploadHost);
      src=backgrand2Wite.clone();
    }
    
    // 进行图像增强
    Mat usm = ImgprocUtils.usm(src);
    String usmName = MatUtils.getBaseName(baseName, "usm");
    String usmDstPath = MatUtils.getDstPath(tempPath, usmName, extensionName);
    log.info("save file name:{}", usmName);
    MatUtils.debugToFile(isSave, usm, usmName, extensionName, usmDstPath, isUpload, uploadHost);
    
    src = usm.clone();
    

//    Mat bilatera = new Mat();
    // 高斯双边滤波
//    Imgproc.bilateralFilter(src, bilatera, 0, 100, 10);
//    String bilateraName = MatUtils.getBaseName(baseName, "bilatera");
//    String bilateraDstPath = MatUtils.getDstPath(tempPath, bilateraName, extensionName);
//    log.info("save file name:{}", bilateraName);
//    MatUtils.debugToFile(isSave, bilatera, bilateraName, extensionName, bilateraDstPath, isUpload, uploadHost);

    // 高斯滤波
//    Mat gaussianBlur = new Mat();
//    Imgproc.GaussianBlur(usm, gaussianBlur, new Size(3, 3), 3);
//    String gaussianBlurName = MatUtils.getBaseName(baseName, "gaussianBlur");
//    String gaussianBlureDstPath = MatUtils.getDstPath(tempPath, gaussianBlurName, extensionName);
//    log.info("save file name:{}", gaussianBlurName);
//    MatUtils.debugToFile(isSave, gaussianBlur, gaussianBlurName, extensionName, gaussianBlureDstPath, isUpload,
//        uploadHost);

    // 转成hsv
    Mat hsv = new Mat();
    Imgproc.cvtColor(src, hsv, Imgproc.COLOR_BGR2HSV);
    String hsvFilename = MatUtils.getBaseName(baseName, "hsv");
    String hsvDstPath = MatUtils.getDstPath(tempPath, hsvFilename, extensionName);
    log.info("save file name:{}", hsvFilename);
    MatUtils.debugToFile(isSave, hsv, hsvFilename, extensionName, hsvDstPath, isUpload, uploadHost);

    // 每次分割一种颜色,并且查找轮廓
    debugInfo.setImagePath(hsvDstPath);
    List<Shape> blackShape = colorDivisionAndFindShape(hsv, "黑色", "black", HsvConstants.lower_black,
        HsvConstants.upper_black, debugInfo);

    List<Shape> redShape = colorDivisionAndFindShape(hsv, "红色", "red", HsvConstants.lower_red, HsvConstants.upper_red,
        debugInfo);

    List<Shape> orangeShape = colorDivisionAndFindShape(hsv, "橙色", "orange", HsvConstants.lower_orange,
        HsvConstants.upper_orange, debugInfo);

    List<Shape> yellowShape = colorDivisionAndFindShape(hsv, "黄色", "yellow", HsvConstants.lower_yellow,
        HsvConstants.upper_yellow, debugInfo);

    List<Shape> greenShape = colorDivisionAndFindShape(hsv, "绿色", "green", HsvConstants.lower_green,
        HsvConstants.upper_green, debugInfo);
    List<Shape> cyanShape = colorDivisionAndFindShape(hsv, "青色", "cyan", HsvConstants.lower_cyan,
        HsvConstants.upper_cyan, debugInfo);

    List<Shape> blueShape = colorDivisionAndFindShape(hsv, "蓝色", "blue", HsvConstants.lower_blue,
        HsvConstants.upper_blue, debugInfo);

    List<Shape> purpleShape = colorDivisionAndFindShape(hsv, "紫色", "purple", HsvConstants.lower_purple,
        HsvConstants.upper_purple, debugInfo);

    List<Shape> retval = ListUitls.toList(blackShape, redShape, orangeShape, yellowShape, greenShape, cyanShape,
        blueShape, purpleShape);
    // 合并list并返回
    return retval;

  }

  /**
   * 分离图片颜色并且查找图形
   * 
   * @param recognize
   * @param tempPath
   * @param debug
   * @param mat
   * @param matFilename
   * @throws IOException
   */

  public static List<Shape> colorDivisionAndFindShape(Mat mat, String color, String colorSuffix, Scalar lower,
      Scalar upper, DebugInfo debugInfo) throws IOException {

    Boolean isSave = debugInfo.getIsSave();
    String baseName = debugInfo.getBaseName();

    String tempPath = debugInfo.getTempPath();
    String extensionName = debugInfo.getExtensionName();
    Boolean isUpload = debugInfo.getIsUpload();
    String uploadHost = debugInfo.getUploadHost();
    // 分离颜色
    Mat mask = colorDivision(color, mat, lower, upper);
    // 保存分离后的图像
    String colorBaseName = MatUtils.getBaseName(baseName, colorSuffix);
    String dstPath = MatUtils.getDstPath(tempPath, colorBaseName, extensionName);
    log.info("save file name:{}", colorBaseName);
    MatUtils.debugToFile(isSave, mask, colorBaseName, extensionName, dstPath, isUpload, uploadHost);

    DebugInfo debugInfoForFindShape = new DebugInfo(dstPath, isSave, tempPath);
    List<Shape> shapList = findShape(mask, color, debugInfoForFindShape);
    return shapList;
  }

  /**
   * 查找图形
   * 
   * @param color
   * 
   * @param redMask
   * @param debugInfo
   * @return
   * @throws IOException
   */
  public static List<Shape> findShape(Mat mask, String color, DebugInfo debugInfo) throws IOException {
    Boolean isSave = debugInfo.getIsSave();
    String baseName = debugInfo.getBaseName();
    String extensionName = debugInfo.getExtensionName();
    String tempPath = debugInfo.getTempPath();

    Boolean isUpload = debugInfo.getIsUpload();
    String uploadHost = debugInfo.getUploadHost();

    List<Shape> retval = new ArrayList<>();
    Mat thresholded = new Mat(mask.rows(), mask.cols(), CvType.CV_8UC1);
    int type = Imgproc.THRESH_BINARY | Imgproc.THRESH_TRIANGLE;
//    log.info("type:{}", type);
    Imgproc.threshold(mask, thresholded, 0, 255, type);

    String thresholdName = MatUtils.getBaseName(baseName, "threshold_0_255_" + type);
    String dstPath = tempPath + File.separator + thresholdName + "." + extensionName;
    log.info("save file name:{}", thresholdName);
    MatUtils.debugToFile(isSave, thresholded, thresholdName, extensionName, dstPath, isUpload, uploadHost);

    // 进行一次闭运算
    Mat structuringElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
    Mat morphology = new Mat(thresholded.rows(), thresholded.cols(), CvType.CV_8UC1);
    Imgproc.morphologyEx(thresholded, morphology, Imgproc.MORPH_CLOSE, structuringElement, new Point(-1, -1), 1);

    String morphologyName = MatUtils.getBaseName(thresholdName, "MORPH_CLOSE");
    String morphologyDstPath = MatUtils.getDstPath(tempPath, morphologyName, extensionName);
    log.info("save file name:{}", morphologyName);
    MatUtils.debugToFile(isSave, morphology, morphologyName, extensionName, morphologyDstPath, isUpload, uploadHost);

    // 查找二进制图像的轮廓
    Mat hierarchy = new Mat();
    List<MatOfPoint> contours = new ArrayList<>();
    // Imgproc.findContours(morphology, contours, hierarchy, Imgproc.RETR_TREE,
    // Imgproc.CHAIN_APPROX_SIMPLE);
    Imgproc.findContours(morphology, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
    // 如果设置为CHAIN_APPROX_NONE,在某些情况五角星会识别成9变形,测试使用CHAIN_APPROX_SIMPLE,测试无效,并且对于5变形,查找到了多个图形
    // Imgproc.findContours(morphology, contours, hierarchy,
    // Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE);
    //
    log.info("color:{},查找到的轮廓数量:{}", color, contours.size());
    // log.info("color:{},轮廓关系:{}", color, hierarchy);
    // 进行边框过滤,得到子图像
    List<MatOfPoint> realContours = new ArrayList<MatOfPoint>();
    for (int i = 0; i < contours.size(); i++) {
      MatOfPoint contour = contours.get(i);
      double contourArea = Imgproc.contourArea(contour);
      log.info("contourArea:{}", contourArea);
      // 过滤面积比较小和比较大的轮廓
      if (contourArea < 8|| contourArea > 30000) {
        // log.info("过滤面积:{}", contourArea);
        log.info("过滤面积:{}", i);
        continue;
      }
      Rect boundingRect = Imgproc.boundingRect(contour);
      // log.info("boundingRect:{}", boundingRect);
      // 过滤边框
      int height = mask.rows();
      int width = mask.cols();
      int x = boundingRect.x;
      int y = boundingRect.y;
      int borderWitdh = 5;
      if (x < borderWitdh || y < borderWitdh || (x >= width - borderWitdh && x <= width)
          || (y >= height - borderWitdh && y <= height)) {
        log.info("过滤边框:{}", i);
        continue;
      }
      realContours.add(contour);
    }
    int size = realContours.size();
    log.info("颜色:{},过滤后的轮廓数量:{}", color, size);
    // 在原图上画出轮廓并保存

//    Mat maskControus = mask.clone();
    // 因为是灰度图像,画布不出来图像,所以也无须保存
//    Imgproc.drawContours(maskControus, realContours, 0, new Scalar(0, 0, 255));
//    String realContoursName = MatUtils.getBaseName(morphologyName, "realContours");
//    String realContoursDstPath = MatUtils.getDstPath(tempPath, realContoursName, extensionName);
//    log.info("save file name:{}", realContoursName);
//    MatUtils.debugToFile(isSave, maskControus, realContoursName, extensionName, realContoursDstPath, isUpload, uploadHost);

    for (int i = 0; i < size; i++) {
      MatOfPoint contour = realContours.get(i);
      // 提取出轮廓区域处,计算区域处的面积,如果面积较小,则舍弃
      // 计算白色,区块数量,如果小于500,说明图像中没有图像,则跳过该颜色
      // 提取出轮廓
      Rect rect = Imgproc.boundingRect(contour);
      Mat boundingRectMat = morphology.submat(rect);
      // 保存boundingRectMat
      String boundingRectName = MatUtils.getBaseName(morphologyName, "boundingRect_" + i);
      String boundingRectNameDstPath = MatUtils.getDstPath(tempPath, boundingRectName, extensionName);
      log.info("save file name:{}", boundingRectNameDstPath);
      MatUtils.debugToFile(isSave, boundingRectMat, boundingRectName, extensionName, boundingRectNameDstPath, isUpload,
          uploadHost);
      int count = Core.countNonZero(boundingRectMat);
      int total = boundingRectMat.rows() * boundingRectMat.cols();

      // 计算白色点在图形中占据的比值
      double persent = new BigDecimal((float) count / total).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
      log.info("color:{},count:{},占比:{}", color, count, persent);
      if (persent < 0.3) {
//      白色区域太小,过滤掉
        log.info("color:{},位置:{},count:{},有效区域太小,跳过图形检测", color, i, count);
        continue;
      }

//      if (count < 110) { // 该值 //380
//        // 白色区域太小,过滤掉
//        log.info("color:{},位置:{},count:{},有效区域太小,跳过图形检测", color, i, count);
//        return retval;
//      }
      // 将轮廓写入文件
      Mat contourMat = Mat.zeros(morphology.size(), CvType.CV_8UC3);
      Imgproc.drawContours(contourMat, Arrays.asList(contour), 0, new Scalar(0, 0, 255));
      String contourName = MatUtils.getBaseName(morphologyName, "contour_" + i);
      String contourDstPath = MatUtils.getDstPath(tempPath, contourName, extensionName);
      // 保存近似多边形曲线之后的图像
      log.info("save file name:{}", contourName);
      MatUtils.debugToFile(isSave, contourMat, contourName, extensionName, contourDstPath, isUpload, uploadHost);

      // 直接识别图形并不十分的准确,先绘制一个边框再识别边框更准确
      MatOfPoint2f approxCurve = ShapeUtils.getShape(contour);
      // 在空白图形上画出图像
      ArrayList<MatOfPoint> list = new ArrayList<>();
      list.add(new MatOfPoint(approxCurve.toArray()));
      Mat approxCurveMat = Mat.zeros(morphology.size(), CvType.CV_8UC3);
      Imgproc.drawContours(approxCurveMat, list, 0, new Scalar(0, 0, 255));
      String approxCurveName = MatUtils.getBaseName(contourName, "approxCurve");
      String approxCurveDstPath = MatUtils.getDstPath(tempPath, approxCurveName, extensionName);
      // 保存近似多边形曲线之后的图像
      log.info("save file name:{}", approxCurveName);
      MatUtils.debugToFile(isSave, approxCurveMat, approxCurveName, extensionName, approxCurveDstPath, isUpload,
          uploadHost);

      List<MatOfPoint> contoursExternal = ShapeUtils.findExternalContours(approxCurveMat, debugInfo);

      for (MatOfPoint contourExternal : contoursExternal) {
        // 识别绘制的边框的的形状
        Shape shape = getShape(contourExternal);
        if (shape.getType() != null) { // 排除N边形
          shape.setColor(color);
          retval.add(shape);
          log.info("第:{}个轮廓,识别到的图形是:{}", i, shape.getType());
        }
      }
    }
    return retval;
  }

  /**
   * 获取图形形状
   * 
   * @param contour
   */
  public static Shape getShape(MatOfPoint contour) {
    Shape shape = new Shape();
    // 设置面积
    double contourArea = Imgproc.contourArea(contour);
    shape.setArea(contourArea);
    MatOfPoint2f matOfPoint2f = new MatOfPoint2f();
    contour.convertTo(matOfPoint2f, CvType.CV_32F);
    // 计算弧长
    double arcLength = Imgproc.arcLength(matOfPoint2f, true);
    // 设置弧长
    shape.setArcLength(arcLength);
//    log.info("arcLength:{}", arcLength);
    // 以指定的精确到近似多边形曲线
    MatOfPoint2f approxCurve = new MatOfPoint2f();
    // 进行第一次拟合
    Imgproc.approxPolyDP(matOfPoint2f, approxCurve, 0.03 * arcLength, true);
    // 设置多边形曲线
    shape.setApproxyCurve(approxCurve);
    // 进行第二次拟合
    // Imgproc.approxPolyDP(approxCurve, approxCurve, 0.03 * arcLength, true);
    // 进行第3次拟合
    // Imgproc.approxPolyDP(approxCurve, approxCurve, 0.03 * arcLength, true);
    // 测试多次拟合失败
    // log.info("approxCurve:{}", approxCurve);

    List<Point> pointList = matOfPoint2f.toList();
    // log.info("count:{}", pointList.size());// 47
    shape.setPointSize(pointList.size());
    Size size = approxCurve.size();
    // log.info("size:{}", size);
    // shape.setSize(size);
    // 矩形 1x4
    // 三角形 1x3
    // 五角星 1*10
    double height = size.height;
    if (height == 3d) {
//      log.info("三角形");
      shape.setShape(ShapeShape.三角形);
      double[] a = approxCurve.get(0, 0);
      double[] b = approxCurve.get(1, 0);
      double[] c = approxCurve.get(2, 0);
      // 三个顶点对应的角度 （单位：度）

      double angle_a = MathUtils.calculating_angle(b, c, a);
      double angle_b = MathUtils.calculating_angle(a, c, b);
      double angle_c = MathUtils.calculating_angle(a, b, c);

      // 最大角
      // double angle_max = max(angle_a, angle_b, angle_c);
      // 堂堂的Java竟然不支持一个函数求三个数中的最大数
      double angle_max = Math.max(angle_a, Math.max(angle_b, angle_c));

      double cos = Math.cos(Math.toRadians(angle_max));
//      log.info("cos:{}", cos);
      if (cos > 0.05) {
//        log.info("锐角三角形");
        shape.setType(ShapeType.锐角三角形);
      } else if (cos < -0.05) {
//        log.info("钝角三角形");
        shape.setType(ShapeType.钝角三角形);
      } else {
//        log.info("直角三角形");
        shape.setType(ShapeType.直角三角形);
      }

    } else if (height == 4d) {
//      log.info("四边形");
      shape.setShape(ShapeShape.四边形);
      // 四个顶点
      double[] a = approxCurve.get(0, 0);
      double[] b = approxCurve.get(1, 0);
      double[] c = approxCurve.get(2, 0);
      double[] d = approxCurve.get(3, 0);
//      log.info("a:{},b:{},c:{},d:{}", a, b, c, d);
      // 四个顶点对应的角度 （单位：度）
      double angle_a = MathUtils.calculating_angle(b, d, a);
      double angle_b = MathUtils.calculating_angle(a, c, b);
      double angle_c = MathUtils.calculating_angle(b, d, c);
      double angle_d = MathUtils.calculating_angle(a, c, d);

      // 直线ab边长 （单位：像素）
      double ab = MathUtils.calculating_distance(a, b);
      // 直线bc边长 （单位：像素）
      double bc = MathUtils.calculating_distance(b, c);

      double angle_max = MathUtils.max(angle_a, angle_b, angle_c, angle_d);
      double radians = Math.toRadians(angle_max);
      double cos = Math.cos(radians);
      double abs = Math.abs(ab - bc);
//      log.info("cos:{}", cos);
      if ((cos > 0.07 || cos < -0.07) && abs < 5) {
//        log.info("菱形");
        shape.setType(ShapeType.菱形);
      } else if (abs < 5) {
//        log.info("正方形");
        shape.setType(ShapeType.正方形);
      } else {
//        log.info("长方形");
        shape.setType(ShapeType.长方形);
      }
    } else if (height == 10d || height == 9d) { // 9变形也检测为五角星
//      log.info("五角星");
      // 面积大于弧长
      if (contourArea > arcLength) {
        shape.setShape(ShapeShape.五角星);
        shape.setType(ShapeType.五角星);
      } else {
        shape.setShape(ShapeShape.N变形);
      }

    } else {
      // 判断圆形和多边形
      // 筛选出圆形
      // 圆半径
      double r = arcLength / (2 * Math.PI);
      double pi = contourArea / (Math.pow(r, 2));
      if (Math.abs(pi - Math.PI) < 1.) {
        // 满足圆的条件
//        log.info("圆形");
        shape.setShape(ShapeShape.圆形);
        shape.setType(ShapeType.圆形);
      } else {
        // # n边形
        shape.setShape(ShapeShape.N变形);
//        log.info("shape:{} 边形 ,面积:{}", height, contourArea);
      }
    }
    return shape;
  }

  /**
   * 顔色分割
   * 
   * @param color
   * @param hsv
   * @param lowerb
   * @param upperb
   * @return
   */
  public static Mat colorDivision(String color, Mat hsv, Scalar lowerb, Scalar upperb) {
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

  public static String[] formatToArray(List<Shape> shapList) {
    // 进行格式化处理,方便android端显示
    int size = shapList.size();
    String[] array = new String[size];
    // 图形,类型,颜色,面积,弧长
    String templateString = "%s %s %s %s %s";

    for (int i = 0; i < size; i++) {
      Shape shape = shapList.get(i);
      array[i] = String.format(templateString, shape.getShape(), shape.getType(), shape.getColor(), shape.getArea(),
          shape.getArcLength());
    }
    return array;
  }
}
