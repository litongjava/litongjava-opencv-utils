package com.litongjava.opencv.utils;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class FileUploadUtils {
  private static final int TIME_OUT = 10 * 1000; // 超时时间
  private static final String CHARSET = "utf-8"; // 设置编码
  private static final String uploadKey = "file";

  /**
   * 上传文件到服务器
   *
   * @param file       需要上传的文件
   * @param requestURL 请求的rul
   * @return 返回响应的内容
   */
  public static String uploadFile(File file, String requestURL) {
    if (file != null) {
      String filename = file.getName();
      try (InputStream fileInputStream = new FileInputStream(file)) {
        return upload(fileInputStream, requestURL, uploadKey, filename);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  public static String upload(InputStream inputStream, String requestURL, String name, String filename) {
    String PREFIX = "--", LINE_END = "\r\n";
    /**
     * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件 filename是文件的名字，包含后缀名的
     * 比如:abc.png
     */
    String contentDisposition = "Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"" + LINE_END;
    contentDisposition = String.format(contentDisposition, name, filename);
    String result = null;
    String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成

    String CONTENT_TYPE = "multipart/form-data"; // 内容类型

    try {
      URL url = new URL(requestURL);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setReadTimeout(TIME_OUT);
      conn.setConnectTimeout(TIME_OUT);
      conn.setDoInput(true); // 允许输入流
      conn.setDoOutput(true); // 允许输出流
      conn.setUseCaches(false); // 不允许使用缓存
      conn.setRequestMethod("POST"); // 请求方式
      conn.setRequestProperty("Charset", CHARSET); // 设置编码
      conn.setRequestProperty("connection", "keep-alive");
      conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
      conn.connect();

      /**
       * 当文件不为空，把文件包装并且上传
       */
      DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
      StringBuffer sb = new StringBuffer();
      sb.append(PREFIX);
      sb.append(BOUNDARY);
      sb.append(LINE_END);
      sb.append(contentDisposition);
      sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
      sb.append(LINE_END);
      dos.write(sb.toString().getBytes());

      byte[] bytes = new byte[1024];
      int len = 0;
      while ((len = inputStream.read(bytes)) != -1) {
        dos.write(bytes, 0, len);
      }
      dos.write(LINE_END.getBytes());
      byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
      dos.write(end_data);
      dos.flush();
      /**
       * 获取响应码 200=成功 当响应成功，获取响应的流
       */
      int res = conn.getResponseCode();
      if (res == 200) {
        InputStream input = conn.getInputStream();
        StringBuffer sb1 = new StringBuffer();
        int ss;
        while ((ss = input.read()) != -1) {
          sb1.append((char) ss);
        }
        result = sb1.toString();
      }
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }

  public static String upload(byte[] bytes, String requestURL, String name, String filename) {
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
    return upload(byteArrayInputStream,requestURL, name, filename);
  }
}
