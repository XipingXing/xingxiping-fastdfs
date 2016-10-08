package org.djr.fastdfs.utils;

/**
 * String 字符串工具类
 * 
 * @author xiping xing
 *
 *         Created on 2016年7月20日
 *
 */
public class StringUtils {

  /**
   * StringUtils.isBlank(null)    true
   * StringUtils.isBlank("")    true
   * StringUtils.isBlank("  ")    true
   * StringUtils.isBlank("bbb")    false
   * StringUtils.isBlank(" bbb ")    false
   * 
   * @param str
   * @return
   */
  public static boolean isBlank(String str) {
    int strLen = 0;
    if(str == null || (strLen = str.length()) == 0){
      return true;
    }
    for(int i = 0; i < strLen; i++){
      if(Character.isWhitespace(str.charAt(i)) == false){
        return false;
      }
    }
    return true;
  }

  /**
   * StringUtils.isBlank(null)    false
   * StringUtils.isBlank("")    false
   * StringUtils.isBlank("  ")    false
   * StringUtils.isBlank("bbb")    true
   * StringUtils.isBlank(" bbb ")    true
   * 
   * @param str
   * @return
   */
  public static boolean isNotBlank(String str) {
    return !StringUtils.isBlank(str);
  }
  
  /**
   * StringUtils.isBlank(null)    true
   * StringUtils.isBlank("")    true
   * StringUtils.isBlank("  ")    false
   * StringUtils.isBlank("bbb")    false
   * StringUtils.isBlank(" bbb ")    false
   * 
   * @param str
   * @return
   */
  public static boolean isEmpty(String str){
    return str == null || str.length() == 0;
  }
  
  /**
   * StringUtils.isBlank(null)    false
   * StringUtils.isBlank("")    false
   * StringUtils.isBlank("  ")    true
   * StringUtils.isBlank("bbb")    true
   * StringUtils.isBlank(" bbb ")    true
   * 
   * @param str
   * @return
   */
  public static boolean isNotEmpty(String str){
    return !StringUtils.isEmpty(str);
  }
  
}
