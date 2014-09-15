package test;

import com.qbit.commons.crypto.util.EncryptionUtil;
import static com.qbit.commons.rest.util.RESTClientUtil.*;
import com.qbit.commons.socialvalues.SocialValueRequest;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.JAXBException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.glassfish.jersey.client.ClientConfig;

/**
 * @author Alex
 */
public class Test {

	public final static String VK_API_BASE_URL = "https://api.vk.com/method/";
	public final static String SOCIALVALUES_API_BASE_URL = "http://socialvalues.co/api/estimate";

	public static void main(String args[]) throws JAXBException, IOException, ParseException {
		SocialValueRequest valueRequest = new SocialValueRequest();

		String path = ("users.get");
		Client client = ClientBuilder.newClient(new ClientConfig());
		Invocation.Builder builder = client.target(VK_API_BASE_URL).path(path)
				.queryParam("fields", "city, country, sex, bdate, counters, relatives, relation, connections")
				.queryParam("user_ids", "13768234")
				.queryParam("v", "5.24")
				.queryParam("access_token", "68dca5ec15e936a7c9737fdae1221737227c3e09c40654538b1dbea6b3c7d8bb32d5cfc484ce5705060c7")
				.request(MediaType.APPLICATION_JSON_TYPE);

		String response = builder.get(String.class);
		response = response.replace("{\"response\":[", "");
		response = response.substring(0, response.lastIndexOf("]}"));
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(response);
		System.out.println("HASH: " + getUserHash(jsonNode));
		System.out.println("SEX: " + getSex(jsonNode));
		System.out.println("AGE: " + getAge(jsonNode));
		System.out.println("CITY: " + getCity(jsonNode));
		System.out.println("COUNTRY: " + getCountry(jsonNode));
		System.out.println("ALBUMS: " + getAlbumsCount(jsonNode));
		System.out.println("VIDEO: " + getVideoCount(jsonNode));
		System.out.println("AUDIO: " + getAudioCount(jsonNode));
		System.out.println("NOTE: " + getNotesCount(jsonNode));
		System.out.println("PHOTO: " + getPhotoCount(jsonNode));
		System.out.println("GROUPS: " + getGroupsCount(jsonNode));
		System.out.println("FRIENDS: " + getFriendsCount(jsonNode));
		System.out.println("FOLLOWERS: " + getFollowersCount(jsonNode));
		System.out.println("RELATIVES: " + getRelatives(jsonNode));
		System.out.println("FAMILY_STATUS: " + getFamilyStatus(jsonNode));
		System.out.println("SKYPE: " + getSkype(jsonNode));
		System.out.println("FACEBOOK: " + getFacebook(jsonNode));
		System.out.println("TWITTER: " + getTwitter(jsonNode));
		System.out.println("INSTAGRAM: " + getInstagram(jsonNode));
		System.out.println("LIVEJOURNAL: " + getLivejournal(jsonNode));

		path = ("wall.get");
		Invocation.Builder wallBuilder = client.target(VK_API_BASE_URL).path(path)
				.queryParam("owner_id", "13768234")
				.queryParam("count", "1")
				.queryParam("v", "5.24")
				//.queryParam("access_token", "7d018abd36820cbdf4fc779751a389a99d6bc6b4c55f14bf5ac4edbb28fe599c26d4f7b3d1c4209057e62")
				.request(MediaType.APPLICATION_JSON_TYPE);
		String wallResponse = wallBuilder.get(String.class);
		JsonNode wallJsonNode = objectMapper.readTree(wallResponse);
		System.out.println(wallResponse);
		System.out.println("POSTS_COUNT: " + getPostsCount(wallJsonNode));
		System.out.println("LAST_POST_DATE: " + getPostDate(wallJsonNode));

		Invocation.Builder firstWallBuilder = client.target(VK_API_BASE_URL).path(path)
				.queryParam("owner_id", "13768234")
				.queryParam("count", "1")
				.queryParam("offset", getPostsCount(wallJsonNode) - 1)
				.queryParam("v", "5.24")
				//.queryParam("access_token", "7d018abd36820cbdf4fc779751a389a99d6bc6b4c55f14bf5ac4edbb28fe599c26d4f7b3d1c4209057e62")
				.request(MediaType.APPLICATION_JSON_TYPE);
		String firstWallResponse = firstWallBuilder.get(String.class);
		JsonNode firstWallJsonNode = objectMapper.readTree(firstWallResponse);
		System.out.println("FIRST_POST_DATE: " + getPostDate(firstWallJsonNode));
		
		
		Invocation.Builder pagesWallBuilder = client.target(VK_API_BASE_URL).path("users.getSubscriptions")
				.queryParam("user_id", "13768234")
				.queryParam("count", "1")
				.queryParam("extended", "0")
				.queryParam("v", "5.24")
				//.queryParam("access_token", "7d018abd36820cbdf4fc779751a389a99d6bc6b4c55f14bf5ac4edbb28fe599c26d4f7b3d1c4209057e62")
				.request(MediaType.APPLICATION_JSON_TYPE);
		System.out.println("@@@ " + pagesWallBuilder.get(String.class));
		String pagesResponse = pagesWallBuilder.get(String.class);
		JsonNode pagesJsonNode = objectMapper.readTree(pagesResponse);
		System.out.println("SUBSCRIPTIONS: " + getSubscriptionsCount(pagesJsonNode));
		
		System.out.println("PAGES: " + getPagesCount(pagesJsonNode));
		
		/*Invocation.Builder socialBuilder = client.target(SOCIALVALUES_API_BASE_URL)
		 .queryParam("user_hash", getUserHash(jsonNode))
		 .queryParam("sex", getSex(jsonNode))
		 .queryParam("age", getAge(jsonNode))
		 .queryParam("city", getCity(jsonNode))
		 .queryParam("country", getCountry(jsonNode))
		 .queryParam("albums_count", getAlbumsCount(jsonNode))
		 .queryParam("video_count", getVideoCount(jsonNode))
		 //.queryParam("access_token", "7d018abd36820cbdf4fc779751a389a99d6bc6b4c55f14bf5ac4edbb28fe599c26d4f7b3d1c4209057e62")
		 .request(MediaType.APPLICATION_JSON_TYPE);*/
		
		Invocation.Builder socialBuilder = client.target(SOCIALVALUES_API_BASE_URL).request(MediaType.APPLICATION_FORM_URLENCODED);
		MultivaluedMap<String, String> map = new MultivaluedHashMap<>();

		map.add("token", "fc9c3560373611e4a50104011c471c01");
		map.add("user_hash", getUserHash(jsonNode));
		map.add("sex", String.valueOf(getSex(jsonNode)));
		map.add("age", String.valueOf(getAge(jsonNode)));
		map.add("city", getCity(jsonNode));
		map.add("country", getCountry(jsonNode));
		map.add("albums_count", String.valueOf(getAlbumsCount(jsonNode)));
		map.add("video_count", String.valueOf(getVideoCount(jsonNode)));
		map.add("audio_count", String.valueOf(getAudioCount(jsonNode)));
		map.add("notes_count", String.valueOf(getNotesCount(jsonNode)));
		map.add("photo_count", String.valueOf(getPhotoCount(jsonNode)));
		map.add("groups_count", String.valueOf(getGroupsCount(jsonNode)));
		map.add("friends_count", String.valueOf(getFriendsCount(jsonNode)));
		map.add("followers_count", String.valueOf(getFollowersCount(jsonNode)));
		map.add("pages_count", String.valueOf(getGroupsCount(jsonNode)));//getPagesCount(jsonNode)));
		map.add("relativities", getRelatives(jsonNode));
		map.add("family_status", "не женат/не замужем");//getFamilyStatus(jsonNode));
		map.add("skype", getSkype(jsonNode));
		map.add("facebook", getFacebook(jsonNode));
		//map.add("twitter", getTwitter(jsonNode));
		//map.add("livejournal", getLivejournal(jsonNode));
		//map.add("instagram", getInstagram(jsonNode));
		map.add("last_post_dt", getPostDate(wallJsonNode));
		map.add("first_post_dt", getPostDate(firstWallJsonNode));
		map.add("posts_count", String.valueOf(getPostsCount(wallJsonNode)));
		map.add("subscriptions_count", String.valueOf(getGroupsCount(jsonNode)));
		map.add("likes_count", String.valueOf(1234));

		System.out.println("^^ " + socialBuilder.post(Entity.form(map), String.class));
	}

