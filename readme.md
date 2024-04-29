# litongjava-opencv-utils

## 文档
[文档](https://litongjava-opencv-utils-docs.vercel.app/)

## 1. 简介
`litongjava-opencv-utils` 是一个基于 OpenCV 的 Java 工具库，旨在提供一系列便利的功能，帮助开发者在 Java 应用程序中轻松实现图像处理和计算机视觉任务。此库封装了多种图像操作的方法，包括图像的读取、保存、形状识别、颜色提取和更多复杂的图像分析功能。


## 2. 主要特点
- **简化的 API**：提供了一系列简单易用的接口，让开发者能够快速实现如形状检测、颜色分离等复杂功能。
- **丰富的图像处理功能**：支持多种图像预处理操作，如灰度转换、二值化、轮廓提取等，为高级图像分析提供基础。
- **交通灯识别专用工具**：包含专门用于交通灯检测和状态识别的工具，适用于智能交通系统和自动驾驶领域。

## 3. 安装和使用
为了使用 `litongjava-opencv-utils`，您需要先在您的 Java 项目中引入 OpenCV 的库。库的安装可以通过 Maven 或直接将 JAR 文件添加到项目依赖中进行。一旦设置好环境，您就可以通过以下步骤来使用此工具库：

**步骤 1：添加库到项目中**

如果是使用 Maven，可以在 `pom.xml` 中添加相应的依赖（注意替换为最新版本）：
```xml
<dependency>
    <groupId>com.litongjava</groupId>
    <artifactId>opencv-utils</artifactId>
    <version>版本号</version>
</dependency>
```

**步骤 2：使用库中的方法**

以下是一个简单的示例，展示如何使用库中的方法来读取图像和提取颜色：
```java
import com.litongjava.opencv.utils.MatUtils;
import com.litongjava.opencv.utils.ColorDivisionUtils;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class Example {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);  // 加载本地库
        Mat src = MatUtils.imread("path/to/image.jpg");
        Scalar lower = new Scalar(0, 50, 50);
        Scalar upper = new Scalar(10, 255, 255);
        Mat redMask = ColorDivisionUtils.colorDivision(src, lower, upper);
        MatUtils.imwrite("path/to/output.jpg", redMask);
    }
}
```
这个示例加载了一个图像，使用 `ColorDivisionUtils` 提取图像中的红色部分，并保存结果图像。
