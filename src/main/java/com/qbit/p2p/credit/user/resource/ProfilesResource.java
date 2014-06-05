package com.qbit.p2p.credit.user.resource;

import com.qbit.commons.auth.AuthFilter;
import com.qbit.commons.auth.EncryptionUtil;
import com.qbit.commons.user.UserDAO;
import com.qbit.p2p.credit.env.Env;
import com.qbit.p2p.credit.material.dao.MaterialDAO;
import com.qbit.p2p.credit.material.model.MaterialType;
import com.qbit.p2p.credit.material.model.Materials;
import com.qbit.p2p.credit.user.dao.UserProfileDAO;
import com.qbit.p2p.credit.user.model.UserPrivateProfile;
import com.qbit.p2p.credit.user.model.UserPublicProfile;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import sun.misc.BASE64Decoder;

/**
 * @author Alexander_Sergeev
 */
@Path("profiles")
@Singleton
public class ProfilesResource {

	public static class UserPhotoRequest {

		public static final int MAX_STRING_LENGTH = 1000000;
		public static final int MAX_WIDTH = 1000;
		public static final int MAX_HEIGHT = 1000;
		public static final int MIN_WIDTH = 100;
		public static final int MIN_HEIGHT = 100;

		private int x1;
		private int x2;
		private int y1;
		private int y2;

		private String imageString;

		public int getX1() {
			return x1;
		}

		public void setX1(int x1) {
			this.x1 = x1;
		}

		public int getX2() {
			return x2;
		}

		public void setX2(int x2) {
			this.x2 = x2;
		}

		public int getY1() {
			return y1;
		}

		public void setY1(int y1) {
			this.y1 = y1;
		}

		public int getY2() {
			return y2;
		}

		public void setY2(int y2) {
			this.y2 = y2;
		}

		public String getImageString() {
			return imageString;
		}

		public void setImageString(String imageString) {
			this.imageString = imageString;
		}

		public boolean isValid() {
			return imageString != null && ((imageString.isEmpty())
					|| ((imageString.length() < MAX_STRING_LENGTH)
					&& (x1 >= 0) && (x2 > 0)
					&& (x2 - x1 < MAX_WIDTH)
					&& (x2 - x1 > MIN_WIDTH)
					&& (y1 >= 0) && (y2 > 0)
					&& (y2 - y1 < MAX_HEIGHT)
					&& (y2 - y1 > MIN_HEIGHT)));

		}

		@Override
		public String toString() {
			return "UserPhotoRequest{" + "x1=" + x1 + ", x2=" + x2 + ", y1=" + y1 + ", y2=" + y2 + ", imageString=" + imageString + '}';
		}
	}

	@XmlRootElement
	public static class UsersPublicProfilesWrapper {

		@XmlElement
		@XmlList
		private List<UserPublicProfile> users;
		@XmlElement
		Number length;

		public UsersPublicProfilesWrapper() {
		}

		public UsersPublicProfilesWrapper(List<UserPublicProfile> users, Number length) {
			this.users = users;
			this.length = length;
		}

		public List<UserPublicProfile> getUsers() {
			return users;
		}

		public Number getLength() {
			return length;
		}
	}

	@Inject
	Env env;

	@Context
	private HttpServletRequest request;

	@Inject
	UserProfileDAO userProfileDAO;

	@Inject
	UserDAO userDAO;

	@Inject
	MaterialDAO materialDAO;

	@GET
	@Path("current")
	@Produces(MediaType.APPLICATION_JSON)
	public UserPublicProfile current() {
		String userId = AuthFilter.getUserId(request);
		if (!userId.contains("@")) {
			return null;
		}
		UserPublicProfile user = userProfileDAO.find(userId);
		if (user == null) {
			user = userProfileDAO.create(userId);
		}
		return user;
	}

