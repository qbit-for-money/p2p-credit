package com.qbit.p2p.credit.commons.resource;

import com.qbit.p2p.credit.commons.model.Currency;
import java.util.Arrays;
import java.util.EnumSet;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author Alexander_Sergeev
 */
@Path("currency")
@Singleton
public class CurrencyResource {

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public CurrencyWrapper get(@PathParam("id") String id) {
		return new CurrencyWrapper(Currency.valueOf(id));
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public CurrencyListWrapper findAll() {
		return new CurrencyListWrapper(EnumSet.copyOf(Arrays.asList(Currency.values())));
	}
}
