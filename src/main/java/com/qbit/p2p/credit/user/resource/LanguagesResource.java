package com.qbit.p2p.credit.user.resource;

import com.qbit.p2p.credit.user.dao.LanguageDAO;
import com.qbit.p2p.credit.user.model.Language;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author Alexander_Sergeev
 */
@Path("languages")
@Singleton
public class LanguagesResource {

	@Inject
	private LanguageDAO languageDAO;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public LanguagesWrapper getAll() {
		return new LanguagesWrapper(languageDAO.findAll());
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Language create(Language language) {
		return languageDAO.create(language.getCode());
	}
}
