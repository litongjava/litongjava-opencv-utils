package com.litongjava.opencv.utils;

/**
 * 数学计算工具类
 * 
 * @author Ping
 *
 */
public class MathUtils {

  /**
   * 从三个坐标点中计算角度
   * 
   * @param p1 点1
   * @param p2 点2
   * @param p0 交点 <br/>
   * @return
   */
  public static double calculating_angle(double[] p1, double[] p2, double[] p0) {
    //@formatter:off
    /*
     * 记各顶点坐标A（x1，y1）、B（x2，y2）、C（x3，y3），以求∠A为例： <br/>
     * 向量AB=（x2-x1，y2-y1），|AB|=c=√[(x2-x1)²+(y2-y1)²] <br/>
     * 向量AC=（x3-x1，y3-y1），|AC|=b=√[(x3-x1)²+(y3-y1)²] <br/>
     * AB · AC=(x2-x1)(x3-x1)+(y2-y1)(y3-y1) <br/> 
     * cosA = (AB · AC)/(|AB||AC|)=[(x2-x1)(x3-x1)+(y2-y1)(y3-y1)]/√[(x2-x1)²+(y2-y1)²][(x3-x1)²+(y3-y1)²]<br/>
     */
    //@formatter:on
    // 为了避免上面的代码被格式化,请使用eclipse开启Off/On tags 开启方法
    // Windows-->Preferences-->Java-->Code Style --> Formatter-->Edit-->Off/On tags
    double x1 = p1[0] - p0[0];
    double y1 = p1[1] - p0[1];
    double x2 = p2[0] - p0[0];
    double y2 = p2[1] - p0[1];
    // 注意在eclipse中 x1 ^ 2 提示错误
    // The operator ^ is undefined for the argument type(s) double, double
    // 使用 Math.pow(x, i); 替代

    // double angle = (x1 * x2 + y1 * y2) / Math.sqrt((x1 ^ 2 + y1 ^ 2) * (x2 ^ 2 +
    // y2 ^ 2));
    // return int(math.acos(angle) * 180 / math.pi)
    double angle = (x1 * x2 + y1 * y2)
        / Math.sqrt((Math.pow(x1, 2) + Math.pow(y1, 2)) * (Math.pow(x2, 2) + Math.pow(y2, 2)));
    return Math.acos(angle) * 180 / Math.PI;
  }

  /**
   * 从已知道的两个点计算两点之间距离
   * 
   * @param a
   * @param b
   * @return
   */
  public static double calculating_distance(double[] p0, double[] p1) {
    double x1 = p1[0] - p0[0];
    double y1 = p1[1] - p0[1];
    double dis = Math.sqrt(Math.pow(x1, 2) + Math.pow(y1, 2));
    return dis;
  }

  /**
   * 返回三个值中最大的一个
   * @param angle_a
   * @param angle_b
   * @param angle_c
   * @param angle_d
   * @return
   */
  public static double max(double angle_a, double angle_b, double angle_c, double angle_d) {
    return Math.max(angle_a, Math.max(angle_b, angle_c));
  }
}
