package com.anioutkazharkova.popmechreeder.entities;

import com.anioutkazharkova.popmechreeder.R;



public class Category {

	private String name;
	private String url;
	private int image_name;

	public Category()
	{
		image_name=R.drawable.rss;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getImage_name() {
		return image_name;
	}

	public void setImage_name(int image_name) {
		this.image_name = image_name;
	}
}
