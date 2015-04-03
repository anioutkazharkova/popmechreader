package com.anioutkazharkova.popmechreeder.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.anioutkazharkova.popmechreeder.R;
import com.anioutkazharkova.popmechreeder.R.color;
import com.anioutkazharkova.popmechreeder.R.drawable;
import com.anioutkazharkova.popmechreeder.R.id;
import com.anioutkazharkova.popmechreeder.R.layout;
import com.anioutkazharkova.popmechreeder.adapters.PostDataAdapter.ViewHolder;
import com.anioutkazharkova.popmechreeder.common.UpdatePostTask;
import com.anioutkazharkova.popmechreeder.common.Utility;
import com.anioutkazharkova.popmechreeder.entities.PostData;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PostGridAdapter extends BaseAdapter {

	Context mContext;
	LayoutInflater inflater;
	private ArrayList<PostData> posts;

	public PostGridAdapter(Context context) {
		this.mContext = context;
		inflater = LayoutInflater.from(context);
		posts = new ArrayList<PostData>();
	}

	public PostGridAdapter(Context context, List<PostData> temp) {
		this(context);
		if (temp != null) {
			this.posts = new ArrayList<PostData>(temp);
		}
	}

	public ArrayList<PostData> getPosts() {
		return posts;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return posts.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return posts.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		PostData post = posts.get(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.post_grid_item, null);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView
					.findViewById(R.id.imPostImage);
			holder.text = (TextView) convertView.findViewById(R.id.tvText);
			holder.image.setTag(post.getUrlImage());
			holder.imageURL = (post.getUrlImage());
			holder.date = (TextView) convertView.findViewById(R.id.tvDate);
			holder.favorite=(ImageButton)convertView.findViewById(R.id.imFavorite);
			holder.favorite.setTag(position);
			holder.favorite.setClickable(false);
			holder.favorite.setFocusable(false);
			holder.layout = (LinearLayout) convertView
					.findViewById(R.id.mainLayout);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (!holder.favorite.hasOnClickListeners())
		{
			holder.favorite.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int ind=(Integer) holder.favorite.getTag();
					PostData post=posts.get(ind);
					if (post.isFavorite())
					{
						holder.favorite.setImageResource(R.drawable.favorite_white);
					}
					else
					{
						holder.favorite.setImageResource(R.drawable.not_favorite_white);
					}
					ToAwayFavorite(post, !post.isFavorite());
				}
			});
		}
		if (Utility.IS_LIGHT) {
			holder.layout.setBackgroundColor(mContext.getResources().getColor(
					R.color.smockie));
		} else {
			holder.layout.setBackgroundColor(mContext.getResources().getColor(
					R.color.dark));
		}
		
			holder.text.setTextColor(mContext.getResources().getColor(
					R.color.light_secondary));
		Spannable span = new SpannableString(post.getTitle());
		span.setSpan(new StyleSpan(Typeface.BOLD), 0,
				(post.getTitle().length()), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		
			span.setSpan(new ForegroundColorSpan(mContext.getResources()
					.getColor(R.color.clouds)), 0, (post.getTitle().length()),
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		holder.text.setText(span);

		if (post.isFavorite())
		{
			holder.favorite.setImageResource(R.drawable.favorite_white);
		}
		else
		{
			holder.favorite.setImageResource(R.drawable.not_favorite_white);
		}

		if (post.getPubDate() != "") {
			String resDate = Utility.getParsedDate(post.getPubDate(), mContext);
			holder.date.setText(resDate);
			holder.date.setTextColor(mContext.getResources().getColor(R.color.smockie));
		}

		if (post.getUrlImage() != null) {
			holder.image.setVisibility(View.VISIBLE);
			if (post.getImageBitmap() != null) {
				try {
					holder.image.setImageBitmap(post.getImageBitmap());
				} catch (Exception e) {

				}
			} else {
				holder.image.setImageDrawable(mContext.getResources()
						.getDrawable(R.drawable.default_image));
				// holder.image.setVisibility(View.GONE);
			}
		} else {
			holder.image.setVisibility(View.GONE);

		}

		return convertView;
	}
	private void ToAwayFavorite(PostData post,boolean isFavorite) {
		// TODO Auto-generated method stub
		post.setFavorite(isFavorite);
		try {
			new UpdatePostTask(mContext, post)
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
							post.getLink()).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		notifyDataSetInvalidated();
	}

	class ViewHolder {
		public ImageButton favorite;
		public Bitmap bitmap;
		public String imageURL;
		public ImageView image;
		public TextView text, date;
		public LinearLayout layout;
	}
}
