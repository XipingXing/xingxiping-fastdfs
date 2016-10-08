package org.djr.fastdfs.utils;

/**
 * 图片压缩比例实体
 * 
 * @author xiping xing
 *
 *         Created on 2016年8月11日
 *
 */
public class SizeBean {

  private int wsize;

  private int hsize;

  /**
   * 实体构造方法
   * 
   * @param x
   * @param y
   */
  public SizeBean(int w, int h) {
    this.wsize = w;
    this.hsize = h;
  }

  public int getWsize() {
    return wsize;
  }

  public int getHsize() {
    return hsize;
  }

}
