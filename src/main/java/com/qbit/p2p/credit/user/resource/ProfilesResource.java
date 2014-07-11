package com.qbit.p2p.credit.user.resource;

import com.qbit.commons.auth.AuthFilter;
import com.qbit.commons.auth.EncryptionUtil;
import com.qbit.commons.user.UserDAO;
import com.qbit.p2p.credit.commons.model.Currency;
import com.qbit.p2p.credit.env.Env;
import com.qbit.p2p.credit.material.dao.MaterialDAO;
import com.qbit.p2p.credit.material.model.MaterialType;
import com.qbit.p2p.credit.material.model.Materials;
import com.qbit.p2p.credit.order.dao.OrderDAO;
import com.qbit.p2p.credit.order.model.FilterCondition;
import com.qbit.p2p.credit.order.model.FilterItem;
import com.qbit.p2p.credit.order.model.SearchRequest;
import com.qbit.p2p.credit.order.model.FilterOperator;
import com.qbit.p2p.credit.order.model.OrderInfo;
import com.qbit.p2p.credit.order.model.OrderStatus;
import com.qbit.p2p.credit.user.dao.UserProfileDAO;
import com.qbit.p2p.credit.user.model.Point2;
import com.qbit.p2p.credit.user.model.Statistic;
import com.qbit.p2p.credit.user.model.UserPrivateProfile;
import com.qbit.p2p.credit.user.model.UserPublicProfile;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
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
import javax.ws.rs.PUT;
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

	@Inject
	private Env env;

	@Context
	private HttpServletRequest request;

	@Inject
	private UserProfileDAO userProfileDAO;
	
	@Inject
	private OrderDAO orderDAO;

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
			profile.setStatistic(getStatistic(userId));
			return userProfileDAO.updateUserPublicProfile(profile);
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

	@GET
	@Path("byOrder")
	@Produces(MediaType.APPLICATION_JSON)
	public List<UserPublicProfile> getByOrder(@QueryParam("id") String id,
		@QueryParam("offset") int offset, @QueryParam("limit") int limit) {
		return userProfileDAO.findByOrder(id, offset, limit);
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
		userProfile.setStatistic(getStatistic(userId));
		return userProfileDAO.updateUserPublicProfile(userProfile);
	}
	
	private Statistic getStatistic(String publicKey) {
		Statistic statistic = new Statistic();
		long openessRating = 0;
		long transactionsRating = 0;
		long allOrders = 0;
		long allTransactions = 0;
		long allSuccessTransactions = 0;
		long allUsersTransactions = 0;
		long allUsersSuccessTransactions = 0;
		UserPublicProfile user = userProfileDAO.find(publicKey);
		if(user == null) {
			return null;
		}
		if((user.getName() != null) && !user.getName().isEmpty()) {
			openessRating = openessRating + 5;
		}
		if(user.isPassportEnabled()) {
			openessRating = openessRating + 5;
		}
		if(user.isPersonalDataEnabled()) {
			openessRating = openessRating + 5;
		}
		if(user.isMailEnabled() && (user.getMail() != null) && !user.getMail().isEmpty()) {
			openessRating = openessRating + 5;
		}
		if(user.isPhoneEnabled() && (user.getPhone() != null) && !user.getPhone().isEmpty()) {
			openessRating = openessRating + 5;
		}
		if((user.getBkiData() != null) && !user.getBkiData().isEmpty()) {
			openessRating = openessRating + 5;
		}
		if(user.getVideos() != null) {
			openessRating = openessRating + user.getVideos().size() * 2;
		}
		if(user.getPhones()!= null) {
			openessRating = openessRating + user.getPhones().size() * 3;
		}
		if(user.getSocialLinks()!= null) {
			openessRating = openessRating + user.getSocialLinks().size() * 3;
		}
		if(user.getNamesLinks()!= null) {
			openessRating = openessRating + user.getNamesLinks().size() * 3;
		}
		statistic.setOpennessRating(openessRating);
		SearchRequest filter = new SearchRequest();
		FilterItem filterItem = new FilterItem();
		filterItem.setFilterDataField("status");
		filterItem.setFilterValue("SUCCESS");
		filterItem.setFilterOperator(FilterOperator.AND);
		filterItem.setFilterCondition(FilterCondition.EQUAL);
		filter.setFilterItems(Arrays.asList(filterItem));
		long successLength = orderDAO.getLengthWithFilter(publicKey, filter, null);
		
		filterItem.setFilterValue("NOT_SUCCESS");
		filter.setFilterItems(Arrays.asList(filterItem));
		long notSuccessLength = orderDAO.getLengthWithFilter(publicKey, filter, null);
			transactionsRating = successLength - notSuccessLength;
		statistic.setTransactionsRating(transactionsRating);
		allOrders = orderDAO.getLengthWithFilter(publicKey, null, null);
		//if(allOrdersData != null) {
		//	allOrders = allOrdersData.getLength();
		//}
		statistic.setOrdersSumValue(allOrders);
		filterItem.setFilterCondition(FilterCondition.NOT_EQUAL);
		filterItem.setFilterValue("OPENED");
		filter.setFilterItems(Arrays.asList(filterItem));
		allTransactions = orderDAO.getLengthWithFilter(publicKey, filter, null);
		//if(allOrdersData != null) {
		//	allTransactions = allTransactionsData.getLength();
		//}
		statistic.setTransactionsSum(allTransactions);
		System.out.println("!! RATING: " + openessRating + " " + allTransactions);
		statistic.setSummaryRating((long)(openessRating * env.getUserOpenessRatingFactor()) + (long)(allTransactions * env.getUserAllTransactionsFactor()));
		
		allUsersTransactions = orderDAO.getLengthWithFilter(null, filter, null);
		//if(allUsersTransactionsData != null) {
		//	allUsersTransactions = allUsersTransactionsData.getLength();
		//}
		statistic.setAllTransactionsSum(allUsersTransactions);
		
		filterItem.setFilterCondition(FilterCondition.EQUAL);
		filterItem.setFilterValue("SUCCESS");
		filter.setFilterItems(Arrays.asList(filterItem));
		allSuccessTransactions = orderDAO.getLengthWithFilter(publicKey, filter, null);
		//if(allOrdersData != null) {
		//	allSuccessTransactions = allSuccessTransactionsData.getLength();
		//}
		statistic.setSuccessTransactionsSum(allSuccessTransactions);
		
		allUsersSuccessTransactions = orderDAO.getLengthWithFilter(null, filter, null);
		//if(allUsersSuccessTransactionsData != null) {
		//	allUsersSuccessTransactions = allUsersSuccessTransactionsData.getLength();
		//}
		statistic.setAllSuccessTransactionsSum(allUsersSuccessTransactions);
		
		return statistic;

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

	@PUT
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

	@GET
	@Path("random")
	public void random() {
		Random rand = new Random();
		char[] symbols = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n'};
		for (int i = 0; i < 1000; i++) {

			String publicKey = "";
			for (int j = 0; j < 5; j++) {
				publicKey = publicKey + symbols[rand.nextInt(14)];
			}
			userDAO.create(publicKey + i + "@mail.ru");
			UserPublicProfile user = userProfileDAO.create(publicKey + i + "@mail.ru");

			publicKey = "";
			for (int j = 0; j < 5; j++) {
				publicKey = publicKey + symbols[rand.nextInt(14)];
			}
			user.setName(publicKey);
			publicKey = "";
			for (int j = 0; j < 5; j++) {
				publicKey = publicKey + symbols[rand.nextInt(14)];
			}
			user.setMail(publicKey + i + "@mail.ru");
			user.setMailEnabled(true);
			user.setLanguages(Arrays.asList("Russian, English"));
			user.setLanguagesEnabled(true);
			Currency c1 = Currency.BITCOIN;//new Currency(Currency.BITCOIN, 10, 500);
			Currency c2 = Currency.LITECOIN;//new Currency(Currency.LITECOIN, 10, 500);
			List<Currency> c = new ArrayList<>();
			Collections.addAll(c, c1, c2);

			user.setCurrencies(c);
			user.setCurrenciesEnabled(true);

			userProfileDAO.updateUserPublicProfile(user);

			OrderInfo order = new OrderInfo();
			order.setUserPublicKey(user.getPublicKey());
			order.setCreationDate(new Date());
			order.setEndDate(new Date());
			order.setReward(String.valueOf(rand.nextInt(1000)));
			order.setStatus(OrderStatus.OPENED);

			//Collections.addAll(c, c1, c2);
			//order.setCurrency(Currency.BITCOIN);

			List<String> l = new ArrayList<>();
			Collections.addAll(l, "Russian", "English");
			order.setLanguages(l);
			order.setResponses(rand.nextInt(10));
			/*if(rand.nextInt(3) < 2) {
				order.setType(OrderType.CREDIT);
			} else {
				order.setType(OrderType.BORROW);
			}*/

			orderDAO.create(order);
		}
	}
}
