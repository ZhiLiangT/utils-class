package com.bbwhm.omeng.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.renderscript.Element;
import android.util.Log;
import android.widget.ImageView;

public class BitmapUtils {
	private static final String Tag = "BitmapUtils";

	/**
	 * 根据图片的url路径获得Bitmap对象
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap returnBitmap(String url) {
		URL fileUrl = null;
		Bitmap bitmap = null;

		try {
			fileUrl = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) fileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;

	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getHttpBitmap(String url) {
		URL myFileURL;
		Bitmap bitmap = null;
		try {
			myFileURL = new URL(url);
			// 获得连接
			HttpURLConnection conn = (HttpURLConnection) myFileURL
					.openConnection();
			// 设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
			conn.setConnectTimeout(6000);
			// 连接设置获得数据流
			conn.setDoInput(true);
			// 不使用缓存
			conn.setUseCaches(false);
			// 这句可有可无，没有影响
			// conn.connect();
			// 得到数据流
			InputStream is = conn.getInputStream();
			// 解析得到图片
			bitmap = BitmapFactory.decodeStream(is);
			// 关闭数据流
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bitmap;

	}

	/**
	 * 通过调用系统高斯模糊api的方法模糊
	 *
	 * @param bitmap
	 *            source bitmap
	 * @param radius
	 *            0 < radius <= 25
	 * @param context
	 *            context
	 * @return out bitmap
	 */
	@SuppressLint("NewApi")
	public static Bitmap blurBitmap(Bitmap bitmap, float radius, Context context) {
		// Let's create an empty bitmap with the same size of the bitmap we want
		// to blur
		Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Bitmap.Config.ARGB_4444);

		// Instantiate a new Renderscript
		android.renderscript.RenderScript rs = android.renderscript.RenderScript
				.create(context);

		// Create an Intrinsic Blur Script using the Renderscript
		android.renderscript.ScriptIntrinsicBlur blurScript = android.renderscript.ScriptIntrinsicBlur
				.create(rs, Element.U8_4(rs));

		// Create the Allocations (in/out) with the Renderscript and the in/out
		// bitmaps
		android.renderscript.Allocation allIn = android.renderscript.Allocation
				.createFromBitmap(rs, bitmap);
		android.renderscript.Allocation allOut = android.renderscript.Allocation
				.createFromBitmap(rs, outBitmap);

		// Set the radius of the blur
		blurScript.setRadius(radius);
		// Perform the Renderscript
		blurScript.setInput(allIn);
		blurScript.forEach(allOut);

		// Copy the final bitmap created by the out Allocation to the outBitmap
		allOut.copyTo(outBitmap);

		// recycle the original bitmap
		// bitmap.recycle();

		// After finishing everything, we destroy the Renderscript.
		// view.setBackground(new BitmapDrawable(context.getResources(),
		// outBitmap));
		rs.destroy();

