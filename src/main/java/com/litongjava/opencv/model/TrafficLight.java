package com.litongjava.opencv.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ping E Lee
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrafficLight {

  // 0 没有识别到 1 红 2 绿 3 黄色
  private int color;

  // 最大加权值
  public int maxBright;
  // 比值
  public float ratio;

  public TrafficLight(int color) {
    this.color = color;
  }

  public TrafficLight(int color, int maxBright) {
    this.color = color;
    this.maxBright = maxBright;
  }
}
