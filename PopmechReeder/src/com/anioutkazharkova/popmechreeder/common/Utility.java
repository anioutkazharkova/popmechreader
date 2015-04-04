package com.anioutkazharkova.popmechreeder.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.anioutkazharkova.popmechreeder.R;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;

public class Utility {
	
	public static final String PREFERENCES = "reader_preferences";
	public static final String THEME_PREF="theme_preferences";
	public static final String MODE_PREF="mode_preferences";
	public static final String SHOW_IMAGES_PREF = "show_images_preferences";
	
	public static String DATE_FORMAT="dd MMM yyyy HH:mm:ss";
	public static String DATE="dd.MM.yyyy HH:mm";
	public static String TIME_FORMAT="HH:mm";
	
	public static boolean IS_LIGHT=true;
	public static boolean IS_LIST=true;
	public static boolean IS_SHOW_IMAGES=true;

	public static String getUrl(String base) {
		String regex = "http(.+)(jpg|JPG|png|PNG|gif|GIF|jpeg|JPEG)";
		Pattern patten = Pattern.compile(regex);
		Matcher matcher = patten.matcher(base);

		if (matcher.find()) {
			return matcher.group(0);
		} else
			return "";
	}
	
	public static String getParsedDate(String date,Context context)
	{
		String workDate="";
		String regex = "\\d{2} ([a-zA-Z]{3}) \\d{4} \\d{2}\\:\\d{2}\\:\\d{2}";
		Pattern pattern = Pattern.compile(regex);

		Matcher m = pattern.matcher(date);
		if (m.find()) {
			workDate=(m.group(0));
		}
		String result="";
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				DATE_FORMAT,Locale.ENGLISH);
		SimpleDateFormat timeFormat=new SimpleDateFormat(TIME_FORMAT);
		SimpleDateFormat correctDate=new SimpleDateFormat(DATE);
		
		Calendar calendar=Calendar.getInstance();
		
	try {
	Date pubDate=	dateFormat.parse(workDate);
	Date curDate=dateFormat.parse(dateFormat.format(calendar.getTime()));
	if (pubDate.getMonth()==curDate.getMonth() && pubDate.getYear()==curDate.getYear())
	{
		if (pubDate.getDay()==curDate.getDay())
		{
			result+=context.getResources().getString(R.string.today)+", "+timeFormat.format(pubDate);
		}
		else
		{
			if (pubDate.getDay()==curDate.getDay()-1)
			{
				result+=context.getResources().getString(R.string.yesterday)+", "+timeFormat.format(pubDate);
			}
			else
			{
				result=correctDate.format(pubDate);
			}
		}
	}
	else
	{
		result=correctDate.format(pubDate);
	}
	
	return result;
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		return workDate;
	}

	public static int DpToPx(Context context, int value) {
		DisplayMetrics displayMetrics = context.getResources()
				.getDisplayMetrics();
		float scale = displayMetrics.density;
		int pixels = (int) (value * scale + 0.5f);
		return pixels;

	}
	
	public static boolean hasNetworkConnection(Context context)
	{
		ConnectivityManager manager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info =manager.getActiveNetworkInfo();
		return (info!=null)? info.isConnectedOrConnecting():false;
		
		
	}
	
	
}
