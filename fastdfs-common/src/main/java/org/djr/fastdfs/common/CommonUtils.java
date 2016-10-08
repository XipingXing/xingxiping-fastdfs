package org.djr.fastdfs.common;

/**
 * 工具类
 * 
 * @author xiping xing
 *
 *         Created on 2016年6月17日
 *
 */
public class CommonUtils {

  private static String sepStr = "/";

  public static String parsePath(String path) {
    if (path != null && !"".equals(path)) {
      return path.replaceAll("\\\\", sepStr);
    }
    return path;
  }
}
