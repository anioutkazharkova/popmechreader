package com.anioutkazharkova.popmechreeder.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anioutkazharkova.popmechreeder.R;
import com.anioutkazharkova.popmechreeder.R.color;
import com.anioutkazharkova.popmechreeder.R.id;
import com.anioutkazharkova.popmechreeder.R.layout;
import com.anioutkazharkova.popmechreeder.common.Utility;
import com.anioutkazharkova.popmechreeder.entities.Category;

public class CategoriesAdapter extends BaseAdapter {

	Context mContext;
	ArrayList<Category> categories;
	LayoutInflater inflater;

	public CategoriesAdapter(Context context) {
		// TODO Auto-generated constructor stub
		mContext = context;
		categories = new ArrayList<Category>();
		inflater = LayoutInflater.from(context);
	}

	public CategoriesAdapter(Context context, ArrayList<Category> list) {
		this(context);
		categories = new ArrayList<Category>(list);
	}
	public ArrayList<Category> getCategories()
	{
		return categories;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return categories.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return categories.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub

		if (view == null) {
			view = inflater.inflate(R.layout.category_layout, null);
		}
		Category currentCategory = categories.get(arg0);

		LinearLayout layout=(LinearLayout)view.findViewById(R.id.mainLayout);
		if (Utility.IS_LIGHT)
		{
			layout.setBackgroundColor(mContext.getResources().getColor(R.color.smockie));
		}
		else
		{
			layout.setBackgroundColor(mContext.getResources().getColor(R.color.dark));
		}
		TextView tvName = (TextView) view.findViewById(R.id.tvCategory);
		if (Utility.IS_LIGHT)
		{
			tvName.setTextColor(mContext.getResources().getColor(R.color.dark));
		}
		else tvName.setTextColor(mContext.getResources().getColor(R.color.clouds));
		ImageView image=(ImageView)view.findViewById(R.id.imCategoryImage);
		tvName.setText(currentCategory.getName());
		if (currentCategory.getImage_name()!=0)
		{
			image.setImageResource(currentCategory.getImage_name());
		}

		return view;
	}

}
