package com.anioutkazharkova.popmechreeder.common;

import android.content.Context;
import android.os.AsyncTask;

import com.anioutkazharkova.popmechreeder.entities.PostData;
import com.anioutkazharkova.popmechreeder.services.DatabaseWorker;

public class UpdatePostTask extends AsyncTask<String, Void, Boolean> {

	DatabaseWorker dbWorker;
	Context mContext;
	PostData post;

	public UpdatePostTask(Context context, PostData post) {
		dbWorker = new DatabaseWorker(context);
		mContext = context;
		this.post = post;
	}

	@Override
	protected Boolean doInBackground(String... params) {
		// TODO Auto-generated method stub
		if (post != null) {
			boolean res = dbWorker
					.updatePost(post.getLink(), post.isFavorite());
			if (post.isFavorite()) {
				return dbWorker.addToFavorite(post);
			} else {
				return dbWorker.removeFromFavorite(post);
			}
		}
		return true;
	}

}
