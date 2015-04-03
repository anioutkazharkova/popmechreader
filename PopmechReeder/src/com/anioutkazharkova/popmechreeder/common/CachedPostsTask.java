package com.anioutkazharkova.popmechreeder.common;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.anioutkazharkova.popmechreeder.entities.PostData;
import com.anioutkazharkova.popmechreeder.services.DatabaseWorker;

public class CachedPostsTask extends AsyncTask<String, Void, ArrayList<PostData>> {

	DatabaseWorker dbWorker;
	Context mContext;
	private String category="";
ProgressDialog progress;
	
	public CachedPostsTask(Context context) {
		mContext = context;
		dbWorker = new DatabaseWorker(context);
	}
	public CachedPostsTask(Context context,String category) {
		mContext = context;
		dbWorker = new DatabaseWorker(context);
		this.category=category;
	}
	public CachedPostsTask(Context context,String category,ProgressDialog dialog) 
	{
		this(context,category);
		this.progress=dialog;
	}
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		if (progress!=null)
			progress.show();
	}
	@Override
	protected ArrayList<PostData> doInBackground(String... params) {
		
		android.os.Process
		.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND
				+ android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE);
		if (category!="")
		{
			
			return dbWorker.getAllPostsByCategory(category);
		}
		else 
		return dbWorker.getAllPosts();
		
		
	}
	
	@Override
	protected void onPostExecute(ArrayList<PostData> result) {
		// TODO Auto-generated method stub
		if (progress!=null)
		{
			progress.dismiss();
		}
	}

	
}
