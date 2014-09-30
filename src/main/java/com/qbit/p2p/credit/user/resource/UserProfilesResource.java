package com.qbit.p2p.credit.user.resource;

import com.qbit.commons.auth.AuthFilter;
import com.qbit.commons.user.UserDAO;
import com.qbit.commons.xss.util.XSSRequestFilter;
import com.qbit.p2p.credit.user.dao.UserProfileDAO;
import com.qbit.p2p.credit.statistics.service.StatisticsService;
import com.qbit.p2p.credit.user.model.DataLink;
import com.qbit.p2p.credit.user.model.Language;
import com.qbit.p2p.credit.user.model.ShortProfile;
import com.qbit.p2p.credit.user.model.UserPublicProfile;
import java.util.List;
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
	UserDAO userDAO;
	@Inject
	private UserProfileDAO userProfileDAO;

	@Inject
	private StatisticsService statisticsService;

	@GET
	@Path("current")
	@Produces(MediaType.APPLICATION_JSON)
	public UserPublicProfile current() {
		String userId = AuthFilter.getUserId(request);
		if (!userId.contains("@") && !userId.contains("vk-") && !userId.contains("fb-")) {
			return null;
		}
		UserPublicProfile profile = userProfileDAO.find(userId);
		if (profile == null) {
			profile = userProfileDAO.create(userId);
		}
		return profile;
	}

	@GET
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
		if (!profile.isLanguagesEnabled()) {
			profile.setLanguages(null);
		}
		if (profile.isCurrenciesEnabled()) {
			profile.setCurrencies(null);
		}
		return profile;
	}

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
		if ((userProfile == null) || !userId.equals(userProfile.getUserId())) {
			throw new IllegalArgumentException();
		}
		userProfile.setName(XSSRequestFilter.stripXSS(userProfile.getName()));
		userProfile.setMail(XSSRequestFilter.stripXSS(userProfile.getMail()));
		userProfile.setPhone(XSSRequestFilter.stripXSS(userProfile.getPhone()));
		List<Language> languages = userProfile.getLanguages();
		if ((languages != null) && !languages.isEmpty()) {
			for (Language language : languages) {
				String code = language.getCode();
				if (!code.equals(XSSRequestFilter.stripXSS(code))) {
					return null;
				}
			}
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
		if ((userProfile == null) || !userId.equals(userProfile.getUserId())) {
			throw new IllegalArgumentException();
		}
		List<DataLink> links = userProfile.getSocialLinks();
		if (links != null) {
			for (DataLink link : links) {
				link.setLink(XSSRequestFilter.stripXSS(link.getLink()));
				link.setTitle(XSSRequestFilter.stripXSS(link.getTitle()));
			}
		}
		UserPublicProfile newProfile = userProfileDAO.updateUserSocialLinks(userId, links);
		statisticsService.recalculateOpenessRating(userId);

		return newProfile;
	}

	@POST
	@Path("current/video")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public UserPublicProfile updateUserVideos(UserPublicProfile userProfile) {
		String userId = AuthFilter.getUserId(request);
		if ((userProfile == null) || !userId.equals(userProfile.getUserId())) {
			throw new IllegalArgumentException();
		}
		List<DataLink> videos = userProfile.getVideos();
		if (videos != null) {
			for (DataLink video : videos) {
				video.setLink(XSSRequestFilter.stripXSS(video.getLink()));
				video.setTitle(XSSRequestFilter.stripXSS(video.getTitle()));
			}
		}
		UserPublicProfile newProfile = userProfileDAO.updateUserVideos(userId, userProfile.getVideos());
		statisticsService.recalculateOpenessRating(userId);

		return newProfile;
	}

	@POST
	@Path("current/passport")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public UserPublicProfile updatePassportEnabled(UserPublicProfile userProfile) {
		String userId = AuthFilter.getUserId(request);
		if ((userProfile == null) || !userId.equals(userProfile.getUserId())) {
			throw new IllegalArgumentException();
		}
		UserPublicProfile newProfile = userProfileDAO.updatePassportEnabled(userId, userProfile.isPassportEnabled());
		statisticsService.recalculateOpenessRating(userId);

		return newProfile;
	}
}
