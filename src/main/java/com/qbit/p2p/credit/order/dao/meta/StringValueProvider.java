package com.qbit.p2p.credit.order.dao.meta;

/**
 * @author Alexander_Sergeev
 */
public class StringValueProvider implements ValueProvider {
	
	public static final StringValueProvider INST = new StringValueProvider();
	
	private StringValueProvider() {
	}
	
	@Override
	public String get(String value) {
		return value;
	}
}
