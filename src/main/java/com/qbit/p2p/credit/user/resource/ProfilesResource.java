package com.qbit.p2p.credit.user.resource;

import com.qbit.commons.auth.AuthFilter;
import com.qbit.commons.auth.EncryptionUtil;
import com.qbit.commons.user.UserDAO;
import com.qbit.p2p.credit.commons.model.Currency;
import com.qbit.p2p.credit.env.Env;
import com.qbit.p2p.credit.material.dao.MaterialDAO;
import com.qbit.p2p.credit.material.model.MaterialType;
import com.qbit.p2p.credit.material.model.Material;
import com.qbit.p2p.credit.order.dao.OrderDAO;
import com.qbit.p2p.credit.order.model.OrderInfo;
import com.qbit.p2p.credit.order.model.OrderStatus;
import com.qbit.p2p.credit.statistics.dao.StatisticsDAO;
import com.qbit.p2p.credit.user.dao.UserProfileDAO;
import com.qbit.p2p.credit.user.model.Language;
import com.qbit.p2p.credit.commons.model.Point2;
import com.qbit.p2p.credit.statistics.model.Statistics;
import com.qbit.p2p.credit.statistics.service.StatisticsService;
import com.qbit.p2p.credit.user.model.ShortProfile;
import com.qbit.p2p.credit.user.model.UserPublicProfile;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

	@XmlRootElement
	public static class LanguagesWrapper {

		@XmlElement
		@XmlList
		private List<Language> languages;

		public LanguagesWrapper() {
		}

		public LanguagesWrapper(List<Language> languages) {
			this.languages = languages;
		}

		public List<Language> getLanguages() {
			return languages;
		}
	}

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

	@Inject
	private StatisticsDAO statisticsDAO;

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
	@Path("short/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public ShortProfile getShortById(@PathParam("id") String id) {
		UserPublicProfile user = userProfileDAO.find(id);
		if (user == null) {
			return null;
		}
		ShortProfile tuple = new ShortProfile();
		tuple.setPublicKey(user.getPublicKey());
		tuple.setName(user.getName());
		tuple.setMail(user.getMail());
		tuple.setPhone(user.getPhone());
		tuple.setCurrencies(user.getCurrencies());
		tuple.setLanguages(user.getLanguages());

		if (!user.isMailEnabled()) {
			tuple.setMail(null);
		}
		if (!user.isPhoneEnabled()) {
			tuple.setPhone(null);
		}
		if (!user.isLanguagesEnabled()) {
			tuple.setLanguages(null);
		}
		if (!user.isCurrenciesEnabled()) {
			tuple.setCurrencies(null);
		}

		return tuple;
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
	@Path("current")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public UserPublicProfile updatePublicProfile(UserPublicProfile userProfile) {
		if (userProfile == null || !userProfile.isValid()) {
			throw new IllegalArgumentException();
		}
		String userId = AuthFilter.getUserId(request);
		userProfile.setPublicKey(userId);
		userProfileDAO.updateUserSocialLinks(userProfile.getSocialLinks(), userId);
		userProfileDAO.updateUserVideos(userProfile.getVideos(), userId);
		userProfileDAO.updateUserBkiData(userProfile.getBkiData(), userId);
		userProfileDAO.updatePassportEnabled(userProfile.isPassportEnabled(), userId);
		UserPublicProfile newProfile = userProfileDAO.updateUserMainAttributes(userProfile);
		if (statisticsDAO.find(userId) == null) {
			statisticsDAO.create(userId);
		}
		Statistics statistics = new Statistics(userId);
		statistics.setOpennessRating(statisticsService.getOpenessRating(userId));
		statistics.setSummaryRating(statisticsService.getSummaryRating(userId));
		statisticsDAO.updateProfileRating(statistics);
		return newProfile;
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
			Point2 newPosition = userPhoto.getEndPoint().subtraction(userPhoto.getStartPoint());
			destBufferedImage = bufferedImage.getSubimage(userPhoto.getStartPoint().getX(), userPhoto.getStartPoint().getY(),
				newPosition.getX(), newPosition.getY());
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
				Material material;
				List<Material> materials = materialDAO.findByUserAndType(userId, MaterialType.USER_PHOTO, 0, 1);
				if (materials.isEmpty()) {
					material = new Material();
					material.setUserId(userId);
					material.setTitle("User Photo");
					material.setType(MaterialType.USER_PHOTO);

				} else {
					material = materials.get(0);
				}
				material.setPhysicalSize(userPhotoFile.length());
				if (materials.isEmpty()) {
					materialDAO.create(material);
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
		List<Material> materials = materialDAO.findByUserAndType(userId, MaterialType.USER_PHOTO, 0, 1);

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
	@Path("languages")
	@Produces(MediaType.APPLICATION_JSON)
	public LanguagesWrapper getOrdersCategories() {
		return new LanguagesWrapper(userProfileDAO.findAllLanguages());
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
			//user.setLanguages(Arrays.asList("Russian, English"));
			user.setLanguagesEnabled(true);
			Currency c1 = Currency.BITCOIN;//new Currency(Currency.BITCOIN, 10, 500);
			Currency c2 = Currency.LITECOIN;//new Currency(Currency.LITECOIN, 10, 500);
			List<Currency> c = new ArrayList<>();
			Collections.addAll(c, c1, c2);

			user.setCurrencies(c);
			user.setCurrenciesEnabled(true);

			//userProfileDAO.updateUserPublicProfile(user);

			OrderInfo order = new OrderInfo();
			order.setUserPublicKey(user.getPublicKey());
			order.setCreationDate(new Date());
			order.setEndDate(new Date());
			order.setStatus(OrderStatus.OPENED);

			//Collections.addAll(c, c1, c2);
			//order.setCurrency(Currency.BITCOIN);
			List<String> l = new ArrayList<>();
			Collections.addAll(l, "Russian", "English");
			//order.setLanguages(l);
			//order.setResponses(rand.nextInt(10));
			/*if(rand.nextInt(3) < 2) {
			 order.setType(OrderType.CREDIT);
			 } else {
			 order.setType(OrderType.BORROW);
			 }*/

			orderDAO.create(order);
		}
	}

	@GET
	@Path("create_language")
	public void createLanguages() {
		String[] languages = {"Abkhaz", "Afar", "Afrikaans", "Akan", "Albanian", "Amharic", "Arabic", "Aragonese", "Armenian", "Assamese", "Avaric", "Avestan", "Aymara", "Azerbaijani", "Bambara", "Bashkir", "Basque", "Belarusian", "Bengali", "Bihari", "Bislama", "Bosnian", "Breton", "Bulgarian", "Burmese", "Catalan; Valencian", "Chamorro", "Chechen", "Chichewa; Chewa; Nyanja", "Chinese", "Chuvash", "Cornish", "Corsican", "Cree", "Croatian", "Czech", "Danish", "Divehi; Dhivehi; Maldivian;", "Dutch", "English", "Esperanto", "Estonian", "Ewe", "Faroese", "Fijian", "Finnish", "French", "Fula; Fulah; Pulaar; Pular", "Galician", "Georgian", "German", "Greek, Modern", "Guaraní", "Gujarati", "Haitian; Haitian Creole", "Hausa", "Hebrew (modern)", "Herero", "Hindi", "Hiri Motu", "Hungarian", "Interlingua", "Indonesian", "Interlingue", "Irish", "Igbo", "Inupiaq", "Ido", "Icelandic", "Italian", "Inuktitut", "Japanese", "Javanese", "Kalaallisut, Greenlandic", "Kannada", "Kanuri", "Kashmiri", "Kazakh", "Khmer", "Kikuyu, Gikuyu", "Kinyarwanda", "Kirghiz, Kyrgyz", "Komi", "Kongo", "Korean", "Kurdish", "Kwanyama, Kuanyama", "Latin", "Luxembourgish, Letzeburgesch", "Luganda", "Limburgish, Limburgan, Limburger", "Lingala", "Lao", "Lithuanian", "Luba-Katanga", "Latvian", "Manx", "Macedonian", "Malagasy", "Malay", "Malayalam", "Maltese", "Māori", "Marathi (Marāṭhī)", "Marshallese", "Mongolian", "Nauru", "Navajo, Navaho", "Norwegian Bokmål", "North Ndebele", "Nepali", "Ndonga", "Norwegian Nynorsk", "Norwegian", "Nuosu", "South Ndebele", "Occitan", "Ojibwe, Ojibwa", "Old Slavonic", "Oromo", "Oriya", "Ossetian, Ossetic", "Panjabi, Punjabi", "Pāli", "Persian", "Polish", "Pashto, Pushto", "Portuguese", "Quechua", "Romansh", "Kirundi", "Romanian, Moldavian, Moldovan", "Russian", "Sanskrit (Saṁskṛta)", "Sardinian", "Sindhi", "Northern Sami", "Samoan", "Sango", "Serbian", "Scottish Gaelic; Gaelic", "Shona", "Sinhala, Sinhalese", "Slovak", "Slovene", "Somali", "Southern Sotho", "Spanish; Castilian", "Sundanese", "Swahili", "Swati", "Swedish", "Tamil", "Telugu", "Tajik", "Thai", "Tigrinya", "Tibetan Standard, Tibetan, Central", "Turkmen", "Tagalog", "Tswana", "Tonga (Tonga Islands)", "Turkish", "Tsonga", "Tatar", "Twi", "Tahitian", "Uighur, Uyghur", "Ukrainian", "Urdu", "Uzbek", "Venda", "Vietnamese", "Volapük", "Walloon", "Welsh", "Wolof", "Western Frisian", "Xhosa", "Yiddish", "Yoruba", "Zhuang, Chuang"};
		for (String l : languages) {
			userProfileDAO.createLanguage(l);
		}
	}
}
