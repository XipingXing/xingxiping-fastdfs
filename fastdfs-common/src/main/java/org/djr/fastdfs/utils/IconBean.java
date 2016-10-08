package org.djr.fastdfs.utils;

import java.io.InputStream;

/**
 * 水印图片上传实体
 * 
 * @author xiping xing
 *
 * Created on 2016年8月29日
 *
 */
public class IconBean {

  private int x;
  
  private int y;
  
  private Integer degree;
  
  private InputStream iconIn; //水印图片输入流
  
  /**
   * 构造函数
   * @param x  水印位置（横坐标）
   * @param y  水印位置（纵坐标）
   * @param degree  设置水印旋转
   * @param iconIn
   */
  public IconBean(int x, int y, Integer degree, InputStream iconIn){
    this.x = x;
    this.y = y;
    this.degree = degree;
    this.iconIn = iconIn;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public InputStream getIconIn() {
    return iconIn;
  }

  public Integer getDegree() {
    return degree;
  }

}
