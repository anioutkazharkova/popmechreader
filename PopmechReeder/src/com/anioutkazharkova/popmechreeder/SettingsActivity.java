package com.anioutkazharkova.popmechreeder;

import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.anioutkazharkova.popmechreeder.common.Utility;
import com.anioutkazharkova.popmechreeder.services.DatabaseWorker;

public class SettingsActivity extends Activity {

	LinearLayout mainLayout,layoutMode;
	private Button btnCache, btnFavorite;
	CheckBox chbTheme;
	 TextView themeLabel,themeTurnLabel,cleanLabel,cleanCacheLabel,cleanFavLabel,tvMode,showImagesLabel;
	ToggleButton chMode;
	private CheckBox chbImages;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_layout);

		mainLayout=(LinearLayout)findViewById(R.id.mainLayout);
		layoutMode=(LinearLayout)findViewById(R.id.layoutMode);
		
		chMode=(ToggleButton)findViewById(R.id.chkState);		
		
		chbTheme=(CheckBox)findViewById(R.id.chbTheme);
		chbImages=(CheckBox)findViewById(R.id.chbImages);
		
		
		btnCache = (Button) findViewById(R.id.btnCleanCache);
		btnFavorite = (Button) findViewById(R.id.btnCleanFav);

		themeLabel=(TextView)findViewById(R.id.themeLabel);
		themeTurnLabel=(TextView)findViewById(R.id.themeTurnLabel);
		cleanLabel=(TextView)findViewById(R.id.cleanLabel);
		cleanCacheLabel=(TextView)findViewById(R.id.cleanCacheLabel);
		cleanFavLabel=(TextView)findViewById(R.id.cleanFavLabel);
		showImagesLabel=(TextView)findViewById(R.id.showImagesLabel);
		tvMode=(TextView)findViewById(R.id.tvMode);
		
		chbTheme.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				Utility.IS_LIGHT=!isChecked;
				setTheme(Utility.IS_LIGHT);
			}
		});
		
		chMode.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				Utility.IS_LIST=!isChecked;
			}
		});
		chbImages.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				Utility.IS_SHOW_IMAGES=isChecked;
				if (!isChecked)
				{
					Utility.IS_LIST=true;
				}
				layoutMode.setEnabled(isChecked);
				themeTurnLabel.setEnabled(isChecked);
				chMode.setEnabled(isChecked);
			}
		});
	
		
		btnCache.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					boolean res = new RemoveCacheTask(getApplicationContext())
							.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
							.get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		btnFavorite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					boolean res = new RemoveFavoriteTask(
							getApplicationContext()).executeOnExecutor(
							AsyncTask.THREAD_POOL_EXECUTOR).get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		chbTheme.setChecked(!Utility.IS_LIGHT);
		chMode.setChecked(!Utility.IS_LIST);
		chbImages.setChecked(Utility.IS_SHOW_IMAGES);
		setTheme(Utility.IS_LIGHT);

		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void setTheme(boolean isLight)
	{
		if (isLight)
		{
			mainLayout.setBackgroundColor(getResources().getColor(R.color.smockie));
			themeLabel.setTextColor(getResources().getColor(R.color.dark));
			themeTurnLabel.setTextColor(getResources().getColor(R.color.dark));
			cleanLabel.setTextColor(getResources().getColor(R.color.dark));
			cleanCacheLabel.setTextColor(getResources().getColor(R.color.dark));
			cleanFavLabel.setTextColor(getResources().getColor(R.color.dark));
			tvMode.setTextColor(getResources().getColor(R.color.dark));
			showImagesLabel.setTextColor(getResources().getColor(R.color.dark));
			
			btnCache.setTextColor(getResources().getColor(R.color.dark));
			btnFavorite.setTextColor(getResources().getColor(R.color.dark));
		
		}
		else
		{
			mainLayout.setBackgroundColor(getResources().getColor(R.color.dark));
			themeLabel.setTextColor(getResources().getColor(R.color.clouds));
			themeTurnLabel.setTextColor(getResources().getColor(R.color.clouds));
			cleanLabel.setTextColor(getResources().getColor(R.color.clouds));
			cleanCacheLabel.setTextColor(getResources().getColor(R.color.clouds));
			cleanFavLabel.setTextColor(getResources().getColor(R.color.clouds));
			tvMode.setTextColor(getResources().getColor(R.color.clouds));
			showImagesLabel.setTextColor(getResources().getColor(R.color.clouds));
			btnCache.setTextColor(getResources().getColor(R.color.clouds));
			btnFavorite.setTextColor(getResources().getColor(R.color.clouds));
		}
	}
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent=new Intent();
			setResult(RESULT_OK, intent);
			finish();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent intent=new Intent();
		setResult(RESULT_OK, intent);
		finish();
	}
	private class RemoveFavoriteTask extends AsyncTask<Void, Void, Boolean> {
		DatabaseWorker dbWorker;

		public RemoveFavoriteTask() {

		}

		public RemoveFavoriteTask(Context context) {
			dbWorker = new DatabaseWorker(context);

		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			return dbWorker.cleanFavorites();
		}
		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			Toast.makeText(getApplicationContext(), "Архив очищен",Toast.LENGTH_LONG).show();
		}

	}

	private class RemoveCacheTask extends AsyncTask<Void, Void, Boolean> {
		DatabaseWorker dbWorker;

		public RemoveCacheTask() {

		}

		public RemoveCacheTask(Context context) {
			dbWorker = new DatabaseWorker(context);

		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			return dbWorker.cleanAll();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			Toast.makeText(getApplicationContext(), "Кэш очищен",Toast.LENGTH_LONG).show();
		}
	}

}
