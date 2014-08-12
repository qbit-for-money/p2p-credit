package com.qbit.p2p.credit.commons.resource;

import com.qbit.p2p.credit.commons.model.Currency;
import com.qbit.p2p.credit.commons.model.CurrencyAdapter;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author Alexander_Sergeev
 */
@XmlRootElement
public class CurrencyWrapper implements Serializable {

	@XmlJavaTypeAdapter(CurrencyAdapter.class)
	private Currency currency;

	public CurrencyWrapper() {
	}

	public CurrencyWrapper(Currency currency) {
		this.currency = currency;
	}

	public Currency getCurrency() {
		return currency;
	}
}