	private static String getUserHash(JsonNode jsonNode) throws IOException {
		JsonNode firstName = jsonNode.get("first_name");
		JsonNode lastName = jsonNode.get("last_name");
		JsonNode id = jsonNode.get("id");
		String common = firstName.asText() + lastName.asText() + id.asText();
		return EncryptionUtil.getMD5(common);
	}

	private static int getSex(JsonNode jsonNode) throws IOException {
		JsonNode sex = jsonNode.findValue("sex");
		if ((sex == null) || (sex.asInt() == 0)) {
			return -1;
		}
		return (sex.asInt() == 1) ? 0 : 1;
	}

	private static int getAge(JsonNode jsonNode) throws IOException, ParseException {
		JsonNode bDateNode = jsonNode.findValue("bdate");
		if (bDateNode == null) {
			return -1;
		}
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		Date bDate = dateFormat.parse(bDateNode.asText());
		Calendar bdateCalendar = Calendar.getInstance();
		bdateCalendar.setTime(bDate);
		Calendar today = Calendar.getInstance();
		int age = today.get(Calendar.YEAR) - bdateCalendar.get(Calendar.YEAR);
		if (today.get(Calendar.DAY_OF_YEAR) <= bdateCalendar.get(Calendar.DAY_OF_YEAR)) {
			age--;
		}
		return age;
	}

