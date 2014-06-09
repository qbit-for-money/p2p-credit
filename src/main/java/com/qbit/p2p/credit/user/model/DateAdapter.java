package com.qbit.p2p.credit.user.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ws.rs.WebApplicationException;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Alexander_Sergeev
 */
public class DateAdapter extends XmlAdapter<String, Date> {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	@Override
	public String marshal(Date v) {
		if (v == null) {
			return null;
		}
		return dateFormat.format(v);
	}

	@Override
	public Date unmarshal(String v) {
		if (v == null) {
			return null;
		}
		try {
			return dateFormat.parse(v);
		} catch (ParseException e) {
			throw new WebApplicationException();
		}
	}
}
