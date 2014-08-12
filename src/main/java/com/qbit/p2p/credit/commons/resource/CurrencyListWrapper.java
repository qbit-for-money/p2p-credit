package com.qbit.p2p.credit.commons.resource;

import com.qbit.p2p.credit.commons.model.Currency;
import com.qbit.p2p.credit.commons.model.CurrencyAdapter;
import java.io.Serializable;
import java.util.EnumSet;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author Alexander_Sergeev
 */
@XmlRootElement
public class CurrencyListWrapper implements Serializable {

	@XmlJavaTypeAdapter(CurrencyAdapter.class)
	private EnumSet<Currency> currencies;

	public CurrencyListWrapper() {
	}

	public CurrencyListWrapper(EnumSet<Currency> currencies) {
		this.currencies = currencies;
	}

	public EnumSet<Currency> getCurrencies() {
		return currencies;
	}
}
