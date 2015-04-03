package com.anioutkazharkova.popmechreeder.common;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.anioutkazharkova.popmechreeder.entities.PostData;
import com.anioutkazharkova.popmechreeder.services.DatabaseWorker;

public class GetFavoritesTask extends
		AsyncTask<Void, Void, ArrayList<PostData>> {

	ProgressDialog progress;
	DatabaseWorker dbWorker;
	public GetFavoritesTask(Context context)
	{
		dbWorker=new DatabaseWorker(context);
	}
	public GetFavoritesTask(Context context,ProgressDialog dialog)
	{
		this(context);
		this.progress=dialog;
	}
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		progress.show();
	}
	@Override
	protected ArrayList<PostData> doInBackground(Void... params) {
		// TODO Auto-generated method stub
		return dbWorker.getAllFavoritePosts();
	}
	@Override
	protected void onPostExecute(ArrayList<PostData> result) {
		// TODO Auto-generated method stub
		progress.dismiss();
	}

}
