package com.qbit.p2p.credit.order.model;

import com.qbit.p2p.credit.commons.model.Currency;

/**
 * @author Alexander_Sergeev
 */
public class CurrencyValueProvider implements ValueProvider {
	@Override
	public Currency get(String value) {
		return Currency.valueOf(value);
	}
}
