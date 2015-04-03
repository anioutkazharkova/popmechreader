package com.anioutkazharkova.popmechreeder.common;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ParseException;
import android.os.AsyncTask;
import android.util.Log;

import com.anioutkazharkova.popmechreeder.entities.PostData;
import com.anioutkazharkova.popmechreeder.services.DatabaseWorker;

public class RssDataController extends
		AsyncTask<String, Integer, ArrayList<PostData>> {
	private RSSXMLTag currentTag;
	DatabaseWorker dbWorker;
	String category;
	private Context mContext;

	public RssDataController() {
	}

	public RssDataController(Context context) {
		dbWorker = new DatabaseWorker(context);
		mContext = context;
	}

	@Override
	protected ArrayList<PostData> doInBackground(String... params) {
		// TODO Auto-generated method stub
		String urlStr = params[0];
		if (params.length >= 2) {
			category = params[1];
		}
		InputStream is = null;
		HttpURLConnection connection = null;
		ArrayList<PostData> postDataList = new ArrayList<PostData>();
		try {
			URL url = new URL(urlStr);
			connection = (HttpURLConnection) url.openConnection();
			connection.setReadTimeout(10 * 1000);
			connection.setConnectTimeout(10 * 1000);
			connection.setRequestMethod("GET");
			connection.setDoInput(true);
			connection.connect();
			int response = connection.getResponseCode();
			
			Log.d("Reader", "The response is: " + response);
			is = connection.getInputStream();

			//Parsing xml
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(is, null);

			int eventType = xpp.getEventType();
			PostData pdData = null;			
			
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_DOCUMENT) {

				} else if (eventType == XmlPullParser.START_TAG) {
					if (xpp.getName().equals("item")) {
						pdData = new PostData();
						if (category != null) {
							pdData.setCategory(category);
						}
						currentTag = RSSXMLTag.IGNORETAG;
					} else if (xpp.getName().equals("title")) {
						currentTag = RSSXMLTag.TITLE;
					} else if (xpp.getName().equals("link")) {
						currentTag = RSSXMLTag.LINK;
					} else if (xpp.getName().equals("pubDate")) {
						currentTag = RSSXMLTag.DATE;
					} else if (xpp.getName().equals("enclosure")) {
						currentTag = RSSXMLTag.ENCLOSURE;
					}
					if (xpp.getName().equals("description")) {
						currentTag = RSSXMLTag.DESCRIPTION;
					}

				} else if (eventType == XmlPullParser.END_TAG) {
					if (xpp.getName().equals("item")) {

						postDataList.add(pdData);
					} else {
						currentTag = RSSXMLTag.IGNORETAG;
					}
				} else if (eventType == XmlPullParser.TEXT) {
					String content = xpp.getText();
					content = content.trim();
					Log.d("debug", content);
					if (pdData != null) {
						switch (currentTag) {
						case TITLE:
							if (content.length() != 0) {
								if (pdData.getTitle() != null) {
									pdData.setTitle(pdData.getTitle() + content);
								} else {
									pdData.setTitle(content);
								}
							}
							break;
						case LINK:
							if (content.length() != 0) {
								if (pdData.getLink() != null) {
									pdData.setLink(pdData.getLink() + content);
								} else {
									pdData.setLink(content);
								}
							}
							break;
						case DATE:
							if (content.length() != 0) {
								if (pdData.getPubDate() != null) {
									pdData.setPubDate(pdData.getPubDate()
											+ content);
								} else {
									pdData.setPubDate(content);
								}
							}
							break;
						case DESCRIPTION:
							if (content.length() != 0) {
								if (pdData.getDescription() != null) {
									pdData.setDescription(pdData
											.getDescription()
											+ content.substring(content
													.indexOf("<br />") + 6));
								} else {
									pdData.setDescription(content
											.substring(content
													.indexOf("<br />") + 6));
								}
								if (pdData.getUrlImage() != null) {
									pdData.setUrlImage(pdData.getUrlImage()
											+ Utility.getUrl(content));
								} else {
									pdData.setUrlImage(Utility.getUrl(content));
								}
							}
							break;
						case ENCLOSURE:
							if (content.length() != 0) {

							}
							break;
						default:
							break;
						}
					}
				}

				eventType = xpp.next();
			}
			
			Log.v("tst", String.valueOf((postDataList.size())));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Saving received posts in database and getting corrected list of posts (includes old posts with some user data - favorite marks)
		if (dbWorker != null) {
			if (category == null) {
				return dbWorker.savePosts(postDataList);
			} else {
				return dbWorker.savePosts(postDataList, category);
			}
		}

		return postDataList;
	}

}
