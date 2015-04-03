package com.anioutkazharkova.popmechreeder.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anioutkazharkova.popmechreeder.R;
import com.anioutkazharkova.popmechreeder.R.color;
import com.anioutkazharkova.popmechreeder.R.drawable;
import com.anioutkazharkova.popmechreeder.R.id;
import com.anioutkazharkova.popmechreeder.R.layout;
import com.anioutkazharkova.popmechreeder.common.UpdatePostTask;
import com.anioutkazharkova.popmechreeder.common.Utility;
import com.anioutkazharkova.popmechreeder.entities.PostData;

public class PostDataAdapter extends BaseAdapter {

	Context mContext;
	LayoutInflater inflater;
	private ArrayList<PostData> posts;

	public PostDataAdapter(Context context) {
		this.mContext = context;
		inflater = LayoutInflater.from(context);
		posts = new ArrayList<PostData>();
	}

	public PostDataAdapter(Context context, List<PostData> temp) {
		this(context);
		if (temp != null) {
			this.posts = new ArrayList<PostData>(temp);
		}
	}
	public ArrayList<PostData> getPosts()
	{
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
		// TODO Auto-generated method stub
		final ViewHolder holder;
		 PostData post = posts.get(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.post_layout, null);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView.findViewById(R.id.imPost);
			holder.text = (TextView) convertView.findViewById(R.id.tvPostText);
			holder.image.setTag(post.getUrlImage());
			holder.imageURL = (post.getUrlImage());
			holder.favorite=(ImageButton)convertView.findViewById(R.id.imFavorite);
			holder.favorite.setTag(position);
			holder.date=(TextView)convertView.findViewById(R.id.tvDate);
			holder.category=(TextView)convertView.findViewById(R.id.tvCategory);
			holder.layout=(LinearLayout)convertView.findViewById(R.id.mainLayout);
			holder.favorite.setFocusable(false);
			holder.favorite.setClickable(false);
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
						holder.favorite.setImageResource((Utility.IS_LIGHT)?R.drawable.favorite_grey:R.drawable.favorite_white);
					}
					else
					{
						holder.favorite.setImageResource((Utility.IS_LIGHT)?R.drawable.not_favorite_grey:R.drawable.not_favorite_white);
					}
					ToAwayFavorite(post, !post.isFavorite());
				}
			});
		}
		
		
		

		if (Utility.IS_LIGHT)
		{
			holder.layout.setBackgroundColor(mContext.getResources().getColor(R.color.smockie));
		}
		else
		{
			holder.layout.setBackgroundColor(mContext.getResources().getColor(R.color.dark));
		}
		if (Utility.IS_LIGHT)
		holder.text.setTextColor(mContext.getResources().getColor(R.color.secondary));
		else
			holder.text.setTextColor(mContext.getResources().getColor(R.color.light_secondary));
		Spannable span = new SpannableString(post.getTitle() + ": "
				+ post.getDescription());
		span.setSpan(new StyleSpan(Typeface.BOLD), 0,
				(post.getTitle().length() + 2),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		if (Utility.IS_LIGHT)
		{
		span.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.dark)), 0, (post.getTitle()
				.length() + 2), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		else
		{
			span.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.clouds)), 0, (post.getTitle()
					.length() + 2), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		holder.text.setText(span);
		
		if (post.getCategory()!="")
			holder.category.setText(post.getCategory());
		
		if (post.getPubDate()!="")
		{
		String resDate=Utility.getParsedDate(post.getPubDate(),mContext);
		holder.date.setText(resDate);
		}
		
		if (post.isFavorite())
		{
			holder.favorite.setImageResource((Utility.IS_LIGHT)?R.drawable.favorite_grey:R.drawable.favorite_white);
		}
		else
		{
			holder.favorite.setImageResource((Utility.IS_LIGHT)?R.drawable.not_favorite_grey:R.drawable.not_favorite_white);
		}
if (Utility.IS_SHOW_IMAGES)
{
		if (post.getUrlImage() != null) {
			holder.image.setVisibility(View.VISIBLE);
			if (post.getImageBitmap() != null) {
				try {
					holder.image.setImageBitmap(post.getImageBitmap());
				} catch (Exception e) {

				}
			} else
				{holder.image.setImageDrawable(mContext.getResources()
						.getDrawable(R.drawable.default_image));
				//holder.image.setVisibility(View.GONE);
				}
		} else {
			holder.image.setVisibility(View.GONE);

		}
}
else
holder.image.setVisibility(View.GONE);
		
		

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
		public Bitmap bitmap;
		public String imageURL;
		public ImageView image;
		public ImageButton favorite;
		public TextView text,date,category;
		public LinearLayout layout;
	}

}
