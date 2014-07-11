package com.qbit.p2p.credit.commons.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Alexander_Sergeev
 */
public final class DateUtil {
	
	public static final String DATE_FORMAT = "MMMM d, yyyy";
	private DateUtil(){
	}
	
	public static Date stringToDate(String dateStr) {
		try {
			return new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH).parse(dateStr);
		} catch (ParseException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public static Date stringToDate(String dateStr, String dateFormat) {
		try {
			return new SimpleDateFormat(dateFormat, Locale.ENGLISH).parse(dateStr);
		} catch (ParseException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public static String dateToString(Date date) {
		return new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH).format(new Date());
	}
	
	public static String dateToString(Date date, String dateFormat) {
		return new SimpleDateFormat(dateFormat, Locale.ENGLISH).format(new Date());
	}
}