		return outBitmap;
	}

	/**
	 * @param
	 * @描述 快速模糊化处理bitmap
	 * @作者 tll
	 * @时间 2016/12/5 19:22
	 */
	public static Bitmap fastblur(Bitmap sentBitmap, int radius) {

		Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

		if (radius < 1) {
			return (null);
		}

		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		int[] pix = new int[w * h];
		bitmap.getPixels(pix, 0, w, 0, 0, w, h);

		int wm = w - 1;
		int hm = h - 1;
		int wh = w * h;
		int div = radius + radius + 1;

		int r[] = new int[wh];
		int g[] = new int[wh];
		int b[] = new int[wh];
		int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
		int vmin[] = new int[Math.max(w, h)];

		int divsum = (div + 1) >> 1;
		divsum *= divsum;
		int temp = 256 * divsum;
		int dv[] = new int[temp];
		for (i = 0; i < temp; i++) {
			dv[i] = (i / divsum);
		}

		yw = yi = 0;

		int[][] stack = new int[div][3];
		int stackpointer;
		int stackstart;
		int[] sir;
		int rbs;
		int r1 = radius + 1;
		int routsum, goutsum, boutsum;
		int rinsum, ginsum, binsum;

		for (y = 0; y < h; y++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			for (i = -radius; i <= radius; i++) {
				p = pix[yi + Math.min(wm, Math.max(i, 0))];
				sir = stack[i + radius];
				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);
				rbs = r1 - Math.abs(i);
				rsum += sir[0] * rbs;
				gsum += sir[1] * rbs;
				bsum += sir[2] * rbs;
				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}
			}
			stackpointer = radius;

			for (x = 0; x < w; x++) {

				r[yi] = dv[rsum];
				g[yi] = dv[gsum];
				b[yi] = dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (y == 0) {
					vmin[x] = Math.min(x + radius + 1, wm);
				}
				p = pix[yw + vmin[x]];

				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[(stackpointer) % div];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi++;
			}
			yw += w;
		}
		for (x = 0; x < w; x++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			yp = -radius * w;
			for (i = -radius; i <= radius; i++) {
				yi = Math.max(0, yp) + x;

				sir = stack[i + radius];

				sir[0] = r[yi];
				sir[1] = g[yi];
				sir[2] = b[yi];

				rbs = r1 - Math.abs(i);

				rsum += r[yi] * rbs;
				gsum += g[yi] * rbs;
				bsum += b[yi] * rbs;

				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}

				if (i < hm) {
					yp += w;
				}
			}
			yi = x;
			stackpointer = radius;
			for (y = 0; y < h; y++) {
				pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
						| (dv[gsum] << 8) | dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (x == 0) {
					vmin[y] = Math.min(y + r1, hm) * w;
				}
				p = x + vmin[y];

				sir[0] = r[p];
				sir[1] = g[p];
				sir[2] = b[p];

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[stackpointer];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi += w;
			}
		}

		bitmap.setPixels(pix, 0, w, 0, 0, w, h);
		return (bitmap);
	}

	public static Bitmap postMatrix(Matrix matrix, Bitmap bitmap,
			boolean isRecycle) {
		Bitmap resource = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, false);
		if (isRecycle)
			destroyBitmap(bitmap);
		return resource;
	}

	/**
	 * 真实偏移,图片宽高会根据图片的偏移距离重新生成宽高,回收原始图片
	 *
	 * @param bitmap
	 *            ,目标图片
	 * @param preX
	 *            ,偏移X轴百分比
	 * @param preY
	 *            ,偏移Y轴百分比
	 * @return
	 */
	public static Bitmap translate(Bitmap bitmap, float preX, float preY) {
		return translate(bitmap, preX, preY, true);
	}

	/**
	 * 真实偏移,图片宽高会根据图片的偏移距离重新生成宽高
	 *
	 * @param bitmap
	 *            ,目标图片
	 * @param preX
	 *            ,偏移X轴百分比
	 * @param preY
	 *            ,偏移Y轴百分比
	 * @param isRecycle
	 *            ，是否回收目标图
	 * @return
	 */
	public static Bitmap translate(Bitmap bitmap, float preX, float preY,
			boolean isRecycle) {
		Matrix matrix = new Matrix();
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int disX = (int) (width * preX);
		int disY = (int) (height * preY);
		matrix.postTranslate(disX, disY);
		Bitmap resource = Bitmap.createBitmap(bitmap.getWidth() - disX,
				bitmap.getHeight() - disY, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(resource);
		// canvas.drawColor(Color.BLACK);//模擬空白
		canvas.concat(matrix);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		canvas.drawBitmap(bitmap, -disX, -disY, paint);// 从负偏移点开始画，那么如果进行了translate操作，那么图片不会留白
		if (isRecycle)
			destroyBitmap(bitmap);
		return resource;
	}

	/**
	 * 真实缩放,图片宽高会根据图片的缩放比例重新生成宽高,回收原始图片
	 *
	 * @param bitmap
	 * @param scale
	 * @return
	 */
	public static Bitmap scale(Bitmap bitmap, float scale) {
		return scale(bitmap, scale, true);
	}

	/**
	 * 真实缩放,图片宽高会根据图片的缩放比例重新生成宽高
	 *
	 * @param bitmap
	 *            ,目标
	 * @param scale
	 *            ，缩放比例
	 * @param isRecycle
	 *            ，是否回收目标图
	 * @return
	 */
	public static Bitmap scale(Bitmap bitmap, float scale, boolean isRecycle) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		int reWidth, reHeight;
		reWidth = (int) (width * scale);
		reHeight = (int) (height * scale);
		matrix.postScale(scale, scale);

		Bitmap resource = Bitmap.createBitmap(reWidth, reHeight,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(resource);
		// canvas.drawColor(Color.BLUE);//模擬空白
		canvas.concat(matrix);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		canvas.drawBitmap(bitmap, (float) ((reWidth - width) * 1.0 / 2),
				(float) ((reHeight - height) * 1.0 / 2), paint);
		if (isRecycle)
			destroyBitmap(bitmap);
		return resource;
	}

	/**
	 * 真实旋转,图片宽高会根据图片的旋转角度重新生成宽高,回收原始图片
	 *
	 * @param bitmap
	 * @param degree
	 * @return
	 */
	public static Bitmap rotate(Bitmap bitmap, float degree) {
		return rotate(bitmap, degree, true);
	}

	/**
	 * 真实旋转,图片宽高会根据图片的旋转角度重新生成宽高
	 *
	 * @param bitmap
	 *            ,目标
	 * @param degree
	 *            ，旋转角度
	 * @param isRecycle
	 *            ，是否回收目标图
	 * @return
	 */
	public static Bitmap rotate(Bitmap bitmap, float degree, boolean isRecycle) {

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		int reWidth, reHeight;
		// 以弧度为计算方式则新图size为（width*cos(a)+height*sin(a),
		// height*cos(a)+width*sin(a)）
		double angle = (degree * Math.PI) / 180;// 生成degree对应的弧度
		double a = Math.abs(Math.sin(angle)), b = Math.abs(Math.cos(angle));
		reWidth = (int) (width * b + height * a);
		reHeight = (int) (height * b + width * a);
		Log.i(Tag, "width: " + width + "   reWidth   :" + reWidth);
		Log.i(Tag, "height: " + height + "   reHeight   :" + reHeight);
		Bitmap resource = Bitmap.createBitmap(reWidth, reHeight,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(resource);
		// canvas.drawColor(Color.BLACK);//模擬空白
		matrix.postRotate(degree, reWidth / 2, reHeight / 2);
		canvas.concat(matrix);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		canvas.drawBitmap(bitmap, (float) ((reWidth - width) * 1.0 / 2),
				(float) ((reHeight - height) * 1.0 / 2), paint);
		if (isRecycle)
			destroyBitmap(bitmap);
		return resource;
	}

	/**
	 * 清除bitmap对象
	 *
	 * @param bitmap
	 *            目标对象
	 */
	public static void destroyBitmap(Bitmap bitmap) {
		if (bitmap != null) {
			Bitmap b = bitmap;
			if (b != null && !b.isRecycled()) {
				Log.d("BitmapUtils", "可以释放bitmap");
				b.recycle();
			}
			bitmap = null;
		}
	}

	/**
	 *
	 * @param imageView
	 */
	public static void releaseImageViewResouce(ImageView imageView) {
		if (imageView == null)
			return;
		Drawable drawable = imageView.getDrawable();
		if (drawable != null && drawable instanceof BitmapDrawable) {
			BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
			Bitmap bitmap = bitmapDrawable.getBitmap();
			if (bitmap != null && !bitmap.isRecycled()) {
				bitmap.recycle();
			}
		}
	}

	public static Bitmap getFileSimpleBitmap(String file_path, int width,
			int height) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		// 设置为true表示decode函数不会生成bitmap对象，仅是将图像的现相关参数填充到options对象里，
		// 这样我们就可以在不生成bitmap而获取到图像的相关参数了。
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(file_path, options);
		// inSampleSize:表示对图像像素的缩放比例，假设为2，表示decode还有的图像的像素为原图像的1/2
		options.inSampleSize = getFileSimpleSize(width, height, options);
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		// 在设置options的inSampleSize后我们将inJustDecodeBounds设置为false，再次调用就可生成bitmap了
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(file_path, options);
	}

	public static Bitmap getFileSimpleBitmap(Resources resources, int redId,
			int width, int height) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(resources, redId);
		options.inSampleSize = getFileSimpleSize(width, height, options);
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		return BitmapFactory.decodeResource(resources, redId, options);
	}

	private static int getFileSimpleSize(int width, int height,
			BitmapFactory.Options options) {
		int inSimpleSize = 1;
		if (options.outWidth > width || options.outHeight > height) {
			int widthRadio = Math.round(options.outWidth / width);
			int heightRadio = Math.round(options.outHeight / height);
			inSimpleSize = Math.min(widthRadio, heightRadio);
		}
		return inSimpleSize;
	}

	public static Bitmap getFitSampleBitmap(InputStream inputStream, int width,
			int height) throws Exception {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		byte[] bytes = readStream(inputStream);
		// BitmapFactory.decodeStream(inputStream, null, options);
		BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
		options.inSampleSize = getFileSimpleSize(width, height, options);
		options.inJustDecodeBounds = false;
		// return BitmapFactory.decodeStream(inputStream, null, options);
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
	}

	/*
	 * 从inputStream中获取字节流 数组大小
	 */
	public static byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		outStream.close();
		inStream.close();
		return outStream.toByteArray();
	}

	public static byte[] getBytes(Bitmap bitmap) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

		return baos.toByteArray();

	}

	public static Bitmap Bytes2Bimap(byte[] b) {

		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		} else {
			return null;
		}

	}

	public static Bitmap initBitmap(Bitmap bitmap, float redValue,
			float greenValue, float blueValue) {
		Bitmap copyBitmap;
		if (bitmap == null) {
			return null;
		}
		copyBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
				bitmap.getConfig());
		Canvas canvas = new Canvas(copyBitmap);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		float[] colorArray = new float[] { redValue, 0, 0, 0, 0, 0, greenValue,
				0, 0, 0, 0, 0, blueValue, 0, 0, 0, 0, 0, 1, 0 };
		ColorMatrix colorMatrix = new ColorMatrix(colorArray);// 将保存的颜色矩阵的数组作为参数传入
		ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(
				colorMatrix);// 再把该colorMatrix作为参数传入来实例化ColorMatrixColorFilter
		paint.setColorFilter(colorFilter);// 并把该过滤器设置给画笔
		canvas.drawBitmap(bitmap, new Matrix(), paint);// 传如baseBitmap表示按照原图样式开始绘制，将得到是复制后的图片
		return copyBitmap;
	}

	/**
	 *
	 * @param bm
	 *            图像 （不可修改）
	 * @param saturation
	 *            饱和度
	 * @param lum
	 *            亮度
	 * @return
	 */
	public static Bitmap handleImageEffect(Bitmap bm, float saturation,
			float lum) {

		Bitmap bmp = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(),
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		ColorMatrix saturationMatrix = new ColorMatrix();
		saturationMatrix.setSaturation(saturation);
		ColorMatrix lumMatrix = new ColorMatrix();
		lumMatrix.setScale(lum, lum, lum, 1);
		// 融合
		ColorMatrix imageMatrix = new ColorMatrix();
		imageMatrix.postConcat(saturationMatrix);
		imageMatrix.postConcat(lumMatrix);

		paint.setColorFilter(new ColorMatrixColorFilter(imageMatrix));
		canvas.drawBitmap(bm, 0, 0, paint);

		return bmp;
	}

}
