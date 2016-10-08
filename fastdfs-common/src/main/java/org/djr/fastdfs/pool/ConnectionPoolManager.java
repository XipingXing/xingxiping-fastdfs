package org.djr.fastdfs.pool;

import org.djr.fastdfs.core.ClientGlobal;
import org.djr.fastdfs.core.TrackerServer;
import org.djr.fastdfs.utils.StringUtils;

/**
 * 连接池管理
 * 
 * @author xiping xing
 *
 *         Created on 2016年6月15日
 *
 */
public class ConnectionPoolManager {

  public IConnectionPool pool;

  private ConnectionPoolManager() {
    try {
      init();
    } catch (Exception e) {
      System.out.println("Error:Init connection filed -> ");
      e.printStackTrace();
    }
  }

  public static ConnectionPoolManager getInstance(String _fdfsConfPath) throws Exception {
    if (_fdfsConfPath == null || StringUtils.isEmpty(_fdfsConfPath)) {
      throw new Exception("please give me a file'path of fastdfsConfig! -> " + "_fdfsConfPath couldn't be null!");
    }
    ClientGlobal.conf_filename = _fdfsConfPath;
    return Singtonle.instance;
  }

  private static class Singtonle {
    private static ConnectionPoolManager instance = new ConnectionPoolManager();
  }

  public void init() throws Exception {
    ClientGlobal.init(ClientGlobal.conf_filename);
    PoolBean bean = ClientGlobal.bean;
    pool = new ConnectionPool(bean);
    System.out.println("Info:Init connection successed -> " + bean.getPoolName());
  }

  /**
   * 根据连接池名 ，获取连接
   * 
   * @param poolName
   * @return
   */
  public TrackerServer getConnection() {
    TrackerServer ts = null;
    if (pool != null) {
      ts = pool.getConnection();
    } else {
      System.out.println("Error:Can't find this conntceion pool -> ");
    }
    return ts;
  }

  /**
   * 关闭，回收连接
   * 
   * @param poolName
   */
  public void close(TrackerServer conn) {
    IConnectionPool icp = getPool();
    if (icp != null) {
      icp.releaseConncetion(conn);
    }
  }

  public void destroy() {
    IConnectionPool icp = getPool();
    if (icp != null) {
      icp.destory();
    }
  }

  public IConnectionPool getPool() {
    IConnectionPool icp = null;
    if (pool != null) {
      icp = pool;
    }
    return icp;
  }

}
