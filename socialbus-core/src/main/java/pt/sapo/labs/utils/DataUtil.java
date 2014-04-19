package pt.sapo.labs.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DataUtil {
	/** The date format in iso. */
	public static String FORMAT_DATE_ISO="yyyy-MM-dd'T'HH:mm:ssZ";
	
	public static String FORMAT_DATE_TWITTER="EEE MMM dd HH:mm:ss Z yyyy";

	/**
	 * Takes in an Twitter date string of the following format:
	 * EEE MMM dd HH:mm:ss Z yyyy
	 *
	 * @param twitterDateString the Twitter date string
	 * @return the date
	 * @throws Exception the exception
	 */
	public static Date fromTwitterDateString(String twitterDateString)
			throws Exception
			{
		DateFormat f = new SimpleDateFormat(FORMAT_DATE_TWITTER);
		return f.parse(twitterDateString);
			}
	
	/**
	 * Takes in an ISO date string of the following format:
	 * yyyy-mm-ddThh:mm:ss.ms+HoMo
	 *
	 * @param isoDateString the iso date string
	 * @return the date
	 * @throws Exception the exception
	 */
	public static Date fromISODateString(String isoDateString)
			throws Exception
			{
		DateFormat f = new SimpleDateFormat(FORMAT_DATE_ISO);
		return f.parse(isoDateString);
			}

	/**
	 * Render date
	 *
	 * @param date the date obj
	 * @param format - if not specified, will use FORMAT_DATE_ISO
	 * @param tz - tz to set to, if not specified uses local timezone
	 * @return the iso-formatted date string
	 */
	public static String toISOString(Date date, String format, TimeZone tz)
	{
		if( format == null ) format = FORMAT_DATE_ISO;
		if( tz == null ) tz = TimeZone.getDefault();
		DateFormat f = new SimpleDateFormat(format);
		f.setTimeZone(tz);
		return f.format(date);
	}

	public static String toISOString(Date date)
	{ return toISOString(date,FORMAT_DATE_ISO,TimeZone.getDefault()); }
}
