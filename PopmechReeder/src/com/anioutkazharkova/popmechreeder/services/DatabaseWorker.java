package com.anioutkazharkova.popmechreeder.services;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.anioutkazharkova.popmechreeder.entities.PostData;

//Dispatcher used to save data from database and get it from db. 
public class DatabaseWorker {
	Context mContext;
	DatabaseHelper dbHelper;
	int index = 0;

	public DatabaseWorker(Context context) {
		this.mContext = context;
	}

	public ArrayList<PostData> savePosts(ArrayList<PostData> posts) {
		if (posts != null) {
			ArrayList<PostData> prevPosts = getAllPosts();
			int ind = -1;
			if (prevPosts != null && prevPosts.size() > 0) {
				ind = posts.indexOf(prevPosts.get(0));
				if (ind != -1 && ind != 0) {
					List<PostData> temp = posts.subList(0, ind);
					prevPosts.addAll(0, temp);
				}
			} else {
				prevPosts = posts;
			}

			boolean res = cleanPosts();
			for (PostData post : prevPosts) {
				if (post != null) {
					// ++index;
					savePost(post, mContext);
				}
			}
			return prevPosts;

		}
		return posts;
	}

	public ArrayList<PostData> savePosts(ArrayList<PostData> posts,
			String category) {
		if (posts != null) {
			ArrayList<PostData> prevPosts = getAllPostsByCategory(category);

			if (prevPosts != null && prevPosts.size() > 0) {
				int ind = posts.indexOf(prevPosts.get(0));
				if (ind != -1 && ind != 0) {
					List<PostData> temp = posts.subList(0, ind);
					prevPosts.addAll(0, temp);
				}
			} else {
				prevPosts = posts;
			}
			boolean res = cleanPostsByCategory(category);
			for (PostData post : prevPosts) {
				if (post != null) {
					// ++index;
					savePost(post, mContext);
				}
			}
			return prevPosts;
		}
		return posts;
	}

	public void savePicture(byte[] image, String url) {
		dbHelper = new DatabaseHelper(mContext);
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		ContentValues cv = new ContentValues();
		cv.put("image", image);
		cv.put("url", url);

		long row = db.insert("images", null, cv);
		db.close();
		dbHelper.close();

	}

	public byte[] getImageByUrl(String url) {
		byte[] image = null;
		dbHelper = new DatabaseHelper(mContext);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = db.query("images", new String[] { "image" }, "url = ?",
				new String[] { url }, null, null, null);
		if (c != null) {
			if (c.getCount() > 0) {
				c.moveToFirst();
				image = c.getBlob(0);
			}
		}
		c.close();
		db.close();
		dbHelper.close();
		return image;

	}

	public boolean cleanFavorites() {
		dbHelper = new DatabaseHelper(mContext);
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		int id = db.delete("favorites", null, null);

		ContentValues cv = new ContentValues();
		cv.put("favorite", 0);

		id = db.update("posts", cv, "favorite = ?", new String[] { "1" });
		db.close();
		dbHelper.close();

		return true;
	}

	public boolean cleanAll() {
		cleanPosts();
		cleanImages();

		return true;
	}

	public boolean cleanPosts() {
		dbHelper = new DatabaseHelper(mContext);
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		int id = db.delete("posts", null, null);
		db.close();
		dbHelper.close();

		return true;
	}

	public boolean cleanPostsByCategory(String category) {
		dbHelper = new DatabaseHelper(mContext);
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		int id = db.delete("posts", "category = ?", new String[] { category });
		db.close();
		dbHelper.close();

		return true;
	}

	public boolean updatePost(String url, boolean isFavorite) {
		dbHelper = new DatabaseHelper(mContext);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("favorite", isFavorite ? 1 : 0);

		int id = db.update("posts", cv, "link = ?", new String[] { url });
		db.close();
		dbHelper.close();
		return true;
	}

