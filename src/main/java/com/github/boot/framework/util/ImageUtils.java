package com.github.boot.framework.util;

import com.sun.imageio.plugins.gif.GIFImageReader;
import com.sun.imageio.plugins.gif.GIFImageReaderSpi;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.*;
import java.net.URL;
import java.util.Iterator;
import java.util.Random;

/**
 * 图片处理工具类
 * 
 * @author cjh
 */
@SuppressWarnings("restriction")
public class ImageUtils {

	private static Logger logger = LoggerFactory.getLogger(ImageUtils.class);

	/**
	 * 生成验证码图片
	 * 
	 * @param code
	 *            随机码
	 * @param width
	 *            宽度
	 * @param height
	 *            高度
	 * @return
	 */
	public static BufferedImage getCodeImage(String code, int width, int height) {
		// 定义图像buff
		BufferedImage bufferedImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics graphics = bufferedImg.getGraphics();
		// 随机数生成器
		Random random = new Random();
		// 图像填充为白色
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, width, height);
		// 创建字体
		Font font = new Font("Fixedsys", Font.BOLD, 12);
		graphics.setFont(font);
		// 画边框
		graphics.setColor(Color.BLACK);
		graphics.drawRect(0, 0, width - 1, height - 1);
		// 随机产生10条干扰线，使图象中的认证码不易被其它程序探测到。
		graphics.setColor(Color.BLACK);
		for (int i = 0; i < 15; i++) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			int xl = random.nextInt(12);
			int yl = random.nextInt(12);
			graphics.drawLine(x, y, x + xl, y + yl);
		}
		Color color = new Color(255, 0, 0);
		graphics.setColor(color);
		for (int i = 0; i < code.length(); i++) {
			code = code.substring(i, i + 1);
			graphics.drawString(code, (i + 1) * 15, 22);
		}
		return bufferedImg;
	}

	/**
	 * base64字符串转化成图片
	 * 
	 * @param imgStr
	 *            base64字符串
	 * @return
	 * @throws Exception
	 */
	public static BufferedImage base642Image(String imgStr) throws Exception {
		byte[] b = Base64.decodeBase64(imgStr);
		ByteArrayInputStream in = new ByteArrayInputStream(b);
		BufferedImage bufImg = ImageIO.read(in);
		in.close();
		return bufImg;
	}

	/**
	 * base64解码
	 * 
	 * @param imgStr
	 * @return
	 * @throws Exception
	 */
	public static InputStream decodeBase64(String imgStr) throws Exception {
		byte[] b = Base64.decodeBase64(imgStr);
		return new ByteArrayInputStream(b);
	}

	/**
	 * 将图片按照指定的图片尺寸压缩
	 * 
	 * @param src
	 *            :源图片路径
	 * @param outFile
	 *            :输出的压缩图片的路径
	 * @param new_w
	 *            :压缩后的图片宽
	 * @param new_h
	 *            :压缩后的图片高
	 */
	public static File compressImage(BufferedImage src, File outFile, int newWidth, int newHeigth) throws IOException {
		BufferedImage newImg = null;
		// 判断输入图片的类型
		if (src.getType() == 13) {
			newImg = new BufferedImage(newWidth, newHeigth, BufferedImage.TYPE_4BYTE_ABGR);
		} else {
			newImg = new BufferedImage(newWidth, newHeigth, BufferedImage.TYPE_INT_RGB);
		}
		Graphics2D g = newImg.createGraphics();
		// 从原图上取颜色绘制新图
		g.drawImage(src, 0, 0, src.getWidth(), src.getHeight(), null);
		g.dispose();
		// 根据图片尺寸压缩比得到新图的尺寸
		newImg.getGraphics().drawImage(src.getScaledInstance(newWidth, newHeigth, Image.SCALE_SMOOTH), 0, 0, null);
		ImageIO.write(newImg, "jpg", outFile);
		return outFile;
	}

	/**
	 * 将图片按照指定的图片尺寸压缩
	 * 
	 * @param src
	 *            :源图片路径
	 * @param new_w
	 *            :压缩后的图片宽
	 * @param new_h
	 *            :压缩后的图片高
	 */
	public static BufferedImage compressImage(BufferedImage src, int newWidth, int newHeigth) throws IOException {
		BufferedImage newImg = null;
		// 判断输入图片的类型
		if (src.getType() == 13) {
			newImg = new BufferedImage(newWidth, newHeigth, BufferedImage.TYPE_4BYTE_ABGR);
		} else {
			newImg = new BufferedImage(newWidth, newHeigth, BufferedImage.TYPE_INT_RGB);
		}
		Graphics2D g = newImg.createGraphics();
		// 从原图上取颜色绘制新图
		g.drawImage(src, 0, 0, src.getWidth(), src.getHeight(), null);
		g.dispose();
		// 根据图片尺寸压缩比得到新图的尺寸
		newImg.getGraphics().drawImage(src.getScaledInstance(newWidth, newHeigth, Image.SCALE_SMOOTH), 0, 0, null);
		return newImg;
	}

	/**
	 * 在源图像上设置图片水印 当alpha==1时文字不透明（和在图片上直接输入文字效果一样）
	 * 
	 * @param srcImagePath
	 *            源图片路径
	 * @param appendImagePath
	 *            水印图片路径
	 * @param alpha
	 *            透明度
	 * @param x
	 *            水印图片的起始x坐标
	 * @param y
	 *            水印图片的起始y坐标
	 * @param width
	 *            水印图片的宽度
	 * @param height
	 *            水印图片的高度
	 * @param imageFormat
	 *            图像写入图片格式
	 * @param toPath
	 *            图像写入路径
	 * @throws IOException
	 */
	public static void alphaImage2Image(String srcImagePath, String appendImagePath, float alpha, int x, int y,
			int width, int height, String imageFormat, String toPath) throws IOException {
		FileOutputStream fos = null;
		try {
			BufferedImage image = ImageIO.read(new File(srcImagePath));
			// 创建java2D对象
			Graphics2D g2d = image.createGraphics();
			// 用源图像填充背景
			g2d.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null, null);
			// 设置透明度
			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
			g2d.setComposite(ac);
			// 设置水印图片的起始x/y坐标、宽度、高度
			BufferedImage appendImage = ImageIO.read(new File(appendImagePath));
			g2d.drawImage(appendImage, x, y, width, height, null, null);
			g2d.dispose();
			fos = new FileOutputStream(toPath);
			ImageIO.write(image, imageFormat, fos);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				fos.close();
			}
		}
	}

	/**
	 * 图片灰化操作
	 * 
	 * @param srcImage
	 *            读取图片路径
	 * @param toPath
	 *            写入灰化后的图片路径
	 * @param imageFormat
	 *            图片写入格式
	 */
	public static void grayImage(String srcImage, String toPath, String imageFormat) {
		try {
			BufferedImage src = ImageIO.read(new File(srcImage));
			ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
			ColorConvertOp op = new ColorConvertOp(cs, null);
			src = op.filter(src, null);
			ImageIO.write(src, imageFormat, new File(toPath));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 对图片裁剪，并把裁剪新图片保存
	 * 
	 * @param srcPath
	 *            读取源图片路径
	 * @param toPath
	 *            写入图片路径
	 * @param x
	 *            剪切起始点x坐标
	 * @param y
	 *            剪切起始点y坐标
	 * @param width
	 *            剪切宽度
	 * @param height
	 *            剪切高度
	 * @param readImageFormat
	 *            读取图片格式
	 * @param writeImageFormat
	 *            写入图片格式
	 * @throws IOException
	 */
	public static void cropImage(String srcPath, String toPath, int x, int y, int width, int height,
			String readImageFormat, String writeImageFormat) throws IOException {
		FileInputStream fis = null;
		ImageInputStream iis = null;
		try {
			// 读取图片文件
			fis = new FileInputStream(srcPath);
			Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName(readImageFormat);
			ImageReader reader = it.next();
			// 获取图片流
			iis = ImageIO.createImageInputStream(fis);
			reader.setInput(iis, true);
			ImageReadParam param = reader.getDefaultReadParam();
			// 定义一个矩形
			Rectangle rect = new Rectangle(x, y, width, height);
			// 提供一个 BufferedImage，将其用作解码像素数据的目标。
			param.setSourceRegion(rect);
			BufferedImage bi = reader.read(0, param);
			// 保存新图片
			ImageIO.write(bi, writeImageFormat, new File(toPath));
		} finally {
			if (fis != null){
				fis.close();
			}
			if (iis != null){
				iis.close();
			}
		}
	}

	/**
	 * 在图片上指定位置写文字
	 * 
	 * @param image
	 *            图片
	 * @param content
	 *            文字内容
	 * @param font
	 *            字体
	 * @param x
	 * @param y
	 * @return
	 */
	public static BufferedImage addText(BufferedImage image, String content, Font font, int x, int y) {
		try {
			Graphics2D g = image.createGraphics();
			g.setBackground(new Color(255, 0, 0));
			// 设置字体颜色
			g.setColor(Color.WHITE);
			g.setFont(font);
			String[] lines = content.split("-");
			for (int i = 0; i < lines.length; i++) {
				g.drawString(lines[i], x, y + (font.getSize() + 4) * i);
			}
			g.dispose();
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return image;
	}

	/**
	 * 按指定角度旋转图片
	 * 
	 * @param image
	 * @param angle
	 *            旋转角度
	 * @return
	 */
	public static BufferedImage rotate(BufferedImage image, float angle) {
		// Define Image Center (Axis of Rotation)
		int width = image.getWidth();
		int height = image.getHeight();
		int cx = width / 2;
		int cy = height / 2;
		// create an array containing the corners of the image (TL,TR,BR,BL)
		int[] corners = { 0, 0, width, 0, width, height, 0, height };
		// Define bounds of the image
		int minX, minY, maxX, maxY;
		minX = maxX = cx;
		minY = maxY = cy;
		double theta = Math.toRadians(angle);
		for (int i = 0; i < corners.length; i += 2) {
			// Rotates the given point theta radians around (cx,cy)
			int x = (int) Math
					.round(Math.cos(theta) * (corners[i] - cx) - Math.sin(theta) * (corners[i + 1] - cy) + cx);
			int y = (int) Math
					.round(Math.sin(theta) * (corners[i] - cx) + Math.cos(theta) * (corners[i + 1] - cy) + cy);
			// Update our bounds
			if (x > maxX){
				maxX = x;
			}
			if (x < minX){
				minX = x;
			}
			if (y > maxY){
				maxY = y;
			}
			if (y < minY){
				minY = y;
			}
		}
		// Update Image Center Coordinates
		cx = cx - minX;
		cy = cy - minY;
		// Create Buffered Image
		BufferedImage result = new BufferedImage(maxX - minX, maxY - minY, BufferedImage.TYPE_INT_ARGB);
		// Create Graphics
		Graphics2D g2d = result.createGraphics();
		// Enable anti-alias and Cubic Resampling
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		// Rotate the image
		AffineTransform xform = new AffineTransform();
		xform.rotate(theta, cx, cy);
		g2d.setTransform(xform);
		g2d.drawImage(image, -minX, -minY, null);
		g2d.dispose();
		xform = null;
		return result;
	}

	/**
	 * 合并图片(按指定初始x、y坐标将附加图片贴到底图之上)
	 * 
	 * @param negativeImage
	 *            背景图片
	 * @param additionImage
	 *            附加图片路径
	 * @param x
	 *            附加图片的起始点x坐标
	 * @param y
	 *            附加图片的起始点y坐标
	 */
	public static BufferedImage merge(BufferedImage negativeImage, BufferedImage additionImage, int x, int y) {
		Graphics g = negativeImage.getGraphics();
		g.drawImage(additionImage, x, y, null);
		g.dispose();
		return negativeImage;
	}

	/**
	 * 横向拼接图片（两张）
	 * 
	 * @param firstSrcImage
	 *            第一张图片的路径
	 * @param secondSrcImage
	 *            第二张图片的路径
	 * @param imageFormat
	 *            拼接生成图片的格式
	 */
	public static void joinImagesHorizontal(File firstSrcImage, File secondSrcImage, String imageFormat) {
		try {
			// 读取第一张图片
			BufferedImage imageOne = ImageIO.read(firstSrcImage);
			int width = imageOne.getWidth();// 图片宽度
			int height = imageOne.getHeight();// 图片高度
			// 从图片中读取RGB
			int[] imageArrayOne = new int[width * height];
			imageArrayOne = imageOne.getRGB(0, 0, width, height, imageArrayOne, 0, width);
			// 对第二张图片做相同的处理
			BufferedImage imageTwo = ImageIO.read(secondSrcImage);
			int width2 = imageTwo.getWidth();
			int height2 = imageTwo.getHeight();
			int[] imageArrayTwo = new int[width2 * height2];
			imageArrayTwo = imageTwo.getRGB(0, 0, width, height, imageArrayTwo, 0, width);
			// 生成新图片
			// int height3 = (height>height2 || height==height2)?height:height2;
			BufferedImage imageNew = new BufferedImage(width * 2, height, BufferedImage.TYPE_INT_RGB);
			// BufferedImage imageNew = new
			// BufferedImage(width+width2,height3,BufferedImage.TYPE_INT_RGB);
			imageNew.setRGB(0, 0, width, height, imageArrayOne, 0, width);// 设置左半部分的RGB
			imageNew.setRGB(width, 0, width, height, imageArrayTwo, 0, width);// 设置右半部分的RGB
			// imageNew.setRGB(width,0,width2,height2,imageArrayTwo,0,width2);//设置右半部分的RGB
			File joinImage = File.createTempFile(DataUtils.uuidStr(), "jpg");
			ImageIO.write(imageNew, imageFormat, joinImage);// 写图片
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 横向拼接图片（两张）
	 * 
	 * @param firstSrcImage
	 *            第一张图片的路径
	 * @param secondSrcImage
	 *            第二张图片的路径
	 * @param imageFormat
	 *            拼接生成图片的格式
	 * @return
	 * @throws Exception
	 */
	public static File joinImagesHorizontal(URL firstSrcImage, URL secondSrcImage, String imageFormat)
			throws Exception {
		BufferedImage img1 = ImageIO.read(firstSrcImage);
		BufferedImage img2 = ImageIO.read(secondSrcImage);
		int w1 = img1.getWidth();
		int h1 = img1.getHeight();
		int w2 = img2.getWidth();
		int h2 = img2.getHeight();
		if (w1 * h1 > w2 * h2) {
			compressImage(img1, w2, h2);
		} else {
			compressImage(img2, w1, h1);
		}
		// 从图片中读取RGB
		int[] imageArrayOne = new int[w1 * h1];
		// 逐行扫描图像中各个像素的RGB到数组中
		imageArrayOne = img1.getRGB(0, 0, w1, h1, imageArrayOne, 0, w1);
		int[] imageArrayTwo = new int[w1 * h1];
		imageArrayTwo = img2.getRGB(0, 0, w1, h1, imageArrayTwo, 0, w1);
		// 生成新图片
		BufferedImage destImage = new BufferedImage(w1 + w1, h1, BufferedImage.TYPE_INT_RGB);
		// 设置上半部分或左半部分的RGB
		destImage.setRGB(0, 0, w1, h1, imageArrayOne, 0, w1);
		destImage.setRGB(w1, 0, w1, h1, imageArrayTwo, 0, w1);
		File joinImage = File.createTempFile(DataUtils.uuidStr(), imageFormat);
		// 写图片
		ImageIO.write(destImage, imageFormat, joinImage);
		return joinImage;
	}

	/**
	 * 纵向拼接图片（两张）
	 * 
	 * @param firstSrcImagePath
	 *            读取的第一张图片
	 * @param secondSrcImagePath
	 *            读取的第二张图片
	 * @param imageFormat
	 *            图片写入格式
	 * @param toPath
	 *            图片写入路径
	 */
	public static void joinImagesVertical(String firstSrcImagePath, String secondSrcImagePath, String imageFormat,
			String toPath) {
		try {
			// 读取第一张图片
			File fileOne = new File(firstSrcImagePath);
			BufferedImage imageOne = ImageIO.read(fileOne);
			// 图片宽度
			int width = imageOne.getWidth();
			// 图片高度
			int height = imageOne.getHeight();
			// 从图片中读取RGB
			int[] imageArrayOne = new int[width * height];
			imageArrayOne = imageOne.getRGB(0, 0, width, height, imageArrayOne, 0, width);

			// 对第二张图片做相同的处理
			File fileTwo = new File(secondSrcImagePath);
			BufferedImage imageTwo = ImageIO.read(fileTwo);
			int width2 = imageTwo.getWidth();
			int height2 = imageTwo.getHeight();
			int[] imageArrayTwo = new int[width2 * height2];
			imageArrayTwo = imageTwo.getRGB(0, 0, width, height, imageArrayTwo, 0, width);
			BufferedImage imageNew = new BufferedImage(width, height * 2, BufferedImage.TYPE_INT_RGB);
			// 设置上半部分的RGB
			imageNew.setRGB(0, 0, width, height, imageArrayOne, 0, width);
			// 设置下半部分的RGB
			imageNew.setRGB(0, height, width, height, imageArrayTwo, 0, width);
			File outFile = new File(toPath);
			// 写图片
			ImageIO.write(imageNew, imageFormat, outFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 重新调整图片的大小
	 * 
	 * @param image
	 * @param outputWidth
	 * @param outputHeight
	 * @param maintainRatio
	 */
	public static void resize(BufferedImage image, int outputWidth, int outputHeight, boolean maintainRatio) {
		int width = image.getWidth();
		int height = image.getHeight();
		if (maintainRatio) {
			double ratio = 0;
			if (width > height) {
				ratio = (double) outputWidth / (double) width;
			} else {
				ratio = (double) outputHeight / (double) height;
			}
			double dw = width * ratio;
			double dh = height * ratio;
			outputWidth = (int) Math.round(dw);
			outputHeight = (int) Math.round(dh);
			if (outputWidth > width || outputHeight > height) {
				outputWidth = width;
				outputHeight = height;
			}
		}
		// Resize the image (create new buffered image)
		Image outputImage = image.getScaledInstance(outputWidth, outputHeight, BufferedImage.SCALE_AREA_AVERAGING);
		BufferedImage bi = new BufferedImage(outputWidth, outputHeight, image.getType());
		Graphics2D g2d = bi.createGraphics();
		g2d.drawImage(outputImage, 0, 0, null);
		g2d.dispose();
		image = bi;
		outputImage = null;
		bi = null;
		g2d = null;
	}

	/**
	 * 指定获取GIF图片的某一帧
	 * @param srcPath
	 * @param frame
	 * @return
	 */
	public static InputStream getGifOneFrame(String srcPath, int frame){
		FileImageInputStream in = null;
		try {
			in = new FileImageInputStream(new File(srcPath));
			ImageReaderSpi readerSpi = new GIFImageReaderSpi();
			GIFImageReader gifReader = (GIFImageReader) readerSpi.createReaderInstance();
			gifReader.setInput(in);
			int num = gifReader.getNumImages(true);
			if (num < frame){
				return null;
			}
			BufferedImage image = gifReader.read(frame);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(image, "jpg", os);
			return new ByteArrayInputStream(os.toByteArray());
		} catch (IOException e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			return null;
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				logger.error(ExceptionUtils.getFullStackTrace(e));
			}
		}
	}
}
