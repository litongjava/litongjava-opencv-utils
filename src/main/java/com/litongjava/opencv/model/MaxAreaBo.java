package com.litongjava.opencv.model;

import org.opencv.core.Rect;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 最大面积
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaxAreaBo {
  private double areaMax = 0;
  private Rect rectMax = null;
}
