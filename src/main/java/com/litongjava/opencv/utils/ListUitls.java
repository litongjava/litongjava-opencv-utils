package com.litongjava.opencv.utils;

import java.util.ArrayList;
import java.util.List;

public class ListUitls {

  @SafeVarargs
  public static <T> List<T> toList(List<T>... lists) {
    List<T> retval = new ArrayList<>();
    for (List<T> list : lists) {
      for (T t : list) {
        retval.add(t);
      }
    }
    return retval;
  }

}
