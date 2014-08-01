package com.qbit.p2p.credit.commons.resource;

import com.qbit.p2p.credit.commons.model.Currency;
import com.qbit.p2p.credit.money.model.serialization.CurrencyAdapter;
import java.io.Serializable;
import java.util.Arrays;
import java.util.EnumSet;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author Alexander_Sergeev
 */
@Path("currency")
@Singleton
public class CurrencyResource {

	@XmlRootElement
	public static class CurrencyWrapper implements Serializable {

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

	@XmlRootElement
	public static class CurrencyListWrapper implements Serializable {

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

	/*@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public CurrencyWrapper get(@PathParam("id") String id) {
		return new CurrencyWrapper(Currency.valueOf(id));
	}*/

	/*@GET
	@Produces(MediaType.APPLICATION_JSON)
	public CurrencyListWrapper findAll() {
		return new CurrencyListWrapper(EnumSet.copyOf(Arrays.asList(Currency.values())));
	}*/
}
