package com.qbit.p2p.credit.order.dao.meta;

import com.qbit.p2p.credit.commons.model.Currency;

/**
 * @author Alexander_Sergeev
 */
public class CurrencyValueProvider implements ValueProvider {
	
	public static final CurrencyValueProvider INST = new CurrencyValueProvider();
	
	private CurrencyValueProvider() {
	}
	
	@Override
	public Currency get(String value) {
		if ((value == null) || value.isEmpty()) {
			return null;
		}
		return Currency.valueOf(value.toUpperCase());
	}
}
