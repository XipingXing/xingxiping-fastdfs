package org.djr.fastdfs.utils;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.ImageIcon;

class ImageUtils {

  private final static int DEFAULT_X = 600;
  private final static int DEFAULT_Y = 600;
  private final static int DEFAULT_W = 600;
  private final static int DEFAULT_H = 600;

  /**
   * 切割图片 ，对于参数中的输入流，请自行处理，切割方法不予关闭
   * 
   * @param in
   *          图片输入流
   * @param imageName
   *          图片名称
   * @param x
   *          默认600
   * @param y
   *          默认600
   * @param w
   *          默认600
   * @param h
   *          默认600
   * @return
   * @throws Exception
   */
  protected static ByteArrayInputStream cutImage(InputStream in, String imageName, int x, int y, int w, int h)
      throws Exception {
    if (in == null) {
      throw new Exception("param of in can't be null!");
    } else {
      String imageForm = checkImageName(imageName);

      Iterator<ImageReader> iterator = ImageIO.getImageReadersByFormatName(imageForm);
      ImageReader reader = (ImageReader) iterator.next();
      ImageInputStream iis = null;
      try {
        iis = ImageIO.createImageInputStream(in);
      } catch (IOException e) {
        System.err.println("create inputStream of image has error! ---> " + e.getMessage());
        throw e;
      }
      reader.setInput(iis, true);

      ImageReadParam param = getImageReadParam(reader, x, y, w, h);

      BufferedImage bi = null;
      try {
        bi = reader.read(0, param);
      } catch (IOException e) {
        System.err.println("read bufferedImage from inputStream of image has error! ---> " + e.getMessage());
        throw e;
      }

      return getCutInputStream(bi, imageForm);
    }
  }

  /**
   * 图片压缩,对于参数中的输入流，请自行处理，压缩方法不予关闭
   * 
   * @param in
   * @param imageName
   * @param w
   * @param h
   * @return
   * @throws Exception
   */
  protected static ByteArrayInputStream zoomImage(InputStream in, String imageName, int w, int h) throws Exception {
    if (in == null) {
      throw new Exception("param of in can't be null!");
    } else {
      String imageForm = checkImageName(imageName);

      BufferedImage bufImg = ImageIO.read(in);
      Image Itemp = bufImg.getScaledInstance(w, h, BufferedImage.SCALE_SMOOTH);
      AffineTransformOp ato = getAffineTransformOp(bufImg, w, h);
      Itemp = ato.filter(bufImg, null);

      return getCutInputStream((BufferedImage) Itemp, imageForm);
    }
  }

  /**
   * 将图片打上水印
   * 
   * @param iconByte
   *          水印图片字节数组
   * @param srcImgByte
   *          原图片直接数组
   * @param srcImageFmt
   *          元图片格式
   * @param degree
   *          水印旋转
   * @param x
   *          水印位置（横坐标）
   * @param y
   *          水印位置（纵坐标）
   * @return
   * @throws Exception
   */
  protected static byte[] iconImage(byte[] iconByte, byte[] srcImgByte, String srcImageFmt, Integer degree, int x, int y)
      throws Exception {
    ByteArrayOutputStream os = null;
    try {
      Image srcImg = ImageIO.read(new ByteArrayInputStream(srcImgByte));

      BufferedImage buffImg = new BufferedImage(srcImg.getWidth(null), srcImg.getHeight(null),
          BufferedImage.TYPE_INT_RGB);

      // 得到画笔对象
      // Graphics g= buffImg.getGraphics();
      Graphics2D g = buffImg.createGraphics();

      // 设置对线段的锯齿状边缘处理
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

      g.drawImage(srcImg.getScaledInstance(srcImg.getWidth(null), srcImg.getHeight(null), Image.SCALE_SMOOTH), 0, 0,
          null);

      if (null != degree) {
        // 设置水印旋转
        g.rotate(Math.toRadians(degree), (double) buffImg.getWidth() / 2, (double) buffImg.getHeight() / 2);
      }

      // 水印图象的路径 水印一般为gif或者png的，这样可设置透明度
      ImageIcon imgIcon = new ImageIcon(iconByte);

      // 得到Image对象。
      Image img = imgIcon.getImage();

      float alpha = 0.5f; // 透明度
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));

