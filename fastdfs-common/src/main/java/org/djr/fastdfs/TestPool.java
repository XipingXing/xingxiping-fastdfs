package org.djr.fastdfs;

import org.djr.fastdfs.utils.FastdfsUtils;
import org.djr.fastdfs.utils.FileInfoBean;

public class TestPool {

  public static void main(String[] args) throws Exception {
      FastdfsUtils.fastdfsDeleteFile("group1/M00/00/22/CqfJp1e_nz6AQqv-AAO2E7G9YlI31.docx");
      
      Thread.sleep(5 * 60 * 1000l);
      
      FileInfoBean b1 = FastdfsUtils.fastdfsUploadFile("C:\\Users\\Administrator\\Desktop\\fastdfs-common.jar");
      System.out.println(b1);
  }

}
