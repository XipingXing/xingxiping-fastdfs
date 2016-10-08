package org.djr.fastdfs.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.djr.fastdfs.common.MyException;
import org.djr.fastdfs.common.NameValuePair;
import org.djr.fastdfs.core.StorageClient1;
import org.djr.fastdfs.core.TrackerServer;
import org.djr.fastdfs.pool.ConnectionPoolManager;
import org.djr.fastdfs.pool.IConnectionPool;

/**
 * fastdfs工具类
 * 
 * @author xiping xing
 *
 *         Created on 2016年6月21日
 *
 */
public class FastdfsUtils {

  private static String fdfs_client_conf = "fdfs_client.conf";

  /**
   * 文件上传
   * 
   * @param filePath
   *          文件路径
   * @return
   * @throws MyException
   * @throws IOException
   */
  public static FileInfoBean fastdfsUploadFile(String filePath) throws Exception {
    TrackerServer trackerServer = getConPool().getConnection();
    StorageClient1 client = new StorageClient1(trackerServer, null);

    NameValuePair[] metaList = new NameValuePair[1];
    metaList[0] = new NameValuePair("fileName", filePath);
    FileInfoBean file = client.upload_file1(trackerServer.getGroup(), filePath, null, metaList);
    getConPool().releaseConncetion(trackerServer);
    return file;
  }

  /**
   * 
   * 文件上传
   * 
   * @param fileBytes
   *          字节
   * @param file_ext_name
   *          文件扩展名，不包括(.)
   * @return
   * @throws Exception
   */
  public static FileInfoBean fastdfsUploadFile(byte[] fileBytes, String file_ext_name) throws Exception {

    if (file_ext_name == null || StringUtils.isEmpty(file_ext_name) || file_ext_name.indexOf(".") > -1) {
      throw new Exception(
          "file_ext_name param couldn't be null, empty or include dot,please make sure! -> " + file_ext_name);
    }

    TrackerServer trackerServer = getConPool().getConnection();
    StorageClient1 client = new StorageClient1(trackerServer, null);

    NameValuePair[] metaList = new NameValuePair[0];
    FileInfoBean file = client.upload_file1(trackerServer.getGroup(), fileBytes, file_ext_name, metaList);
    getConPool().releaseConncetion(trackerServer);

    return file;
  }

  /**
   * 上传文件
   * 
   * @param in
   *          输入流
   * @param file_ext_name
   *          扩展名
   * @param charsetName
   *          编码，默认UTF-8
   * @return
   * @throws Exception
   */
  public static FileInfoBean fastdfsUploadFile(InputStream in, String file_ext_name, String charsetName)
      throws Exception {
    if (in == null) {
      throw new Exception("file inputStream couldn't be null!");
    }
    byte[] b = streamToByteArray(in);

    in.close();
    return fastdfsUploadFile(b, file_ext_name);
  }

  /**
   * 文件上传
   * 
   * @param filePath
   *          文件路径
   * @param filePath
   *          storage组
   * @return
   * @throws MyException
   * @throws IOException
   */
  public static FileInfoBean fastdfsUploadFile(String filePath, String group_name) throws Exception {
    TrackerServer trackerServer = getConPool().getConnection();
    StorageClient1 client = new StorageClient1(trackerServer, null);

    NameValuePair[] metaList = new NameValuePair[1];
    metaList[0] = new NameValuePair("fileName", filePath);
    FileInfoBean file = client.upload_file1(group_name, filePath, null, metaList);
    getConPool().releaseConncetion(trackerServer);
    return file;
  }

  /**
   * 
   * 文件上传
   * 
   * @param fileBytes
   *          字节
   * @param file_ext_name
   *          文件扩展名，不包括(.)
   * @param gropu_name
   *          storage组
   * @return
   * @throws Exception
   */
  public static FileInfoBean fastdfsUploadFile(byte[] fileBytes, String group_name, String file_ext_name)
      throws Exception {

    if (file_ext_name == null || StringUtils.isEmpty(file_ext_name) || file_ext_name.indexOf(".") > -1) {
      throw new Exception(
          "file_ext_name param couldn't be null, empty or include dot,please make sure! -> " + file_ext_name);
    }

    TrackerServer trackerServer = getConPool().getConnection();
    StorageClient1 client = new StorageClient1(trackerServer, null);

    NameValuePair[] metaList = new NameValuePair[0];
    FileInfoBean file = client.upload_file1(group_name, fileBytes, file_ext_name, metaList);
    getConPool().releaseConncetion(trackerServer);

    return file;
  }

