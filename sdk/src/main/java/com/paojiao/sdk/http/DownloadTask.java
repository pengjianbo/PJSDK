package com.paojiao.sdk.http;

import android.os.AsyncTask;
import java.io.File;

/**
 * Desction:
 * Author:pengjianbo
 * Date:15/10/22 下午3:37
 */
public class DownloadTask extends AsyncTask<Void, Integer, File> {

    private String url;
    private File target;
    private DownloadListener listener;
    private long lastRefreshUiTime;

    public DownloadTask(String url, File target, DownloadListener listener) {
        this.url = url;
        this.target = target;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if ( listener != null ) {
            listener.onStart();
        }
        lastRefreshUiTime = System.currentTimeMillis();
    }

    @Override
    protected File doInBackground(Void... voids) {
        try {
            HttpRequest request = HttpRequest.get(url)
                    .readTimeout(30000)
                    .connectTimeout(30000);
            if (request.ok()) {

                final long totalLength = request.contentLength();
                if ( target.length() == totalLength ) {
                    publishProgress(100);
                    return target;
                }

                File parentFile = target.getParentFile();
                parentFile.mkdirs();
                File tempFile = new File(parentFile, target.getName()+".tmp");
                if ( tempFile.exists() ) {
                    tempFile.delete();
                }

                tempFile.createNewFile();
                final File file = tempFile;

                new Thread() {
                    @Override
                    public void run() {
                        while (true) {
                            int progress = (int) (file.length() * 100 / totalLength);
                            long curTime = System.currentTimeMillis();
                            if (curTime - lastRefreshUiTime >= 1000 || progress == 100) {
                                publishProgress(progress);
                                lastRefreshUiTime = System.currentTimeMillis();
                            }

                            if (progress == 100) {
                                break;
                            }

                            try {
                                interrupt();
                            } catch (Exception e) {
                            }
                        }
                    }
                }.start();

                request.receive(file);

                return file;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);

        if ( listener != null ) {
            listener.onProgress(progress[0]);
        }
    }

    @Override
    protected void onPostExecute(File file) {
        super.onPostExecute(file);
        if ( file == null ) {
            if ( listener != null ) {
                listener.onFailure(target);
                listener.onFinish();
            }
            return;
        }

        if ( target != file ) {
            //文件重命名
            file.renameTo(target);
        }

        if ( listener != null ) {
            listener.onSuccess(target);
            listener.onFinish();
        }
    }
}
