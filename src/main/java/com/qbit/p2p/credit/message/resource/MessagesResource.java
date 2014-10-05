package com.qbit.p2p.credit.message.resource;

import com.qbit.commons.auth.AuthFilter;
import com.qbit.commons.xss.util.XSSRequestFilter;
import com.qbit.p2p.credit.message.dao.MessageDAO;
import com.qbit.p2p.credit.message.model.Message;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Alexander_Sergeev
 */
@Path("messages")
@Singleton
public class MessagesResource {
	
	private final String ADMIN_ID = "ADMIN";
	
	@Context
	private HttpServletRequest request;
	
	@Inject
	private MessageDAO messageDAO;
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Message create(Message message) {
		if(message == null) {
			return null;
		}
		message.setMessage(XSSRequestFilter.stripXSS(message.getMessage()));
		message.setPartnerId(XSSRequestFilter.stripXSS(message.getPartnerId()));
		String userId = AuthFilter.getUserId(request);
		message.setUserId(userId);
		return messageDAO.create(message);
	}
	
	@PUT
	@Path("admin-message")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Message createMessageForAdmin(Message message) {
		if(message == null) {
			return null;
		}
		message.setMessage(XSSRequestFilter.stripXSS(message.getMessage()));
		String userId = AuthFilter.getUserId(request);
		message.setPartnerId(ADMIN_ID);
		message.setUserId(userId);
		return messageDAO.createMessageForAdmin(message);
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public MessagesWrapper getCurrentUserMessages(@QueryParam("partnerId") String partnerId, 
			@QueryParam("firstPage") int firstPage, @QueryParam("pageSize") int pageSize) {
		String userId = AuthFilter.getUserId(request);
		List<Message> messages = messageDAO.findByUserId(userId, firstPage, pageSize);
		long length = messageDAO.getLengthByUserId(userId);
		return new MessagesWrapper(messages, length);
	}
	
	@GET
	@Path("partners-last-messages")
	@Produces(MediaType.APPLICATION_JSON)
	public MessagesWrapper getPartnersLastMessages(@QueryParam("firstPage") int firstPage, @QueryParam("pageSize") int pageSize) {
		String userId = AuthFilter.getUserId(request);
		List<Message> messages = new ArrayList<>();
		List<String> partnersIds = messageDAO.findPartnersIds(userId);
		for(String partnerId : partnersIds) {
			List<Message> m = messageDAO.findByUserIdAndPartnerId(userId, partnerId, 0, 1);
			messages.add(m.get(0)); 
		}
		long length = messageDAO.getLengthByPartnersIds(userId);
		return new MessagesWrapper(messages, length);
	}
	
	@GET
	@Path("by-partner")
	@Produces(MediaType.APPLICATION_JSON)
	public MessagesWrapper getByPartnerId(@QueryParam("partnerId") String partnerId, 
			@QueryParam("firstPage") int firstPage, @QueryParam("pageSize") int pageSize) {
		String userId = AuthFilter.getUserId(request);
		List<Message> messages = messageDAO.findByUserIdAndPartnerId(userId, partnerId, firstPage, pageSize);
		long length = messageDAO.getLengthByUserIdAndPartnerId(userId, partnerId);
		return new MessagesWrapper(messages, length);
	}
	
	@GET
	@Path("later-than")
	@Produces(MediaType.APPLICATION_JSON)
	public MessagesWrapper getLaterThan(@QueryParam("creationDate") String creationDateString, @QueryParam("partnerId") String partnerId) {
		if(creationDateString == null) {
			return null;
		}
		String userId = AuthFilter.getUserId(request);
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
		Date creationDate = null;
		try {
			creationDate = dateFormat.parse(creationDateString);
		} catch (ParseException ex) {
			//
		}
		
		creationDate.setTime(creationDate.getTime() + 1000);
		return new MessagesWrapper(messageDAO.findLaterThan(userId, partnerId, creationDate));
	}
}
