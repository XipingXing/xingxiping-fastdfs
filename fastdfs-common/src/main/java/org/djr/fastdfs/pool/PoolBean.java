package org.djr.fastdfs.pool;

import java.util.ArrayList;
import java.util.List;

/**
 * 连接池属性
 * 
 * @author xiping xing
 *
 *         Created on 2016年6月15日
 *
 */
public class PoolBean {

  private String fastDFSConfName = "config/fdfs_client.conf"; // fastDFS配置文件路径名称

  private String poolName = "fastdfs_pool"; //连接池名称

  private int initConnections = 2; // 初始化连接处

  private int minConnections = 1; // 空闲池，最小连接处

  private int maxConnections = 10; // 空闲池，最大连接处

  private int activeMaxConnections = 100; // 允许的最大连接数

  private long connectionTimeOut = 10 * 1000l; // 连接超时时间

  private boolean isCheakPool = false; // 是否定时检查连接池

  private long lazyCheck = 1000 * 60 * 60;// 延迟多少时间后开始 检查

  private long periodCheck = 1000 * 60 * 60;// 检查频率

  public String getFastDFSConfName() {
    return fastDFSConfName;
  }

  public void setFastDFSConfName(String fastDFSConfName) {
    this.fastDFSConfName = fastDFSConfName;
  }

  public String getPoolName() {
    return poolName;
  }

  public void setPoolName(String poolName) {
    this.poolName = poolName;
  }

  public int getInitConnections() {
    return initConnections;
  }

  public void setInitConnections(int initConnections) {
    this.initConnections = initConnections;
  }

  public int getMinConnections() {
    return minConnections;
  }

  public void setMinConnections(int minConnections) {
    this.minConnections = minConnections;
  }

  public int getMaxConnections() {
    return maxConnections;
  }

  public void setMaxConnections(int maxConnections) {
    this.maxConnections = maxConnections;
  }

  public int getActiveMaxConnections() {
    return activeMaxConnections;
  }

  public void setActiveMaxConnections(int activeMaxConnections) {
    this.activeMaxConnections = activeMaxConnections;
  }

  public long getConnectionTimeOut() {
    return connectionTimeOut;
  }

  public void setConnectionTimeOut(long connectionTimeOut) {
    this.connectionTimeOut = connectionTimeOut;
  }

  public boolean isCheakPool() {
    return isCheakPool;
  }

  public void setCheakPool(boolean isCheakPool) {
    this.isCheakPool = isCheakPool;
  }

  public long getLazyCheck() {
    return lazyCheck;
  }

  public void setLazyCheck(long lazyCheck) {
    this.lazyCheck = lazyCheck;
  }

  public long getPeriodCheck() {
    return periodCheck;
  }

  public void setPeriodCheck(long periodCheck) {
    this.periodCheck = periodCheck;
  }

}