      // 表示水印图片的位置
      g.drawImage(img, x, y, null);

      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

      g.dispose();

      os = new ByteArrayOutputStream();

      // 生成图片
      ImageIO.write(buffImg, srcImageFmt, os);

    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    } finally {
      try {
        if (null != os)
          os.close();
      } catch (Exception e) {
      }
    }
    return os.toByteArray();
  }

  /**
   * 校验图片名称，并返回图片格式
   * 
   * @param imageName
   * @return
   * @throws Exception
   */
  protected static String checkImageName(String imageName) throws Exception {
    if (imageName == null || StringUtils.isBlank(imageName)) {
      throw new Exception("picture name can`t be null or empty!");
    }

    if (imageName.indexOf(".") == -1) {
      throw new Exception("please give me a full picture name! With image form!");
    }

    String imageForm = imageName.substring(imageName.lastIndexOf(".") + 1);

    for (ImageFmt ift : ImageFmt.values()) {
      if (imageForm != null && StringUtils.isNotBlank(imageForm) && ift.toString().equals(imageForm.toLowerCase())) {
        return imageForm.toLowerCase();
      }
    }

    throw new Exception("Image form isn`t right!");

  }

  /**
   * 组装切割参数
   * 
   * @param reader
   * @param x
   * @param y
   * @param w
   * @param h
   * @return
   */
  private static ImageReadParam getImageReadParam(ImageReader reader, int x, int y, int w, int h) {
    ImageReadParam param = reader.getDefaultReadParam();
    Rectangle rect = new Rectangle(x == 0 ? DEFAULT_X : x, y == 0 ? DEFAULT_Y : y, w == 0 ? DEFAULT_W : w,
        h == 0 ? DEFAULT_H : h);
    param.setSourceRegion(rect);
    return param;
  }

  /**
   * 获取切割后的图片流
   * 
   * @param bi
   * @param imageForm
   * @return
   * @throws IOException
   */
  private static ByteArrayInputStream getCutInputStream(BufferedImage bi, String imageForm) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageOutputStream ios = null;
    try {
      ios = ImageIO.createImageOutputStream(baos);
    } catch (IOException e) {
      System.err.println("create outputStream of image has error! ---> " + e.getMessage());
      throw e;
    }

    ByteArrayInputStream in = null;
    try {
      ImageIO.write(bi, imageForm, ios);
      byte[] outputByte = baos.toByteArray();

      if (outputByte != null && outputByte.length > 0) {
        in = new ByteArrayInputStream(outputByte);
      }

    } catch (IOException e) {
      System.err.println("get inputStream of cut image has error! ---> " + e.getMessage());
      throw e;
    }
    return in;
  }

  /**
   * 获取压缩数据
   * 
   * @param bufImg
   * @param w
   * @param h
   * @return
   */
  private static AffineTransformOp getAffineTransformOp(BufferedImage bufImg, int w, int h) {
    double wr = 0, hr = 0;
    wr = (w == 0 ? DEFAULT_W : w) * 1.0 / bufImg.getWidth();
    hr = (h == 0 ? DEFAULT_H : h) * 1.0 / bufImg.getHeight();
    AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(wr, hr), null);
    return ato;
  }

  private enum ImageFmt {
    BMP("bmp"), GIF("gif"), JPEG("jpeg"), TIFF("tiff"), PSD("psd"), PNG("png"), SWF("swf"), PCX("pcx"), DXF("dxf"), WMF(
        "wmf"), EMF("emf"), LIC("lic"), EPS("eps"), TGA("tga"), JPG("jpg");

    private String fmtName;

    private ImageFmt(String fmtName) {
      this.fmtName = fmtName;
    }

    @Override
    public String toString() {
      return fmtName;
    }
  }
}