	private static String getCity(JsonNode jsonNode) throws IOException {
		JsonNode city = jsonNode.findPath("city").get("title");
		return (city == null) ? null : city.asText();
	}

	private static String getCountry(JsonNode jsonNode) throws IOException {
		JsonNode country = jsonNode.findPath("country").get("title");
		return (country == null) ? null : country.asText();
	}

	private static int getElementCount(JsonNode jsonNode, String element) throws IOException {
		JsonNode count = jsonNode.findPath("counters").get(element);
		return (count == null) ? null : count.asInt();
	}

	private static int getAlbumsCount(JsonNode jsonNode) throws IOException {
		return getElementCount(jsonNode, "albums");
	}

	private static int getVideoCount(JsonNode jsonNode) throws IOException {
		return getElementCount(jsonNode, "videos");
	}

	private static int getAudioCount(JsonNode jsonNode) throws IOException {
		return getElementCount(jsonNode, "audios");
	}

	private static int getNotesCount(JsonNode jsonNode) throws IOException {
		return getElementCount(jsonNode, "notes");
	}

	private static int getPhotoCount(JsonNode jsonNode) throws IOException {
		return getElementCount(jsonNode, "photos");
	}

	private static int getGroupsCount(JsonNode jsonNode) throws IOException {
		return getElementCount(jsonNode, "groups");
	}

	private static int getFriendsCount(JsonNode jsonNode) throws IOException {
		return getElementCount(jsonNode, "friends");
	}

	private static int getFollowersCount(JsonNode jsonNode) throws IOException {
		return getElementCount(jsonNode, "followers");
	}

	private static String getRelatives(JsonNode jsonNode) throws IOException {
		List<String> relatives = jsonNode.get("relatives").findValuesAsText("id");

		if ((relatives == null) || relatives.isEmpty()) {
			return "";
		}
		String relativesIds = "";
		for (String id : relatives) {
			relativesIds += id + ",";
		}
		if(!relativesIds.isEmpty()) {
			relativesIds = relativesIds.substring(0, relativesIds.length() - 1);
		}
		return relativesIds;
	}

	private static String getFamilyStatus(JsonNode jsonNode) throws IOException {
		JsonNode familyStatus = jsonNode.get("relation");
		System.out.println("FAMILY:   " + familyStatus);
		if (familyStatus == null) {
			return null;
		}
		switch (familyStatus.asInt()) {
			case 1:
				return "не женат/не замужем";
			case 2:
				return "есть друг/есть подруга";
			case 3:
				return "помолвлен/помолвлена";
			case 4:
				return "женат/замужем";
			case 5:
				return "всё сложно";
			case 6:
				return "в активном поиске";
			case 7:
				return "влюблён/влюблена";
			default:
				return null;
		}
	}

	private static String getElement(JsonNode jsonNode, String nodeStr) throws IOException {
		JsonNode node = jsonNode.get(nodeStr);
		return (node == null) ? null : node.asText();
	}

	private static String getSkype(JsonNode jsonNode) throws IOException {
		return getElement(jsonNode, "skype");
	}

	private static String getFacebook(JsonNode jsonNode) throws IOException {
		return getElement(jsonNode, "facebook");
	}

	private static String getTwitter(JsonNode jsonNode) throws IOException {
		return getElement(jsonNode, "twitter");
	}

	private static String getInstagram(JsonNode jsonNode) throws IOException {
		return getElement(jsonNode, "instagram");
	}

	private static String getLivejournal(JsonNode jsonNode) throws IOException {
		return getElement(jsonNode, "livejournal");
	}

	private static int getPostsCount(JsonNode jsonNode) throws IOException {
		JsonNode count = jsonNode.findPath("response").get("count");
		return (count == null) ? -1 : count.asInt();
	}

	private static String getPostDate(JsonNode jsonNode) throws IOException {
		JsonNode date = jsonNode.findPath("items").findValue("date");
		if (date == null) {
			return null;
		}
		DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
		Date lastDate = new Date(date.asLong() * 1000);
		return dateFormat.format(lastDate);
	}
	
	private static int getSubscriptionsCount(JsonNode jsonNode) throws IOException {
		JsonNode count = jsonNode.findPath("users").get("count");
		return (count == null) ? null : count.asInt();
	}
	
	private static int getPagesCount(JsonNode jsonNode) throws IOException {
		JsonNode count = jsonNode.findPath("groups").get("count");
		return (count == null) ? null : count.asInt();
	}
}
