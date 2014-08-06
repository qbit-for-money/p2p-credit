package com.qbit.p2p.credit.order.dao.meta;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Alexander_Sergeev
 */
public class DateValueProvider implements ValueProvider {
	
	public static final DateValueProvider INST = new DateValueProvider();
	
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
	private DateValueProvider() {
	}
	
	@Override
	public Date get(String value) {
		try {
			return DATE_FORMAT.parse(value);
		} catch (ParseException ex) {
			return null;
		}
	}
}
