package com.qbit.p2p.credit.user.resource;

import com.qbit.commons.auth.AuthFilter;
import com.qbit.commons.auth.EncryptionUtil;
import com.qbit.commons.user.UserDAO;
import com.qbit.p2p.credit.commons.model.Currency;
import com.qbit.p2p.credit.env.Env;
import com.qbit.p2p.credit.material.dao.MaterialDAO;
import com.qbit.p2p.credit.material.model.MaterialType;
import com.qbit.p2p.credit.material.model.Materials;
import com.qbit.p2p.credit.user.dao.UserProfileDAO;
import com.qbit.p2p.credit.user.model.Point2;
import com.qbit.p2p.credit.user.model.UserCurrency;
import com.qbit.p2p.credit.user.model.UserPrivateProfile;
import com.qbit.p2p.credit.user.model.UserPublicProfile;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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

		private Point2 startPoint;
		private Point2 endPoint;
		private String imageString;

		public Point2 getStartPoint() {
			return startPoint;
		}

		public void setStartPoint(Point2 startPoint) {
			this.startPoint = startPoint;
		}

		public Point2 getEndPoint() {
			return endPoint;
		}

		public void setEndPoint(Point2 endPoint) {
			this.endPoint = endPoint;
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
				&& (startPoint.getX() >= 0) && (endPoint.getX() > 0)
				&& (endPoint.getX() - startPoint.getX() < MAX_WIDTH)
				&& (endPoint.getX() - startPoint.getX() > MIN_WIDTH)
				&& (startPoint.getY() >= 0) && (endPoint.getY() > 0)
				&& (endPoint.getY() - startPoint.getY() < MAX_HEIGHT)
				&& (endPoint.getY() - startPoint.getY() > MIN_HEIGHT)));

		}

		@Override
		public String toString() {
			return "UserPhotoRequest{" + "startPoint=" + startPoint + ", endPoint=" + endPoint + ", imageString=" + imageString + '}';
		}
	}

	@XmlRootElement
	public static class UsersPublicProfilesWrapper {

		@XmlElement
		@XmlList
		private List<UserPublicProfile> users;
		@XmlElement
		private long length;

		public UsersPublicProfilesWrapper() {
		}

		public UsersPublicProfilesWrapper(List<UserPublicProfile> users, long length) {
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

	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class UserProfileRequest {

		@XmlElement(name = "publicKeyoperator")
		private String publicKeyOperator;
		@XmlElement(name = "filtervalue0")
		private String filterValue0;
		@XmlElement(name = "filtervalue1")
		private String filterValue1;
		@XmlElement(name = "filtervalue2")
		private String filterValue2;
		@XmlElement(name = "filtercondition0")
		private String filterCondition0;
		@XmlElement(name = "filtercondition1")
		private String filterCondition1;
		private String filteroperator0;
		@XmlElement(name = "filterdatafield0")
		private String filterDatafield0;
		@XmlElement(name = "filterdatafield1")
		private String filterDatafield1;

		private String filter;
		@XmlElement(name = "sortorder")
		private String sortOrder;
		private int filterscount;
		@XmlElement(name = "pagenum")
		private int pageNumber;
		@XmlElement(name = "pagesize")
		private int pageSize;
		private int recordstartindex;
		private int recordendindex;
		@XmlElement(name = "sortdatafield")
		private String sortDataField;
		@XmlElement(name = "filterscount")
		private String filtersCount;

		public String getFilteroperator0() {
			return filteroperator0;
		}

		public void setFilteroperator0(String filteroperator0) {
			this.filteroperator0 = filteroperator0;
		}

		public int getFilterscount() {
			return filterscount;
		}

		public void setFilterscount(int filterscount) {
			this.filterscount = filterscount;
		}

		public int getPageSize() {
			return pageSize;
		}

		public void setPageSize(int pageSize) {
			this.pageSize = pageSize;
		}

		public int getRecordstartindex() {
			return recordstartindex;
		}

		public void setRecordstartindex(int recordstartindex) {
			this.recordstartindex = recordstartindex;
		}

		public int getRecordendindex() {
			return recordendindex;
		}

		public void setRecordendindex(int recordendindex) {
			this.recordendindex = recordendindex;
		}

		public String getFilter() {
			return filter;
		}

		public void setFilter(String filter) {
			this.filter = filter;
		}

		public String getPublicKeyOperator() {
			return publicKeyOperator;
		}

		public void setPublicKeyOperator(String publicKeyOperator) {
			this.publicKeyOperator = publicKeyOperator;
		}

		public String getFilterValue0() {
			return filterValue0;
		}

		public void setFilterValue0(String filterValue0) {
			this.filterValue0 = filterValue0;
		}

		public String getFilterCondition0() {
			return filterCondition0;
		}

		public void setFilterCondition0(String filterCondition0) {
			this.filterCondition0 = filterCondition0;
		}

		public String getFilterCondition1() {
			return filterCondition1;
		}

		public void setFilterCondition1(String filterCondition1) {
			this.filterCondition1 = filterCondition1;
		}

		public String getFilterDatafield0() {
			return filterDatafield0;
		}

		public void setFilterDatafield0(String filterDatafield0) {
			this.filterDatafield0 = filterDatafield0;
		}

		public String getFilterDatafield1() {
			return filterDatafield1;
		}

		public void setFilterDatafield1(String filterDatafield1) {
			this.filterDatafield1 = filterDatafield1;
		}

		public String getFilterValue1() {
			return filterValue1;
		}

		public void setFilterValue1(String filterValue1) {
			this.filterValue1 = filterValue1;
		}

		public String getSortOrder() {
			return sortOrder;
		}

		public void setSortOrder(String sortOrder) {
			this.sortOrder = sortOrder;
		}

		public int getPageNumber() {
			return pageNumber;
		}

		public void setPageNumber(int pageNumber) {
			this.pageNumber = pageNumber;
		}

		public String getSortDataField() {
			return sortDataField;
		}

		public void setSortDataField(String sortDataField) {
			this.sortDataField = sortDataField;
		}

		public String getFiltersCount() {
			return filtersCount;
		}

		public void setFiltersCount(String filtersCount) {
			this.filtersCount = filtersCount;
		}

		@Override
		public String toString() {
			return "UserProfileRequest{" + "publicKeyOperator=" + publicKeyOperator + ", filterValue0=" + filterValue0 + ", filterCondition0=" + filterCondition0 + ", filterCondition1=" + filterCondition1 + ", filteroperator0=" + filteroperator0 + ", filterDatafield0=" + filterDatafield0 + ", filterDatafield1=" + filterDatafield1 + ", filterValue1=" + filterValue1 + ", filter=" + filter + ", sortOrder=" + sortOrder + ", filterscount=" + filterscount + ", pageNumber=" + pageNumber + ", pageSize=" + pageSize + ", recordstartindex=" + recordstartindex + ", recordendindex=" + recordendindex + ", sortDataField=" + sortDataField + ", filtersCount=" + filtersCount + '}';
		}
	}

	@Inject
	private Env env;

	@Context
	private HttpServletRequest request;

	@Inject
	private UserProfileDAO userProfileDAO;

	@Inject
	private UserDAO userDAO;

	@Inject
	private MaterialDAO materialDAO;

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
			if (!user.isMailEnabled()) {
				user.setMail(null);
			}
			if (!user.isPhoneEnabled()) {
				user.setPhone(null);
			}
			if (!user.isPersonalDataEnabled()) {
				user.setPersonalData(null);
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

	@POST
	@Path("withFilter")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public UsersPublicProfilesWrapper getWithFilter(UserProfileRequest profile) {
		boolean sortDesc = false;
		if (profile.getSortOrder() != null && profile.getSortOrder().equals("desc")) {
			sortDesc = true;
		}
		List<UserPublicProfile> users = null;
		users = userProfileDAO.findWithFilter(profile.getFilterDatafield0(), profile.getFilter(), profile.getSortDataField(), sortDesc, profile.getPageNumber() * profile.getPageSize(), profile.getPageSize());
		return new UsersPublicProfilesWrapper(users, userProfileDAO.length(profile.getFilterDatafield0(), profile.getFilter()));
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
			destBufferedImage = bufferedImage.getSubimage(userPhoto.getStartPoint().getX(), userPhoto.getStartPoint().getY(),
				userPhoto.getEndPoint().getX() - userPhoto.getStartPoint().getX(), userPhoto.getEndPoint().getY() - userPhoto.getStartPoint().getY());
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
				if ((userProfile != null) && (userProfile.getName() != null)
					&& !userProfile.getName().isEmpty()) {
					material.setAuthor(userProfile.getName());
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
