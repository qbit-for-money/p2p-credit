package com.qbit.p2p.credit.order.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import javax.ws.rs.WebApplicationException;

/**
 * @author Alexander_Sergeev
 */
public class DateValueProvider implements ValueProvider {
	@Override
	public Date get(String value) {
		try {
			return DateFormat.getDateInstance().parse(value);
		} catch (ParseException ex) {
			throw new WebApplicationException(ex);
		}
	}
}
