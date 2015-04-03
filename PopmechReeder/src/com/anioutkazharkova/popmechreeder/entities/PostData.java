package com.anioutkazharkova.popmechreeder.entities;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class PostData implements Parcelable {

	public PostData() {
	}

	public PostData(Parcel source) {
		this.title = source.readString();
		this.link = source.readString();
		this.description = source.readString();
		this.urlImage = source.readString();
		this.pubDate = source.readString();
		this.isFavorite = (source.readInt() == 1) ? true : false;
		this.category = source.readString();
	}

	public static final Parcelable.Creator<PostData> CREATOR = new Creator<PostData>() {

		@Override
		public PostData[] newArray(int size) {
			// TODO Auto-generated method stub
			return new PostData[size];
		}

		@Override
		public PostData createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new PostData(source);
		}
	};
	private String title;
	private String link;
	private String description;
	private String pubDate;
	private String urlImage;
	private Bitmap imageBitmap;
	private boolean isFavorite;
	private String category;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	public String getUrlImage() {
		return urlImage;
	}

	public void setUrlImage(String urlImage) {
		this.urlImage = urlImage;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Link: " + link + " , pubDate: " + pubDate + ", url: "
				+ urlImage + " ,title: " + title + " descr: " + description;
	}

	public Bitmap getImageBitmap() {
		return imageBitmap;
	}

	public void setImageBitmap(Bitmap imageBitmap) {
		this.imageBitmap = imageBitmap;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub

		dest.writeString(title);
		dest.writeString(link);
		dest.writeString(description);
		dest.writeString(urlImage);
		dest.writeString(pubDate);
		dest.writeInt((isFavorite) ? 1 : 0);
		dest.writeString(category);
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if (this.link.equals(((PostData) o).getLink()))
			return true;
		else
			return false;
	}

	public boolean isFavorite() {
		return isFavorite;
	}

	public void setFavorite(boolean isFavorite) {
		this.isFavorite = isFavorite;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

}
