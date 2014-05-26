package com.qbit.p2p.credit.user.resource;

import com.qbit.commons.auth.AuthFilter;
import com.qbit.p2p.credit.user.dao.UserProfileDAO;
import com.qbit.p2p.credit.user.model.UserPrivateProfile;
import com.qbit.p2p.credit.user.model.UserPublicProfile;
import com.qbit.p2p.credit.user.model.UserType;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * @author Alexander_Sergeev
 */
@Path("users-profile")
@Singleton
public class UsersProfileResource {

	/*public static class UserProfile {

		private String publicKey;
		private String firstName;
		private String lastName;
		private String password;
		private long rating;
		private UserType type;

		public String getPublicKey() {
			return publicKey;
		}

		public void setPublicKey(String publicKey) {
			this.publicKey = publicKey;
		}

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public long getRating() {
			return rating;
		}

		public void setRating(long rating) {
			this.rating = rating;
		}

		public UserType getType() {
			return type;
		}

		public void setType(UserType type) {
			this.type = type;
		}
	}*/

	@Context
	private HttpServletRequest request;

	@Inject
	UserProfileDAO userProfileDAO;

	@GET
	@Path("current")
	@Produces(MediaType.APPLICATION_JSON)
	public UserPublicProfile current() {
		String userId = AuthFilter.getUserId(request);
		UserPublicProfile user = userProfileDAO.find(userId);
		if(user == null) {
			user = userProfileDAO.create(userId);
		}
		
		return user;
	}
	
	@GET
	@Path("byId")
	@Produces(MediaType.APPLICATION_JSON)
	public UserPublicProfile getById(@QueryParam("id") String id) {
		return userProfileDAO.find(id);
	}

	@GET
	@Path("usersByType")
	@Produces(MediaType.APPLICATION_JSON)
	public List<UserPublicProfile> getByType(@QueryParam("type") UserType type,
			@QueryParam("offset") int offset, @QueryParam("limit") int limit) {
		return userProfileDAO.findByType(type, offset, limit);
	}

	@GET
	@Path("usersByRating")
	@Produces(MediaType.APPLICATION_JSON)
	public List<UserPublicProfile> getByRating(@QueryParam("rating") long rating, @QueryParam("isNoMoreThan") boolean isNoMoreThan,
			@QueryParam("offset") int offset, @QueryParam("limit") int limit) {
		return userProfileDAO.findByRating(rating, isNoMoreThan, offset, limit);
	}

	@GET
	@Path("usersByOrders")
	@Produces(MediaType.APPLICATION_JSON)
	public List<UserPublicProfile> getByOrders(@QueryParam("number") long number, @QueryParam("isNoMoreThan") boolean isNoMoreThan,
			@QueryParam("offset") int offset, @QueryParam("limit") int limit) {
		return userProfileDAO.findByOrders(number, isNoMoreThan, offset, limit);
	}

	@POST
	@Path("updatePublicProfile")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public UserPublicProfile updatePublicProfile(UserPublicProfile userProfile) {
		System.out.println("!!! UPDATE: " + userProfile);
		if(userProfile == null) {
			throw new IllegalArgumentException();
		}
		String userId = AuthFilter.getUserId(request);   //rating
		userProfile.setPublicKey(userId);
		
		return userProfileDAO.updateUserPublicProfile(userProfile);
	}
	
	@POST
	@Path("updatePrivateProfile")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public UserPrivateProfile updatePrivateProfile(UserPrivateProfile userProfile) {
		String userId = AuthFilter.getUserId(request);
		userProfile.setPublicKey(userId);
		return userProfileDAO.updateUserPrivateProfile(userProfile);
	}
}
