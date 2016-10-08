package org.djr.fastdfs.pool;

import org.djr.fastdfs.core.TrackerServer;

/**
 * 连接池接口
 * 
 * @author xiping xing
 *
 *         Created on 2016年6月15日
 *
 */
public interface IConnectionPool {

  /**
   * 获取连接
   * 
   * @return
   */
  public TrackerServer getConnection();

  /**
   * 释放连接
   * 
   * @param conn
   */
  public void releaseConncetion(TrackerServer conn);

  /**
   * 销毁清空
   */
  public void destory();

  /**
   * 连接池状态
   * 
   * @return
   */
  public boolean isActive();

  /**
   * 定时器，检查连接池
   */
  public void checkPool();

}
