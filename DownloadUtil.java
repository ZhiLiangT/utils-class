package com.example.okhttpdemo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.os.Environment;
import android.support.annotation.NonNull;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class DownloadUtil {
	
	private static DownloadUtil downloadUtil;
    private static OkHttpClient okHttpClient;

    public static DownloadUtil get() {
        if (downloadUtil == null) {
            downloadUtil = new DownloadUtil();
        }
        return downloadUtil;
    }

    private DownloadUtil() {
        okHttpClient = new OkHttpClient();
    }

    /**
     * @param url ��������
     * @param saveDir ���������ļ���SDCardĿ¼
     * @param listener ���ؼ���
     */
    public void download(final String url, final String saveDir, final OnDownloadListener listener) {
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Request arg0, IOException arg1) {
				// ����ʧ��
                listener.onDownloadFailed();
			}
			@Override
			public void onResponse(Response response) throws IOException {
				InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                // ���������ļ���Ŀ¼
                String savePath = isExistDir(saveDir);
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(savePath, getNameFromUrl(url));
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        // ������
                        listener.onDownloading(progress);
                    }
                    fos.flush();
                    // �������
                    listener.onDownloadSuccess();
                } catch (Exception e) {
                    listener.onDownloadFailed();
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
			}
        });
    }

    /**
     * @param saveDir
     * @return
     * @throws IOException
     * �ж�����Ŀ¼�Ƿ����
     */
    private String isExistDir(String saveDir) throws IOException {
        // ����λ��
        File downloadFile = new File(Environment.getExternalStorageDirectory(), saveDir);
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }

    /**
     * @param url
     * @return
     * �����������н������ļ���
     */
    @NonNull
    private String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    public interface OnDownloadListener {
        /**
         * ���سɹ�
         */
        void onDownloadSuccess();

        /**
         * @param progress
         * ���ؽ���
         */
        void onDownloading(int progress);

        /**
         * ����ʧ��
         */
        void onDownloadFailed();
    }
public static int download(String downloadUrl, final String savePath){
	get();
		if(downloadUrl == null || downloadUrl.length() == 0 ||
				savePath == null || savePath.length() == 0) {
			return -1;
		}
		InputStream is=null;
		FileOutputStream fos=null;
		Request request=new Request.Builder().url(downloadUrl).get().build();
		Call call=okHttpClient.newCall(request);
		try {
			Response response=call.execute();
			is=response.body().byteStream();
			File file=new File(savePath+"tmp");
			if (file.exists()) {
				file.delete();
			}
			fos=new FileOutputStream(file);
			byte[] buffer = new byte[2048];
			int len = 0;
            while ((len = is.read(buffer)) != -1) {
            	fos.write(buffer, 0, len);
            }
            fos.flush();
            if(file.exists()) {
            	file.renameTo(new File(savePath));
        	}
            return 0;
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if (is!=null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos!=null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return -1;
	}
}
