package org.djr.fastdfs.utils;

import java.util.Map;

/**
 * 上传文件后，返回的相关信息
 * 
 * @author xiping xing
 *
 * Created on 2016年7月25日
 *
 */
public class FileInfoBean {

  private String id;
  
  private String group;
  
  private String file_name;
  
  private String imageHttpUrl;
  
  private final String HTTP_URL_HEADER = "http://";
  
  private Map<String, FileInfoBean> zoomMap; //缩略图实体
  
  /**
   * 构造方法
   * @param group
   * @param file_name
   * @param SPLIT_GROUP_NAME_AND_FILENAME_SEPERATOR   分隔符
   * @throws Exception 
   */
  public FileInfoBean(String group, String file_name, String SPLIT_GROUP_NAME_AND_FILENAME_SEPERATOR, String ip) throws Exception{
    if(StringUtils.isBlank(group) || StringUtils.isBlank(file_name) || StringUtils.isBlank(SPLIT_GROUP_NAME_AND_FILENAME_SEPERATOR)){
      throw new Exception("params can't be null or empty!");
    }
    
    this.group = group;
    this.file_name = file_name;
    this.id = this.group + SPLIT_GROUP_NAME_AND_FILENAME_SEPERATOR + this.file_name;
    try {
      ImageUtils.checkImageName(this.file_name);
      
      if(StringUtils.isBlank(ip)){
        throw new Exception("Ip of param can't be null or empty if uploadfile is picture!");
      }
      
      String[] split_file_name = this.file_name.split("/");
      if(split_file_name == null || split_file_name.length < 4)
        throw new Exception("File_name is error!");
//      this.imageHttpUrl = this.HTTP_URL_HEADER + ip + "/" + split_file_name[split_file_name.length - 3] + "/" + split_file_name[split_file_name.length - 2] + "/" + split_file_name[split_file_name.length - 1];
      this.imageHttpUrl="/" + this.id;
    } catch (Exception e) {
        System.err.println("ImageHttpUrl isn't necessary ,if uploadfile isn't picture!");
        this.imageHttpUrl = "";
    }
    
  }

  public String getId() {
    return id;
  }

  public String getGroup() {
    return group;
  }

  public String getFile_name() {
    return file_name;
  }

  public String getImageHttpUrl() {
    return imageHttpUrl;
  }

  public Map<String, FileInfoBean> getZoomMap() {
    return zoomMap;
  }

  public void setZoomMap(Map<String, FileInfoBean> zoomMap) {
    this.zoomMap = zoomMap;
  }

  @Override
  public String toString() {
    return "FileInfoBean [id=" + id + ", group=" + group + ", file_name=" + file_name + ", imageHttpUrl=" + imageHttpUrl
        + ", HTTP_URL_HEADER=" + HTTP_URL_HEADER + ", zoomMap=" + zoomMap + "]";
  }
  
}
