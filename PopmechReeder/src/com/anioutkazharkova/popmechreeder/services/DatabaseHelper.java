package com.anioutkazharkova.popmechreeder.services;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static String database = "popmechdata.db";
	private static int Version = 2;

	public DatabaseHelper(Context context) {
		super(context, database, null, Version);
	}

	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, database, factory, Version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table posts ("
				+ "id integer primary key autoincrement," + "title text,"
				+ "description text," + "date text," + "link text,"
				+ "image text,"  + "favorite integer,"+  "category text);");

		db.execSQL("create table favorites ("
				+ "id integer primary key autoincrement," + "title text,"
				+ "description text," + "date text," + "link text,"
				+ "image text, "+"category text);");

		db.execSQL("create table images ("
				+ "id integer primary key autoincrement," + "image blob,"
				+ "url text " + ");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
