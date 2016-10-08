package org.djr.fastdfs.pool;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.djr.fastdfs.core.ClientGlobal;
import org.djr.fastdfs.core.ProtoCommon;
import org.djr.fastdfs.core.TrackerClient;
import org.djr.fastdfs.core.TrackerServer;

/**
 * fastDFS连接池
 * 
 * @author xiping xing
 *
 *         Created on 2016年6月15日
 *
 */
public class ConnectionPool implements IConnectionPool {

  private PoolBean poolBean;

  private TrackerClient tracker;

  /**
   * 是否活动
   */
  private boolean isActive = false;

  /**
   * 记录创建的中连接数
   */
  private int contActive = 0;

  /**
   * 空闲连接
   */
  private List<TrackerServer> freeConnection = new Vector<TrackerServer>();

  /**
   * 活跃连接
   */
  private List<TrackerServer> activeConnection = new Vector<TrackerServer>();

  public ConnectionPool(PoolBean poolBean) {
    super();
    this.poolBean = poolBean;
    init();
    checkPool();
  }

  public void init() {
    try {
      this.tracker = new TrackerClient();

      for (int i = 0; i < this.poolBean.getInitConnections(); i++) {
        TrackerServer trackerServer = newConnection(i % ClientGlobal.groups.length);
        if (trackerServer != null) {
          freeConnection.add(trackerServer);
          contActive++;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public synchronized TrackerServer getConnection() {
    TrackerServer ts = null;

    try {
      if (contActive < this.poolBean.getMaxConnections()) {
        if (freeConnection.size() > 0) {
          ts = freeConnection.get(0);
          freeConnection.remove(0);
        } else {
          ts = newConnection((contActive + 1) % ClientGlobal.groups.length);
        }
      } else {
        wait(this.poolBean.getConnectionTimeOut());
        ts = getConnection();
      }
      if (isValid(ts)) {
        activeConnection.add(ts);
        contActive++;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ts;
  }

  @Override
  public synchronized void releaseConncetion(TrackerServer conn) {
    if (isValid(conn) && !(freeConnection.size() > this.poolBean.getMaxConnections())) {
      freeConnection.add(conn);
      activeConnection.remove(conn);
      contActive--;
      notifyAll();
    }
  }

  @Override
  public synchronized void destory() {
    for (TrackerServer conn : freeConnection) {
      try {
        conn.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    for (TrackerServer conn : activeConnection) {
      try {
        conn.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    isActive = false;
    contActive = 0;
  }

  @Override
  public boolean isActive() {
    return isActive;
  }

  @Override
  public void checkPool() {
    if (this.poolBean.isCheakPool()) {
      new Timer().schedule(new TimerTask() {
        @Override
        public void run() {
          System.out.println("空线池连接数：" + freeConnection.size());
          System.out.println("活动连接数：：" + activeConnection.size());
          System.out.println("总的连接数：" + contActive);
        }
      }, this.poolBean.getLazyCheck(), this.poolBean.getPeriodCheck());
    }
  }

  /**
   * 判断当前连接是否可用
   * 
   * @param conn
   * @return
   * @throws IOException 
   */
  private boolean isValid(TrackerServer conn) {
    try {
      if (conn == null || conn.getSocket().isClosed()) {
        return false;
      }
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  private synchronized TrackerServer newConnection() throws IOException {
    TrackerServer ts = null;
    if (this.tracker != null) {
      ts = tracker.getConnection();
      ProtoCommon.activeTest(ts.getSocket());  //保证创建的tracker server可以保持长链接
    }
    return ts;
  }

  private synchronized TrackerServer newConnection(int groupIndex) throws IOException {
    TrackerServer ts = this.newConnection();
    if(groupIndex > ClientGlobal.groups.length - 1){
      groupIndex = new Random().nextInt(ClientGlobal.groups.length);
    }
    ts.setGroup(ClientGlobal.groups[groupIndex]);
    return ts;
  }

}
