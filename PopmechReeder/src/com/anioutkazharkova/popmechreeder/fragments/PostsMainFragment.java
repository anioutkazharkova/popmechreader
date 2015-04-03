package com.anioutkazharkova.popmechreeder.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.YuvImage;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.anioutkazharkova.popmechreeder.PostActivity;
import com.anioutkazharkova.popmechreeder.R;
import com.anioutkazharkova.popmechreeder.SettingsActivity;
import com.anioutkazharkova.popmechreeder.R.array;
import com.anioutkazharkova.popmechreeder.R.color;
import com.anioutkazharkova.popmechreeder.R.id;
import com.anioutkazharkova.popmechreeder.R.layout;
import com.anioutkazharkova.popmechreeder.R.menu;
import com.anioutkazharkova.popmechreeder.R.string;
import com.anioutkazharkova.popmechreeder.adapters.PostDataAdapter;
import com.anioutkazharkova.popmechreeder.adapters.PostGridAdapter;
import com.anioutkazharkova.popmechreeder.common.CachedPostsTask;
import com.anioutkazharkova.popmechreeder.common.Configuration;
import com.anioutkazharkova.popmechreeder.common.GetFavoritesTask;
import com.anioutkazharkova.popmechreeder.common.ImageDownloaderTask;
import com.anioutkazharkova.popmechreeder.common.RssDataController;
import com.anioutkazharkova.popmechreeder.common.Utility;
import com.anioutkazharkova.popmechreeder.entities.Category;
import com.anioutkazharkova.popmechreeder.entities.PostData;

public class PostsMainFragment extends Fragment implements OnRefreshListener {
	private ListView listView;
	PostDataAdapter mAdapter;
	ArrayList<PostData> list = new ArrayList<PostData>();
	int firstItem = 0;
	int lastItem = 0;
	int shift = 0;
	List<PostData> temp;
	// loadDialogFragment //loadDialog= new //loadDialogFragment();
	Context mContext;
	Category currentCategory = null;
	int currentPosition = -1;
	String[] links, cats;
	private ProgressDialog progress;
	private SwipeRefreshLayout swipeLayout;
	private boolean isRefreshing = false;
	Handler handler = new Handler();
	private LinearLayout mainLayout;
	private PostGridAdapter gdAdapter;
	private int currentListPosition=-1;
	private int previousPosition;
	private boolean isFound;
	private ArrayList<PostData> foundPosts=new ArrayList<PostData>();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View view = inflater.inflate(R.layout.main_activity, null);
		mainLayout = (LinearLayout) view.findViewById(R.id.mainLayout);
		listView = (ListView) view.findViewById(R.id.lvPosts);

		mAdapter = new PostDataAdapter(this.getActivity());
		gdAdapter = new PostGridAdapter(getActivity());

		// gridView.setAdapter(gdAdapter);

		mContext = this.getActivity().getApplicationContext();

		swipeLayout = (SwipeRefreshLayout) view
				.findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(this);

		mainLayout.setBackgroundColor(getActivity().getResources().getColor(
				Utility.IS_LIGHT ? R.color.smockie : R.color.dark));
		swipeLayout.setColorSchemeResources(R.color.turbo_yellow,
				R.color.sunflower, R.color.orange, R.color.carrot,
				R.color.pumpkin);

		progress = new ProgressDialog(this.getActivity());
		progress.setMessage(mContext.getResources().getString(R.string.loading));
		progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progress.setCancelable(false);
		progress.setIndeterminate(true);

		links = getActivity().getResources().getStringArray(R.array.links);
		cats = getActivity().getResources().getStringArray(R.array.categories);

		if (currentCategory == null) {
			currentCategory = new Category();
			currentCategory.setUrl(links[1]);
			currentCategory.setName(cats[1]);
		}

		turnOnMenu(1);
		setStartContent(currentCategory,false);

