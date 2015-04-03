package com.anioutkazharkova.popmechreeder.common;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class BigImageDownloadTask extends AsyncTask<String, Void, Bitmap> {
	private WeakReference<ImageView> imageViewReference = null;
	private String url;
	private Context mContext;

	public BigImageDownloadTask(ImageView imageView, Context context) {
		imageViewReference = new WeakReference<ImageView>(imageView);
		mContext = context;

	}

	@Override
	// Actual download method, run in the task thread
	protected Bitmap doInBackground(String... params) {
		// params comes from the execute() call: params[0] is the url.
		android.os.Process
				.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND
						+ android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE);

		url = params[0];
		if (url != "") {
			url = url.replaceAll(" ", "%20");
			try {
				return downloadBitmap();
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

	Bitmap downloadBitmap() {
		URL urlLink = null;
		try {
			urlLink = new URL(url);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {

			InputStream inputStream = null;
			try {
				inputStream = urlLink.openConnection().getInputStream();

				Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

				return bitmap;
			} finally {
				if (inputStream != null) {
					inputStream.close();
				}

			}

		} catch (Exception e) {

			Log.w("ImageDownloader", "Error while retrieving bitmap from "
					+ url);
		}

		return null;
	}
}
