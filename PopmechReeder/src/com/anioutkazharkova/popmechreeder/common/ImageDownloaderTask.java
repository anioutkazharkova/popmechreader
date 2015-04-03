package com.anioutkazharkova.popmechreeder.common;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.anioutkazharkova.popmechreeder.services.DatabaseWorker;

public class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
	private ProgressDialog dialog;
	private String url;
	private Context mContext;
	DatabaseWorker dbWorker;

	public ImageDownloaderTask(ProgressDialog dialog, Context context) {
		this.dialog = dialog;
		mContext = context;
		dbWorker = new DatabaseWorker(context);
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		dialog.show();
	}

	public ImageDownloaderTask(Context context) {

		mContext = context;
		dbWorker = new DatabaseWorker(context);
	}

	@Override
	// Actual download method, run in the task thread
	protected Bitmap doInBackground(String... params) {
		// Increasing a priority of thread
		android.os.Process
				.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND
						+ android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE);
		// Getting url of feed as a parameter
		url = params[0];
		if (url != "") {
			// Correcting spaces
			url = url.replaceAll(" ", "%20");
			try {
				return downloadBitmap();
			} catch (Exception e) {
				return null;
			}
		}
		return null;

	}

	// Download method
	Bitmap downloadBitmap() {
		byte[] image = dbWorker.getImageByUrl(url);
		if (image == null) {
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

					// Scalling bitmap as necessary
					int size = Utility.DpToPx(mContext, (Utility.IS_LIST) ? 80
							: 400);
					int x = bitmap.getWidth();
					int y = bitmap.getHeight();

					float scale = (((float) x / size < (float) y / size) ? ((float) x / size)
							: ((float) y / size));

					if (scale > 1) {
						Bitmap scalledBitmap = Bitmap.createScaledBitmap(
								bitmap, (int) (x * scale), (int) (y * scale),
								true);

						bitmap.recycle();
						bitmap = null;

						// dbWorker.savePicture(BitmapToByte(scalledBitmap),
						// url);
						return scalledBitmap;
					} else
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

		} else {
			Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0,
					image.length);
			return bitmap;
		}

		return null;
	}

	public static byte[] BitmapToByte(Bitmap bmp) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		return byteArray;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		// TODO Auto-generated method stub
		dialog.dismiss();
	}

}