	@GET
	@Path("current/private")
	@Produces(MediaType.APPLICATION_JSON)
	public UserPrivateProfile getPrivateProfile() {
		String userId = AuthFilter.getUserId(request);
		if (!userId.contains("@")) {
			return null;
		}
		UserPrivateProfile user = userProfileDAO.findPrivateProfile(userId);
		if (user == null) {
			user = userProfileDAO.createUserPrivateProfile(userId);
		}

		return user;
	}

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public UserPublicProfile getById(@PathParam("id") String id) {
		UserPublicProfile user = userProfileDAO.find(id);
		if (user != null) {
			if (!user.isAgeEnabled()) {
				user.setBirthDate(null);
			}
			if (!user.isCityEnabled()) {
				user.setCity(null);
			}
			if (!user.isCountryEnabled()) {
				user.setCountry(null);
			}
			if (!user.isHobbyEnabled()) {
				user.setHobby(null);
			}
		}
		return user;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public UsersPublicProfilesWrapper getAll(@QueryParam("filterslength") int filterslength, @QueryParam("sortdatafield") String sortDataField,
			@QueryParam("sortorder") String sortOrder, @QueryParam("pagenum") int pagenum, @QueryParam("pagesize") int limit) {
		boolean sortDesc = false;
		if (sortOrder != null && sortOrder.equals("desc")) {
			sortDesc = true;
		}
		return new UsersPublicProfilesWrapper(userProfileDAO.findAll(sortDataField, sortDesc, pagenum * limit, limit), userProfileDAO.length());
	}

	@GET
	@Path("withFilter")
	@Produces(MediaType.APPLICATION_JSON)
	public UsersPublicProfilesWrapper getWithFilter(@QueryParam("filter") String filter, @QueryParam("filterdatafield") String filterDataField,
			@QueryParam("sortdatafield") String sortDataField, @QueryParam("isLess") boolean isLess,
			@QueryParam("sortorder") String sortOrder, @QueryParam("pagenum") int pagenum, @QueryParam("limit") int limit) {
		boolean sortDesc = false;
		if (sortOrder != null && sortOrder.equals("desc")) {
			sortDesc = true;
		}
		List<UserPublicProfile> users = null;
		if (filterDataField.equals("rating")) {
			long rating = Long.parseLong(filter);
			users = userProfileDAO.findByRating(rating, isLess, sortDataField, sortDesc, pagenum * limit, limit);
			return new UsersPublicProfilesWrapper(users, userProfileDAO.lengthWithFilterByRating(rating, isLess));
		} else {
			users = userProfileDAO.findWithFilter(filterDataField, filter, sortDataField, sortDesc, pagenum * limit, limit);
			return new UsersPublicProfilesWrapper(users, userProfileDAO.length(filterDataField, filter));
		}
	}

	@GET
	@Path("byOrders")
	@Produces(MediaType.APPLICATION_JSON)
	public List<UserPublicProfile> getByOrders(@QueryParam("number") long number, @QueryParam("isNoMoreThan") boolean isNoMoreThan,
			@QueryParam("offset") int offset, @QueryParam("limit") int limit) {
		return userProfileDAO.findByOrders(number, isNoMoreThan, offset, limit);
	}

	@POST
	@Path("current")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public UserPublicProfile updatePublicProfile(UserPublicProfile userProfile) {
		if (userProfile == null || !userProfile.isValid()) {
			throw new IllegalArgumentException();
		}
		String userId = AuthFilter.getUserId(request);
		userProfile.setPublicKey(userId);
		long rating = 5;
		if (userProfile.getFirstName() != null && !userProfile.getFirstName().isEmpty()) {
			rating = rating + 5;
		}
		if (userProfile.getLastName() != null && !userProfile.getLastName().isEmpty()) {
			rating = rating + 5;
		}
		if (userProfile.getCountry() != null && !userProfile.getCountry().isEmpty()) {
			rating = rating + 5;
		}
		if (userProfile.isCountryEnabled()) {
			rating = rating + 5;
		}
		if (userProfile.getCity() != null && !userProfile.getCity().isEmpty()) {
			rating = rating + 5;
		}
		if (userProfile.isCityEnabled()) {
			rating = rating + 5;
		}
		if (userProfile.getHobby() != null && !userProfile.getHobby().isEmpty()) {
			rating = rating + 5;
		}
		if (userProfile.isHobbyEnabled()) {
			rating = rating + 5;
		}
		if (userProfile.getBirthDate() != null) {
			rating = rating + 5;
		}
		if (userProfile.isAgeEnabled()) {
			rating = rating + 5;
		}
		List<Materials> materials = materialDAO.findByUserAndType(userId, MaterialType.USER_PHOTO, 0, 1);
		if (!materials.isEmpty()) {
			rating = rating + 5;
		}
		userProfile.setRating(rating);

		return userProfileDAO.updateUserPublicProfile(userProfile);
	}

	@POST
	@Path("current/private")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public UserPrivateProfile updatePrivateProfile(UserPrivateProfile userProfile) {
		String userId = AuthFilter.getUserId(request);
		userProfile.setPublicKey(userId);
		return userProfileDAO.updateUserPrivateProfile(userProfile);
	}

	@POST
	@Path("current/photo")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void setUserPhoto(UserPhotoRequest userPhoto) {
		if (!userPhoto.isValid()) {
			throw new WebApplicationException();
		}
		String userId = AuthFilter.getUserId(request);
		String imageName = EncryptionUtil.getMD5(userId);
		File userPhotoFile = new File(env.getUserPhotoPathFolder() + imageName + ".jpg");

		if (userPhoto.getImageString().isEmpty() && userPhotoFile.exists()) {
			userPhotoFile.delete();
			return;
		}

		byte[] imageByte;
		ByteArrayInputStream arrayInputStream = null;
		BufferedImage destBufferedImage = null;
		try {
			BASE64Decoder decoder = new BASE64Decoder();
			imageByte = decoder.decodeBuffer(userPhoto.getImageString());

			arrayInputStream = new ByteArrayInputStream(imageByte);
			BufferedImage bufferedImage = ImageIO.read(arrayInputStream);
			destBufferedImage = bufferedImage.getSubimage(userPhoto.getX1(), userPhoto.getY1(),
					userPhoto.getX2() - userPhoto.getX1(), userPhoto.getY2() - userPhoto.getY1());
			if (destBufferedImage.getHeight() > UserPhotoRequest.MAX_HEIGHT || destBufferedImage.getWidth() > UserPhotoRequest.MAX_WIDTH) {
				throw new IOException();
			}
		} catch (IOException e) {
			throw new WebApplicationException(e);
		} finally {
			if (arrayInputStream != null) {
				try {
					arrayInputStream.close();
				} catch (IOException e) {
					throw new WebApplicationException(e);
				}
			}
		}
		if (destBufferedImage != null) {
			try {
				ImageIO.write(destBufferedImage, "jpeg", userPhotoFile);
				Materials material;
				List<Materials> materials = materialDAO.findByUserAndType(userId, MaterialType.USER_PHOTO, 0, 1);
				if (materials.isEmpty()) {
					material = new Materials();
					material.setUserId(userId);
					material.setTitle("User Photo");
					material.setType(MaterialType.USER_PHOTO);

				} else {
					material = materials.get(0);
				}
				material.setPhysicalSize(userPhotoFile.length());

				UserPublicProfile userProfile = userProfileDAO.find(userId);
				if ((userProfile != null) && (userProfile.getFirstName() != null)
						&& !userProfile.getFirstName().isEmpty()
						&& userProfile.getLastName() != null
						&& !userProfile.getLastName().isEmpty()) {
					material.setAuthor(userProfile.getFirstName() + " " + userProfile.getLastName());
				}
				if (materials.isEmpty()) {
					materialDAO.create(material);
				} else {
					materialDAO.update(material);
				}

			} catch (IOException e) {
				throw new WebApplicationException(e);
			}
		}
	}

	@GET
	@Path("{id}/photo")
	@Produces("image/jpeg")
	public byte[] getUserPhoto(@PathParam("id") String userId) {
		List<Materials> materials = materialDAO.findByUserAndType(userId, MaterialType.USER_PHOTO, 0, 1);

		String imageName = EncryptionUtil.getMD5(userId);
		BufferedImage bufferedImage;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(2048);
		File imageFile = new File(env.getUserPhotoPathFolder() + imageName + ".jpg");
		if (!imageFile.exists() || materials.isEmpty()) {
			imageFile = new File(env.getUserPhotoPathFolder() + "NO_IMAGE" + ".jpg");
		}
		try {
			bufferedImage = ImageIO.read(imageFile);
			ImageIO.write(bufferedImage, "jpeg", outputStream);
		} catch (IOException ex) {
			Logger.getLogger(ProfilesResource.class.getName()).log(Level.SEVERE, null, ex);
		}
		return outputStream.toByteArray();
	}
}