	public boolean addToFavorite(PostData post) {
		dbHelper = new DatabaseHelper(mContext);
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		ContentValues cv = new ContentValues();
		// cv.put("id", index);
		cv.put("title", post.getTitle());
		cv.put("description", post.getDescription());
		cv.put("link", post.getLink());
		cv.put("date", post.getPubDate());
		cv.put("image", post.getUrlImage());
		cv.put("category", post.getCategory());
		long row = db.insert("favorites", null, cv);

		db.close();
		dbHelper.close();
		return true;
	}

	public boolean removeFromFavorite(PostData post) {
		dbHelper = new DatabaseHelper(mContext);
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		int id = db.delete("favorites", "link = ?",
				new String[] { post.getLink() });
		db.close();
		dbHelper.close();
		return true;
	}

	private void savePost(PostData post, Context context) {
		dbHelper = new DatabaseHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		ContentValues cv = new ContentValues();
		// cv.put("id", index);
		cv.put("title", post.getTitle());
		cv.put("description", post.getDescription());
		cv.put("link", post.getLink());
		cv.put("date", post.getPubDate());
		cv.put("image", post.getUrlImage());
		cv.put("favorite", post.isFavorite() ? 1 : 0);
		cv.put("category", post.getCategory());
		long row = db.insert("posts", null, cv);

		db.close();
		dbHelper.close();

	}

	public ArrayList<PostData> getAllPostsByCategory(String category) {
		ArrayList<PostData> posts = new ArrayList<PostData>();
		dbHelper = new DatabaseHelper(mContext);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = db.query("posts", null, "category = ?",
				new String[] { category }, null, null, null);
		if (c != null) {
			if (c.getCount() > 0) {
				c.moveToFirst();
				do {
					PostData post = new PostData();
					post.setTitle(c.getString(1));
					post.setDescription(c.getString(2));
					post.setPubDate(c.getString(3));
					post.setLink(c.getString(4));
					post.setUrlImage(c.getString(5));
					post.setFavorite(c.getInt(6) == 1 ? true : false);
					post.setCategory(c.getString(7));
					posts.add(post);
				} while (c.moveToNext());
			}
		}
		c.close();
		db.close();
		dbHelper.close();
		return posts;
	}

	public ArrayList<PostData> getAllFavoritePosts() {
		ArrayList<PostData> posts = new ArrayList<PostData>();
		dbHelper = new DatabaseHelper(mContext);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = db.query("favorites", null, null, null, null, null, null);
		if (c != null) {
			if (c.getCount() > 0) {
				c.moveToFirst();
				do {
					PostData post = new PostData();
					post.setTitle(c.getString(1));
					post.setDescription(c.getString(2));
					post.setPubDate(c.getString(3));
					post.setLink(c.getString(4));
					post.setUrlImage(c.getString(5));
					post.setCategory(c.getString(6));
					post.setFavorite(true);
					posts.add(post);
				} while (c.moveToNext());
			}
		}
		c.close();
		db.close();
		dbHelper.close();
		return posts;
	}

	public ArrayList<PostData> getAllPosts() {
		ArrayList<PostData> posts = new ArrayList<PostData>();
		dbHelper = new DatabaseHelper(mContext);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = db.query("posts", null, null, null, null, null, null);
		if (c != null) {
			if (c.getCount() > 0) {
				c.moveToFirst();
				do {
					PostData post = new PostData();
					post.setTitle(c.getString(1));
					post.setDescription(c.getString(2));
					post.setPubDate(c.getString(3));
					post.setLink(c.getString(4));
					post.setUrlImage(c.getString(5));
					post.setFavorite(c.getInt(6) == 1 ? true : false);
					post.setCategory(c.getString(7));
					posts.add(post);
				} while (c.moveToNext());
			}
		}
		c.close();
		db.close();
		dbHelper.close();
		return posts;
	}

	public boolean cleanImages() {
		// TODO Auto-generated method stub
		dbHelper = new DatabaseHelper(mContext);
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		int id = db.delete("images", null, null);

		db.close();
		dbHelper.close();
		return true;
	}

}