  /**
   * 上传文件
   * 
   * @param in
   *          输入流
   * @param group_name
   *          storage组名
   * @param file_ext_name
   *          扩展名
   * @param charsetName
   *          编码，默认UTF-8
   * @return
   * @throws Exception
   */
  public static FileInfoBean fastdfsUploadFile(InputStream in, String group_name, String file_ext_name,
      String charsetName) throws Exception {
    if (in == null) {
      throw new Exception("file inputStream couldn't be null!");
    }
    byte[] b = streamToByteArray(in);

    in.close();
    return fastdfsUploadFile(b, group_name, file_ext_name);
  }

  /**
   * 上传图片，支持水印，多种尺寸缩略图上传，
   * 
   * @param in
   *          原图输入流
   * @param iconBean
   *          水印图片实体bean，包含位置和水印图片流
   * @param file_ext_name
   *          原图扩展名 如：PNG
   * @param size
   *          缩略图尺寸字体，支持多种
   * @return
   * @throws Exception
   */
  public static FileInfoBean fastdfsUploadFileWithZoom(InputStream in, IconBean iconBean, String file_ext_name,
      SizeBean... size) throws Exception {
    if (in == null) {
      throw new Exception("file inputStream couldn't be null!");
    }

    if (file_ext_name == null || StringUtils.isEmpty(file_ext_name) || file_ext_name.indexOf(".") > -1) {
      throw new Exception(
          "file_ext_name param couldn't be null, empty or include dot,please make sure! -> " + file_ext_name);
    }

    byte[] b = streamToByteArray(in);
    byte[] icon = null;

    Map<String, FileInfoBean> zoomMap = null;
    if (iconBean != null && iconBean.getIconIn() != null) {
      icon = streamToByteArray(iconBean.getIconIn()); // 水印
      iconBean.getIconIn().close();
    }
    in.close();

    if (size != null && size.length > 0) { // 上传缩略图，支持多种格式进行压缩
      zoomMap = new HashMap<String, FileInfoBean>();
      for (SizeBean s : size) {
        ByteArrayInputStream _in = new ByteArrayInputStream(b);
        ByteArrayInputStream resIn = ImageUtils.zoomImage(_in, "." + file_ext_name, s.getWsize(), s.getHsize());
        if (_in != null) {
          _in.close();
        }
        if (resIn != null) {
          FileInfoBean zoomFile = fastdfsUploadFile(resIn, file_ext_name, null);
          resIn.close();
          zoomMap.put(s.getWsize() + "*" + s.getHsize(), zoomFile);
        }
      }
    }

    if (icon != null) {
      b = ImageUtils.iconImage(icon, b, file_ext_name, iconBean.getDegree(), iconBean.getX(), iconBean.getY());
    }
    FileInfoBean fileInfo = fastdfsUploadFile(b, file_ext_name); // 上传原图
    fileInfo.setZoomMap(zoomMap);
    return fileInfo;
  }

  /**
   * 删除文件
   * 
   * @param fileId
   *          文件id
   * @return 0 成功，其他失败
   * @throws Exception
   */
  public static void fastdfsDeleteFile(String... fileId) throws Exception {

    if (fileId == null || fileId.length <= 0) {
      throw new Exception("fileId param couldn't be null or empty,please make sure! -> " + fileId);
    }

    TrackerServer trackerServer = getConPool().getConnection();
    StorageClient1 client = new StorageClient1(trackerServer, null);
    for (String id : fileId)
      client.delete_file1(id);
    getConPool().releaseConncetion(trackerServer);
  }

  /**
   * 下载文件
   * 
   * @param fileId
   *          文件id
   * @return
   * @throws Exception
   */
  public static byte[] fastdfsDownLoadFile(String fileId) throws Exception {

    if (fileId == null || StringUtils.isEmpty(fileId)) {
      throw new Exception("fileId param couldn't be null or empty,please make sure! -> " + fileId);
    }

    TrackerServer trackerServer = getConPool().getConnection();
    StorageClient1 client = new StorageClient1(trackerServer, null);

    byte[] resFile = client.download_file1(fileId);
    getConPool().releaseConncetion(trackerServer);

    return resFile;

  }

  private static IConnectionPool getConPool() throws Exception {
    ConnectionPoolManager cpm = ConnectionPoolManager.getInstance(fdfs_client_conf);
    IConnectionPool icp = cpm.getPool();
    return icp;
  }

  /**
   * 将流转成字节数组
   * 
   * @param in
   * @return
   * @throws IOException
   */
  private static byte[] streamToByteArray(InputStream in) throws IOException {
    int count = 0;
    while (count == 0) {
      count = in.available();
    }
    byte[] b = new byte[count];
    in.read(b);
    return b;
  }

}
