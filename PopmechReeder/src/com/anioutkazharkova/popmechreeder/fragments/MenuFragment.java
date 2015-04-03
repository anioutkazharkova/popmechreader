package com.anioutkazharkova.popmechreeder.fragments;

import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.anioutkazharkova.popmechreeder.PopmechMainActivity;
import com.anioutkazharkova.popmechreeder.R;
import com.anioutkazharkova.popmechreeder.R.array;
import com.anioutkazharkova.popmechreeder.R.drawable;
import com.anioutkazharkova.popmechreeder.R.id;
import com.anioutkazharkova.popmechreeder.R.layout;
import com.anioutkazharkova.popmechreeder.adapters.CategoriesAdapter;
import com.anioutkazharkova.popmechreeder.entities.Category;

public class MenuFragment extends Fragment {

	private ListView listView;
	private CategoriesAdapter mAdapter;
	private ArrayList<Category> categories = new ArrayList<Category>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.menu_fragment_layout, null);
		listView = (ListView) view.findViewById(R.id.lvMenu);
		mAdapter = new CategoriesAdapter(getActivity());
		listView.setAdapter(mAdapter);

		prepareCategories();

		mAdapter = new CategoriesAdapter(getActivity(), categories);
		listView.setAdapter(mAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				((PopmechMainActivity) getActivity()).onCategoryChange(
						categories.get(position), position);
			}
		});
		return view;
	}

	public void resetTheme()
	{
		
		mAdapter=new CategoriesAdapter(getActivity(), mAdapter.getCategories());
		listView.setAdapter(mAdapter);
	}
	
	private void prepareCategories() {
		/*Category settings = new Category();
		settings.setName(getActivity().getResources().getString(
				R.string.set_category));
		settings.setImage_name(R.drawable.settings);
		categories.add(settings);
		*/

		

		String[] links = getActivity().getResources().getStringArray(
				R.array.links);
		String[] catNames = getActivity().getResources().getStringArray(
				R.array.categories);
		Category favorite = new Category();
		favorite.setImage_name(R.drawable.favorite);
		favorite.setName(catNames[0]);
		categories.add(favorite);
		for (int i = 1; i < links.length; i++) {
			Category c = new Category();
			c.setName(catNames[i]);
			c.setUrl(links[i]);

			categories.add(c);
		}
	}
}
