package com.litongjava.opencv.model;

import org.junit.Test;

public class DebugInfoTest {

  @Test
  public void test() {
    DebugInfo debugInfo = new DebugInfo();
    String baseName = debugInfo.getBaseName();
    System.out.println(baseName);
  }
}
