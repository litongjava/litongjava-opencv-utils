package com.litongjava.opencv.model;

import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shape {

  // 三角形,四边形,五角星,圆形,N变形
  private ShapeShape shape;

  /**
   * 三角形:直角三角形,钝角三角形,锐角三角形<br/>
   * 四边形:长方形,正方形,菱形</br>
   * 五角星:五角星<br/>
   * 圆形:圆形<br/>
   * N变形,五变形,六边形,七边形,八变形...
   */
  private ShapeType type;

  // 黑,红橙黄绿青蓝紫
  private String color;

  // 面积
  private Double area;

  // 弧长
  private Double arcLength;
  // 近似多边曲线
  private MatOfPoint2f approxyCurve;
  // 近似多边曲线的矩阵宽高
  private Size size;
  // 点的数量
  private int pointSize;
}
