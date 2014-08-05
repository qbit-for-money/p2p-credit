package com.qbit.p2p.credit.user.resource;

import com.qbit.commons.auth.AuthFilter;
import com.qbit.p2p.credit.user.dao.UserProfileDAO;
import com.qbit.p2p.credit.statistics.service.StatisticsService;
import com.qbit.p2p.credit.user.model.ShortProfile;
import com.qbit.p2p.credit.user.model.UserPublicProfile;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * @author Alexander_Sergeev
 */
@Path("profiles")
@Singleton
public class UserProfilesResource {

	@Context
	private HttpServletRequest request;

	@Inject
	private UserProfileDAO userProfileDAO;

	@Inject
	private StatisticsService statisticsService;

	@GET
	@Path("current")
	@Produces(MediaType.APPLICATION_JSON)
	public UserPublicProfile current() {
		String userId = AuthFilter.getUserId(request);
		if (!userId.contains("@")) {
			return null;
		}
		UserPublicProfile profile = userProfileDAO.find(userId);
		if (profile == null) {
			profile = userProfileDAO.create(userId);
		}
		return profile;
	}

	/*@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public UserPublicProfile getById(@PathParam("id") String userId) {
		UserPublicProfile profile = userProfileDAO.find(userId);
		if (profile == null) {
			return null;
		}
		if (!profile.isMailEnabled()) {
			profile.setMail(null);
		}
		if (!profile.isPhoneEnabled()) {
			profile.setPhone(null);
		}
		if (!profile.isPersonalDataEnabled()) {
			profile.setPersonalData(null);
		}
		return profile;
	}*/

	@GET
	@Path("{id}/short")
	@Produces(MediaType.APPLICATION_JSON)
	public ShortProfile getShortById(@PathParam("id") String userId) {
		UserPublicProfile profile = userProfileDAO.find(userId);
		if (profile == null) {
			return null;
		}
		return new ShortProfile(profile);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public UsersPublicProfilesWrapper getAll(@QueryParam("sortdatafield") String sortDataField, @QueryParam("sortorder") String sortOrder,
			@QueryParam("pagenum") int pagenum, @QueryParam("pagesize") int limit) {
		boolean sortDesc = "desc".equals(sortOrder);
		return new UsersPublicProfilesWrapper(userProfileDAO.findAll(
				sortDataField, sortDesc, pagenum * limit, limit), userProfileDAO.length());
	}
	
	@POST
	@Path("current")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public UserPublicProfile updateUserMainAttributes(UserPublicProfile userProfile) {
		String userId = AuthFilter.getUserId(request);
		if ((userId == null) || (userProfile == null) || !userId.equals(userProfile.getUserId())) {
			throw new IllegalArgumentException();
		}
		UserPublicProfile newProfile = userProfileDAO.updateUserMainAttributes(userProfile);
		statisticsService.recalculateOpenessRating(userId);
		
		return newProfile;
	}

	@POST
	@Path("current/social")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public UserPublicProfile updateUserSocialLinks(UserPublicProfile userProfile) {
		String userId = AuthFilter.getUserId(request);
		if ((userId == null) || (userProfile == null) || !userId.equals(userProfile.getUserId())) {
			throw new IllegalArgumentException();
		}
		UserPublicProfile newProfile = userProfileDAO.updateUserSocialLinks(userId, userProfile.getSocialLinks());
		statisticsService.recalculateOpenessRating(userId);
		
		return newProfile;
	}
	
	@POST
	@Path("current/video")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public UserPublicProfile updateUserVideos(UserPublicProfile userProfile) {
		String userId = AuthFilter.getUserId(request);
		if ((userId == null) || (userProfile == null) || !userId.equals(userProfile.getUserId())) {
			throw new IllegalArgumentException();
		}
		UserPublicProfile newProfile = userProfileDAO.updateUserVideos(userId, userProfile.getSocialLinks());
		statisticsService.recalculateOpenessRating(userId);
		
		return newProfile;
	}
	
	@POST
	@Path("current/passport")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public UserPublicProfile updatePassportEnabled(UserPublicProfile userProfile) {
		String userId = AuthFilter.getUserId(request);
		if ((userId == null) || (userProfile == null) || !userId.equals(userProfile.getUserId())) {
			throw new IllegalArgumentException();
		}
		UserPublicProfile newProfile = userProfileDAO.updatePassportEnabled(userId, userProfile.isPassportEnabled());
		statisticsService.recalculateOpenessRating(userId);
		
		return newProfile;
	}
}