		listView.setOnItemClickListener(new OnItemClickListener() {

			

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				PostData p = isFound?foundPosts.get(position):list.get(position);
				Intent postIntent = new Intent(getActivity(),
						PostActivity.class);
				postIntent.putExtra("post", p);
				postIntent.putExtra("position", position);
				getActivity().startActivityForResult(postIntent, 1234);
			}
		});
		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				firstItem = firstVisibleItem;
				if (firstItem > lastItem) {

					if (listView.getLastVisiblePosition() == (Utility.IS_LIST?(mAdapter
							.getCount() - 1):(gdAdapter.getCount()-1)) && !isFound) {
						progress.show();
						currentListPosition=listView.getLastVisiblePosition();
						if (list != null && list.size() > 0 && temp != null
								&& temp.size() >= 10) {

							// List<PostData>
							if (shift < list.size()) {
								// loadDialog.show(getFragmentManager(),
								// "dialog");

								temp = list.subList(0,
										Math.min(10 + shift, list.size()));
								if (Utility.IS_LIST || !Utility.IS_SHOW_IMAGES) {
									mAdapter = new PostDataAdapter(mContext,
											temp);
									listView.setAdapter(mAdapter);
								} else {
									gdAdapter = new PostGridAdapter(mContext,
											temp);
									listView.setAdapter(gdAdapter);
								}
								if (shift + 10 <= list.size()) {
									shift += 10;
								} else {
									shift = list.size();
								}
								if (Utility.IS_SHOW_IMAGES)
								{
								int i = 0;
								do {
									final PostData p = temp.get(i);
									handler.postDelayed(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub
											if (Utility
													.hasNetworkConnection(mContext)) {
												if (p.getImageBitmap() == null) {
													try {
														p.setImageBitmap(new ImageDownloaderTask(
																progress,
																mContext)
																.executeOnExecutor(
																		AsyncTask.SERIAL_EXECUTOR,
																		p.getUrlImage())
																.get());

														updateCompanyView(p,
																true);

													} catch (InterruptedException e) {
														// TODO Auto-generated
														// catch
														// block
														e.printStackTrace();
													} catch (ExecutionException e) {
														// TODO Auto-generated
														// catch
														// block
														e.printStackTrace();
													}
												}
											}
										}
									}, (i + 1) * 50);
									i += 1;
								} while (i < temp.size());
								}
								// loadDialog.Finish();

								listView.setSelection(currentListPosition);
							}

						}
						progress.dismiss();
					}
				}
				lastItem = firstItem;
			}
		});
		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	public void resetTheme() {
		mainLayout.setBackgroundColor(getActivity().getResources().getColor(
				Utility.IS_LIGHT ? R.color.smockie : R.color.dark));
		if (Utility.IS_LIST||!Utility.IS_SHOW_IMAGES)
		{
		ArrayList<PostData> posts = mAdapter.getPosts();
		mAdapter = new PostDataAdapter(mContext, posts);
		listView.setAdapter(mAdapter);
		}
		else
		{
			ArrayList<PostData> posts = gdAdapter.getPosts();
			gdAdapter = new PostGridAdapter(mContext, posts);
			listView.setAdapter(gdAdapter);
		}
	}

	public void setFavorites(Category category, boolean savePosition) {
		if (category != null)
			currentCategory = category;
		try {
			list = new GetFavoritesTask(mContext, progress).executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list != null && list.size() > 0) {
			setFirstPosts(savePosition);
		} else {
			mAdapter = new PostDataAdapter(mContext);
			listView.setAdapter(mAdapter);
		}
	}

	public void setStartContent() {

		// loadDialog.show(getFragmentManager(), "dialog");
		try {
			if (currentCategory != null) {
				list = new CachedPostsTask(mContext).executeOnExecutor(
						AsyncTask.THREAD_POOL_EXECUTOR,
						currentCategory.getName()).get();
			} else
				list = new CachedPostsTask(mContext).executeOnExecutor(
						AsyncTask.THREAD_POOL_EXECUTOR).get();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (list != null && list.size() > 0) {
			setFirstPosts(false);
		} else {
			if (Utility.hasNetworkConnection(mContext)) {
				getNewPosts();
			}
		}
	}

	public void setStartContent(final Category category,boolean savePosition) {
		// Toast.makeText(getActivity(), "Ask by category" + category.getName(),
		// Toast.LENGTH_LONG).show();
		
		shift = 0;
		if (category != null)
			currentCategory = category;
		if (!isFound)
		{
		if (currentPosition > 0) {
			progress.show();
			// loadDialog.show(getFragmentManager(), "dialog");

			try {
				list = new CachedPostsTask(mContext, currentCategory.getName(),
						progress).executeOnExecutor(
						AsyncTask.THREAD_POOL_EXECUTOR,
						currentCategory.getName()).get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// //loadDialog.dismiss();
			if (list != null && list.size() > 0) {
				setFirstPosts(savePosition);
			} else {
				if (Utility.hasNetworkConnection(mContext)) {
					getNewPostsByCategory(currentCategory);
					// Toast.makeText(getActivity(), "new posts",
					// Toast.LENGTH_LONG)
					// .show();
				}
			}
		} else
			setFavorites(category,savePosition);
		}
	}

	public void updateCompanyView(PostData post) {
		int position = firstItem;
		// Toast.makeText(getApplicationContext(), "update",
		// Toast.LENGTH_LONG).show();
		boolean flag = false;
		for (PostData p : temp) {
			if (p.getLink() == post.getLink()
					&& p.getUrlImage() == post.getUrlImage()) {
				flag = true;
				p = post;
			}

		}
		if (flag) {

			mAdapter = new PostDataAdapter(mContext, temp);
			listView.setAdapter(mAdapter);
			listView.setSelection(position);

		}

		listView.invalidateViews();
	}

	public void updateCompanyView(PostData post, boolean withSelect) {
		int position = firstItem;
		// Toast.makeText(getApplicationContext(), "update",
		// Toast.LENGTH_LONG).show();
		boolean flag = false;
		for (PostData p : temp) {
			if (p.getLink() == post.getLink()
					&& p.getUrlImage() == post.getUrlImage()) {
				flag = true;
				p = post;
			}

		}
		if (flag) {
			

				if (Utility.IS_LIST || !Utility.IS_SHOW_IMAGES) {
					mAdapter = new PostDataAdapter(mContext, temp);
					listView.setAdapter(mAdapter);
					listView.setSelection(position);
				} else {
					gdAdapter = new PostGridAdapter(mContext, temp);
					listView.setAdapter(gdAdapter);
					listView.setSelection(position);
				}
		

		}
		

			listView.invalidateViews();

		
	}

	private void getNewPosts() {

		// //loadDialog.show(getFragmentManager(), "dialog");
		try {
			list = new RssDataController(mContext).executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, (Configuration.URL)).get();
			setFirstPosts(false);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void turnOnMenu(final int position) {
previousPosition=currentPosition;
		if (position != -1) {
			currentPosition = position;
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity()
				.getActionBar().getThemedContext(),
				android.R.layout.simple_spinner_dropdown_item, cats);

		getActivity().getActionBar().setNavigationMode(
				ActionBar.NAVIGATION_MODE_LIST);
		getActivity().getActionBar().setDisplayShowTitleEnabled(false);

		ActionBar.OnNavigationListener navigationListener = new OnNavigationListener() {

			@Override
			public boolean onNavigationItemSelected(int itemPosition,
					long itemId) {
				if (itemPosition > 0 && previousPosition!=itemPosition) {

					currentCategory.setUrl(links[itemPosition]);
					currentCategory.setName(cats[itemPosition]);

					handler.removeCallbacksAndMessages(null);
					progress.show();
					handler.postDelayed(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							
							setStartContent(currentCategory,false);
						}
					},200);

				} else if (itemPosition == 0) {
					currentCategory.setName(cats[itemPosition]);
					progress.show();
					handler.postDelayed(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							setFavorites(currentCategory,false);
						}
					},200);

				}
				currentPosition = itemPosition;
				return true;
			}
		};

		getActivity().getActionBar().setListNavigationCallbacks(adapter,
				navigationListener);
	
		getActivity().getActionBar().setSelectedNavigationItem(currentPosition);

	}

	public void getNewPostsByCategory(Category category) {

		// //loadDialog.show(getFragmentManager(), "dialog");
		currentCategory = category;
		try {
			list = new RssDataController(mContext).executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, category.getUrl(),
					category.getName()).get();
			setFirstPosts(false);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setFirstPosts(boolean savePosition) {
		if (list != null && list.size() > 0) {
			// List<PostData>
			isFound=false;
			shift = 0;
			 currentListPosition=0;

			temp = list.subList(0, Math.min(list.size(), 10));
			if (Utility.IS_LIST || !Utility.IS_SHOW_IMAGES) {

				mAdapter = new PostDataAdapter(mContext, temp);
				listView.setAdapter(mAdapter);
				listView.setSelection(currentListPosition);
			} else {

				gdAdapter = new PostGridAdapter(mContext, temp);
				listView.setAdapter(gdAdapter);
				listView.setSelection(currentListPosition);

			}
			shift += temp.size();
if (Utility.IS_SHOW_IMAGES)
{
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					for (PostData p : temp) {
						if (Utility.hasNetworkConnection(mContext)) {
							if (p.getImageBitmap() == null) {
								try {
									p.setImageBitmap(new ImageDownloaderTask(
											progress, mContext)
											.executeOnExecutor(
													AsyncTask.THREAD_POOL_EXECUTOR,
													p.getUrlImage()).get());
									updateCompanyView(p, true);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (ExecutionException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}
						}
					}
				}
			}, 500);
}
		}
		if (isRefreshing) {
			isRefreshing = false;
			swipeLayout.setRefreshing(isRefreshing);
		} else
			progress.dismiss();
		// loadDialog.Finish();

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		inflater.inflate(R.menu.popmech_main, menu);
		MenuItem searchItem=menu.findItem(R.id.action_search);
		SearchView mSearchView = (SearchView) searchItem.getActionView();
		mSearchView.setOnQueryTextListener(new OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub
				if (query!="")
				{
					searchQuery(query);
				}
				return true;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				return false;
			}
		});
	}

	private void searchQuery(String query)
	{
		if (list!=null && list.size()>0)
		{
		 foundPosts=new ArrayList<PostData>();
		for(PostData p: list)
		{
			if (p.getTitle().toLowerCase().contains(query.toLowerCase()))
			{
				foundPosts.add(p);
			}
		}
		isFound=true;
		if (Utility.IS_LIST||!Utility.IS_SHOW_IMAGES)
		{
		mAdapter=new PostDataAdapter(mContext, foundPosts);
		listView.setAdapter(mAdapter);
		}
		else
		{
			gdAdapter=new PostGridAdapter(mContext, foundPosts);
			listView.setAdapter(gdAdapter);
		}
		if (Utility.IS_SHOW_IMAGES)
		{
					handler.postDelayed(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							for (PostData p : foundPosts) {
								if (Utility.hasNetworkConnection(mContext)) {
									if (p.getImageBitmap() == null) {
										try {
											p.setImageBitmap(new ImageDownloaderTask(
													progress, mContext)
													.executeOnExecutor(
															AsyncTask.THREAD_POOL_EXECUTOR,
															p.getUrlImage()).get());
											updateCompanyView(p, true);
										} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (ExecutionException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}

									}
								}
							}
						}
					}, 500);
		}
		
		}
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		/*case R.id.itemRefresh:

			/*handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (currentPosition > 0) {
						if (Utility.hasNetworkConnection(mContext)) {
							getNewPostsByCategory(currentCategory);
						}
					} else
						setFavorites(currentCategory,false);

				}
			}, 300);

			break;*/
		case R.id.itemSettings:
			getActivity().startActivityForResult(
					new Intent(this.getActivity(), SettingsActivity.class),
					2345);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		isRefreshing = true;
isFound=false;
		// progress.show();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (currentPosition > 0) {
					if (Utility.hasNetworkConnection(mContext)) {
						getNewPostsByCategory(currentCategory);
						
					}
				} else
					setFavorites(currentCategory,false);

			}
		}, 300);
	}

	public void changeFavorite(int position) {
		// TODO Auto-generated method stub
		ArrayList<PostData> temp=(Utility.IS_LIST)?mAdapter.getPosts():gdAdapter.getPosts();
		PostData p=(PostData) temp.get(position);
		p.setFavorite(!p.isFavorite());
		int cursor=listView.getFirstVisiblePosition();
		if (Utility.IS_LIST)
		{
			mAdapter=new PostDataAdapter(mContext, temp);
			listView.setAdapter(mAdapter);
		}
		else{
			gdAdapter=new PostGridAdapter(mContext, temp);
			listView.setAdapter(gdAdapter);
		}
		listView.setSelection(cursor);
		listView.invalidateViews();
	}

}
