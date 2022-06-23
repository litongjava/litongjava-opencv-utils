package com.litongjava.opencv.constatns;

import org.opencv.core.Scalar;

public class HsvConstants {
  
  //白色
  public static Scalar lower_white = new Scalar(0, 0,221);
  public static Scalar upper_white = new Scalar(180, 30, 255);
  
  // 黑,灰,白,红,橙,黄,绿,青,蓝,紫, 和官方的阈值有出入,但是测试正常
  // 设定黑色阈值
  public static Scalar lower_black = new Scalar(0, 0, 0);
  public static Scalar upper_black = new Scalar(180, 255, 46);

  // 设置红色阈值
  public static Scalar lower_red = new Scalar(0, 120, 100);
  public static Scalar upper_red = new Scalar(10, 255, 255);
  public static Scalar lower_red_2 = new Scalar(156, 120, 100);
  public static Scalar upper_red_2 = new Scalar(180, 255, 255);

  // 设定橙色阈值
  public static Scalar lower_orange = new Scalar(11, 120, 100);
  public static Scalar upper_orange = new Scalar(25, 255, 255);

  // 设定黄色阈值
  public static Scalar lower_yellow = new Scalar(16, 60, 60);
  public static Scalar upper_yellow = new Scalar(40, 255, 255);

  // 设定绿色阈值
//  public static Scalar lower_green = new Scalar(41, 120, 100);
//  public static Scalar upper_green = new Scalar(77, 255, 255);
  public static Scalar lower_green = new Scalar(35, 43, 46);
  public static Scalar upper_green = new Scalar(77, 255, 255);
  
  // 设定青色阈值
  public static Scalar lower_cyan = new Scalar(78, 120, 100);
  public static Scalar upper_cyan = new Scalar(99, 255, 255);
  
  // 设定蓝色阈值
  public static Scalar lower_blue = new Scalar(100, 120, 100);
  public static Scalar upper_blue = new Scalar(124, 255, 255);

  // 设定紫色阈值
  public static Scalar lower_purple = new Scalar(125, 120, 100);
  public static Scalar upper_purple = new Scalar(155, 255, 255);

}
