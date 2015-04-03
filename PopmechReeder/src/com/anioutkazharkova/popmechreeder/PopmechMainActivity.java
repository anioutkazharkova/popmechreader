package com.anioutkazharkova.popmechreeder;

import java.util.concurrent.ExecutionException;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.anioutkazharkova.popmechreeder.common.Utility;
import com.anioutkazharkova.popmechreeder.entities.Category;
import com.anioutkazharkova.popmechreeder.fragments.MenuFragment;
import com.anioutkazharkova.popmechreeder.fragments.PostsMainFragment;
import com.anioutkazharkova.popmechreeder.services.DatabaseWorker;

public class PopmechMainActivity extends Activity implements CategoryListener {
	android.support.v4.widget.SlidingPaneLayout pane;
private boolean isChanged=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_slider_layout);
		readPreferences();
		pane = (android.support.v4.widget.SlidingPaneLayout) findViewById(R.id.sp);
		pane.setPanelSlideListener(new PaneListener());
		pane.setSliderFadeColor(getResources().getColor(
				android.R.color.transparent));

		if (!pane.isSlideable()) {
			getFragmentManager().findFragmentById(R.id.leftpane)
					.setHasOptionsMenu(false);
			getFragmentManager().findFragmentById(R.id.rightpane)
					.setHasOptionsMenu(true);
		}
	}

	private class PaneListener implements
			android.support.v4.widget.SlidingPaneLayout.PanelSlideListener {
		@Override
		public void onPanelClosed(View view) {
			System.out.println("Panel closed");
			getFragmentManager().findFragmentById(R.id.leftpane)
					.setHasOptionsMenu(false);
			getFragmentManager().findFragmentById(R.id.rightpane)
					.setHasOptionsMenu(true);
			PostsMainFragment fragment = (PostsMainFragment) getFragmentManager()
					.findFragmentById(R.id.rightpane);

			if (!isChanged)
			{
			if (fragment != null) {
				fragment.turnOnMenu(-1);
			}
			}
			isChanged=false;
		}

		@Override
		public void onPanelOpened(View view) {
			System.out.println("Panel opened");
			getFragmentManager().findFragmentById(R.id.leftpane)
					.setHasOptionsMenu(true);
			getFragmentManager().findFragmentById(R.id.rightpane)
					.setHasOptionsMenu(false);
			turnOff();
		}

		@Override
		public void onPanelSlide(View view, float arg1) {
			System.out.println("Panel sliding");
		}
	}

	public void turnOff() {

		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		getActionBar().setDisplayShowTitleEnabled(true);
		getActionBar().setTitle(getResources().getString(R.string.app_name));
	}

	@Override
	public void onCategoryChange(Category category, int position) {
		// TODO Auto-generated method stub
		// Toast.makeText(getApplicationContext(), category.getName(),
		// Toast.LENGTH_LONG).show();
		
			PostsMainFragment fragment = (PostsMainFragment) getFragmentManager()
					.findFragmentById(R.id.rightpane);

			if (fragment != null) {
				fragment.turnOnMenu(position);
				//fragment.setStartContent(category);
				
			}
			isChanged=true;
			pane.closePane();
		/* else {
			if (position == 1) {
				PostsMainFragment fragment = (PostsMainFragment) getFragmentManager()
						.findFragmentById(R.id.rightpane);

				if (fragment != null) {
					
					fragment.setFavorites(category);
				}
				pane.closePane();
			}*/
		//}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (data == null)
			return;
		if (requestCode == 2345) {	
			savePreferences();
			PostsMainFragment postFragment = (PostsMainFragment) getFragmentManager()
					.findFragmentById(R.id.rightpane);

			if (postFragment != null) {

				postFragment.setStartContent(null,false);
				//postFragment.resetTheme();
				
			}
			MenuFragment menuFragment=(MenuFragment)getFragmentManager().findFragmentById(R.id.leftpane);
			if (menuFragment!=null)
			{
				menuFragment.resetTheme();
			}
		}
		else
		{
			if (requestCode==1234)
			{
				int position=data.getIntExtra("position",-1);
				PostsMainFragment postFragment = (PostsMainFragment) getFragmentManager()
						.findFragmentById(R.id.rightpane);

				if (postFragment != null) {

					//postFragment.setStartContent(null,true);
					postFragment.changeFavorite(position);
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		savePreferences();
		class CleanImages extends AsyncTask<Void,Void,Boolean>
		{
			DatabaseWorker dbWorker;
			public CleanImages()
			{
				dbWorker=new DatabaseWorker(getApplicationContext());
			}
			@Override
			protected Boolean doInBackground(Void... params) {
				// TODO Auto-generated method stub
				return dbWorker.cleanImages();
			}
			
		}
		try {
			boolean res= new CleanImages().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onDestroy();
	}
	
	private void readPreferences()
	{
		SharedPreferences preferences=getSharedPreferences(Utility.PREFERENCES, MODE_PRIVATE);
		Utility.IS_LIGHT= preferences.getBoolean(Utility.THEME_PREF,Utility.IS_LIGHT);
		Utility.IS_LIST=preferences.getBoolean(Utility.MODE_PREF, Utility.IS_LIST);
		Utility.IS_SHOW_IMAGES=preferences.getBoolean(Utility.SHOW_IMAGES_PREF, Utility.IS_SHOW_IMAGES);
	}
	
	private void savePreferences()
	{
		SharedPreferences preferences=getSharedPreferences(Utility.PREFERENCES, MODE_PRIVATE);
		Editor edit=preferences.edit();
		edit.putBoolean(Utility.THEME_PREF,Utility.IS_LIGHT);
		edit.putBoolean(Utility.MODE_PREF,Utility.IS_LIST);
		edit.putBoolean(Utility.SHOW_IMAGES_PREF, Utility.IS_SHOW_IMAGES);
		edit.apply();
	}

}
