package com.anioutkazharkova.popmechreeder;

import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anioutkazharkova.popmechreeder.common.BigImageDownloadTask;
import com.anioutkazharkova.popmechreeder.common.UpdatePostTask;
import com.anioutkazharkova.popmechreeder.common.Utility;
import com.anioutkazharkova.popmechreeder.entities.PostData;

public class PostActivity extends Activity {

	LinearLayout layout;
	private TextView tvTitle, tvText, tvDate, tvUrl;
	private ImageView imPost;
	PostData currentPost = null;
	MenuItem favItem;
int position=-1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post_activity_layout);
	
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvText = (TextView) findViewById(R.id.tvPostText);
		tvDate = (TextView) findViewById(R.id.tvDate);
		tvUrl = (TextView) findViewById(R.id.tvUrl);
		layout=(LinearLayout)findViewById(R.id.mainLayout);

		imPost = (ImageView) findViewById(R.id.imPostImage);

		currentPost = getIntent().getParcelableExtra("post");
		position=getIntent().getIntExtra("position", -1);
		layout.setBackgroundColor(getResources().getColor(Utility.IS_LIGHT?R.color.smockie:R.color.dark));

		if (currentPost != null) {
			tvTitle.setText(currentPost.getTitle());
			
			tvTitle.setTextColor(getResources().getColor(Utility.IS_LIGHT?R.color.dark:R.color.clouds));
			
			tvText.setTextColor(getResources().getColor(Utility.IS_LIGHT?R.color.dark:R.color.light_secondary));
			tvText.setText(Html.fromHtml(currentPost.getDescription()));
			Linkify.addLinks(tvText, Linkify.ALL);
			tvText.setMovementMethod(LinkMovementMethod.getInstance());
			
			tvDate.setTextColor(getResources().getColor(Utility.IS_LIGHT?R.color.secondary:R.color.light_secondary));

			if (currentPost.getPubDate() != null) {
				tvDate.setText(Utility.getParsedDate(currentPost.getPubDate(),getApplicationContext()));
				/*String regex = "\\d{2} ([a-zA-Z]{3}) \\d{4} \\d{2}\\:\\d{2}\\:\\d{2}";
				Pattern pattern = Pattern.compile(regex);

				Matcher m = pattern.matcher(currentPost.getPubDate());
				if (m.find()) {
					tvDate.setText(m.group(0));
				}*/
			}

			String link = "<a href=\"" + currentPost.getLink()
					+ "\">Перейти на сайт</a>";
			tvUrl.setText(Html.fromHtml(link));
			Linkify.addLinks(tvUrl, Linkify.ALL);
			tvUrl.setMovementMethod(LinkMovementMethod.getInstance());

			
		}

		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
@Override
protected void onResume() {
	// TODO Auto-generated method stub
	super.onResume();
	if (currentPost.getUrlImage() != null) {
		try {
			if (Utility.hasNetworkConnection(this))
			{
		Bitmap bitmap=	new BigImageDownloadTask(imPost, getApplicationContext())
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
							currentPost.getUrlImage()).get();
		
		imPost.setImageBitmap(bitmap);
		bitmap=null;
			}
			else
			{
				imPost.setVisibility(View.GONE);
			}
		} catch (Exception e)
		{
			imPost.setVisibility(View.GONE);
		}
	} else
		imPost.setVisibility(View.GONE);
}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.post_menu, menu);
		favItem = menu.findItem(R.id.itemFavorite);
		if (currentPost!=null)
		{
		if (currentPost.isFavorite()) {
			favItem.setIcon(getResources().getDrawable(R.drawable.favorite_white));
		} else {
			favItem.setIcon(getResources().getDrawable(R.drawable.not_favorite_white));
		}
		}
		//invalidateOptionsMenu();
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub

		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent=new Intent();
			intent.putExtra("position",position);
			setResult(RESULT_OK, intent);
			finish();
			break;
		case R.id.itemFavorite:
			if (currentPost.isFavorite()) {
				removeFromFavorite();
			} else {
				addToFavorite();
			}
			break;
		case R.id.itemShare:
			if (currentPost!=null)
			{
				shareTextUrl();
			}
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

	private void addToFavorite() {
		// TODO Auto-generated method stub
		currentPost.setFavorite(true);
		try {
			new UpdatePostTask(getApplicationContext(), currentPost)
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
							currentPost.getLink()).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		favItem.setIcon(getResources().getDrawable(R.drawable.favorite_white));
		invalidateOptionsMenu();
	}

	 private void shareTextUrl() {
	        Intent share = new Intent(android.content.Intent.ACTION_SEND);
	        share.setType("text/plain");
	        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
	 
	        // Add data to the intent, the receiving app will decide
	        // what to do with it.
	        share.putExtra(Intent.EXTRA_SUBJECT, currentPost.getTitle());
	        share.putExtra(Intent.EXTRA_TEXT, currentPost.getLink());
	 
	        startActivity(Intent.createChooser(share, "Поделиться"));
	    }
	
	private void removeFromFavorite() {
		// TODO Auto-generated method stub
		currentPost.setFavorite(false);
		try {
			new UpdatePostTask(getApplicationContext(), currentPost)
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
							currentPost.getLink()).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		favItem.setIcon(getResources().getDrawable(R.drawable.not_favorite_white));
		invalidateOptionsMenu();
	}
}
