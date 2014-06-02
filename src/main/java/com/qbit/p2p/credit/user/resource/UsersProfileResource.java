package com.qbit.p2p.credit.user.resource;

import com.qbit.commons.auth.AuthFilter;
import com.qbit.commons.auth.EncryptionUtil;
import com.qbit.p2p.credit.env.Env;
import com.qbit.p2p.credit.material.dao.MaterialDAO;
import com.qbit.p2p.credit.material.model.MaterialType;
import com.qbit.p2p.credit.material.model.Materials;
import com.qbit.p2p.credit.user.dao.UserProfileDAO;
import com.qbit.p2p.credit.user.model.UserPrivateProfile;
import com.qbit.p2p.credit.user.model.UserPublicProfile;
import com.qbit.p2p.credit.user.model.UserType;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
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
@Path("users-profile")
@Singleton
public class UsersProfileResource {

	public static class UserPhotoRequest {

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

	private static final int MAX_WIDTH = 1000;
	private static final int MAX_HEIGHT = 1000;
	private static final int MAX_IMAGE_STRING_LENGTH = 1000000;

	@Inject
	Env env;

	@Context
	private HttpServletRequest request;

	@Inject
	UserProfileDAO userProfileDAO;

	@Inject
	MaterialDAO materialDAO;

	@GET
	@Path("current")
	@Produces(MediaType.APPLICATION_JSON)
	public UserPublicProfile current() {
		String userId = AuthFilter.getUserId(request);
		UserPublicProfile user = userProfileDAO.find(userId);
		if (user == null) {
			user = userProfileDAO.create(userId);
		}

		return user;
	}

	@GET
	@Path("byId")
	@Produces(MediaType.APPLICATION_JSON)
	public UserPublicProfile getById(@QueryParam("id") String id) {
		UserPublicProfile user = userProfileDAO.find(id);
		if (user != null) {
			if (!user.isAgeEnabled()) {
				user.setAge(0);
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
	@Path("users")
	@Produces(MediaType.APPLICATION_JSON)
	public UsersPublicProfilesWrapper getAll(@QueryParam("filterslength") int filterslength, @QueryParam("sortdatafield") String sortDataField, 
			@QueryParam("sortorder") String sortOrder, @QueryParam("pagenum") int pagenum, @QueryParam("pagesize") int limit) {
		boolean sortDesc = false;
		if(sortOrder != null && sortOrder.equals("desc")) {
			sortDesc = true;
		}
		return new UsersPublicProfilesWrapper(userProfileDAO.findAll(sortDataField, sortDesc, pagenum * limit, limit), userProfileDAO.length());
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
		if (userProfile == null || !userProfile.isValid()) {
			throw new IllegalArgumentException();
		}
		String userId = AuthFilter.getUserId(request);
		userProfile.setPublicKey(userId);
		long rating = 0;
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
		if (userProfile.getAge() != 0) {
			rating = rating + 5;
		}
		if (userProfile.isAgeEnabled()) {
			rating = rating + 5;
		}
		userProfile.setRating(rating);

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

	@POST
	@Path("setUserPhoto")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void setUserPhoto(UserPhotoRequest userPhoto) {
		//if (fileSize > 1024 * 1024 * MAX_SIZE_IN_MB) {
		//	//throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity("Image is larger than " + MAX_SIZE_IN_MB + "MB").build());
		//}
		if (userPhoto.getImageString().length() > MAX_IMAGE_STRING_LENGTH) {
			throw new WebApplicationException();
		}

		String userId = AuthFilter.getUserId(request);
		String imageName = EncryptionUtil.getMD5(userId);
		File userPhotoFile = new File(env.getUserPhotoPathFolder() + imageName + ".jpg");
		
		if(userPhoto.getImageString().isEmpty() && userPhotoFile.exists()) {
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
			if (destBufferedImage.getHeight() > MAX_HEIGHT || destBufferedImage.getWidth() > MAX_WIDTH) {
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
				if(materials.isEmpty()) {
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
	@Path("user-photo")
	@Produces("image/jpeg")
	public byte[] getUserPhoto(@QueryParam("userId") String userId) {
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
			Logger.getLogger(UsersProfileResource.class.getName()).log(Level.SEVERE, null, ex);
		}
		return outputStream.toByteArray();
	}
}
