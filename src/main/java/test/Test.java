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
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.glassfish.jersey.client.ClientConfig;

/**
 * @author Alex
 */
public class Test {

	public final static String VK_FRIENDS_API_BASE_URL = "https://api.vk.com/method/";

	public static void main(String args[]) throws JAXBException, IOException, ParseException {
		SocialValueRequest valueRequest = new SocialValueRequest();
		String path = ("users.get");
		Client client = ClientBuilder.newClient(new ClientConfig());
		Invocation.Builder builder = client.target(VK_FRIENDS_API_BASE_URL).path(path)
				.queryParam("fields", "city, country, sex, bdate, counters, relatives, relation, connections")
				.queryParam("user_ids", "13768234")
				.queryParam("v", "5.24")
				.queryParam("access_token", "7d018abd36820cbdf4fc779751a389a99d6bc6b4c55f14bf5ac4edbb28fe599c26d4f7b3d1c4209057e62").request(MediaType.APPLICATION_JSON_TYPE);
		
		String response = builder.get(String.class);
		response = response.replace("{\"response\":[", "");
		response = response.substring(0, response.lastIndexOf("]}"));
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(response);
		System.out.println("HASH: " + getUserHash(response, jsonNode));
		System.out.println("SEX: " + getSex(response, jsonNode));
		System.out.println("AGE: " + getAge(response, jsonNode));
		System.out.println("CITY: " + getCity(response, jsonNode));
		System.out.println("COUNTRY: " + getCountry(response, jsonNode));
		System.out.println("ALBUMS: " + getAlbumsCount(response, jsonNode));
		System.out.println("VIDEO: " + getVideoCount(response, jsonNode));
		System.out.println("AUDIO: " + getAudioCount(response, jsonNode));
		System.out.println("NOTE: " + getNotesCount(response, jsonNode));
		System.out.println("PHOTO: " + getPhotoCount(response, jsonNode));
		System.out.println("GROUPS: " + getGroupsCount(response, jsonNode));
		System.out.println("FRIENDS: " + getFriendsCount(response, jsonNode));
		System.out.println("FOLLOWERS: " + getFollowersCount(response, jsonNode));
		System.out.println("RELATIVES: " + getRelatives(response, jsonNode));
		System.out.println("FAMILY_STATUS: " + getFamilyStatus(response, jsonNode));
		System.out.println("SKYPE: " + getSkype(response, jsonNode));
		System.out.println("FACEBOOK: " + getFacebook(response, jsonNode));
		System.out.println("TWITTER: " + getTwitter(response, jsonNode));
		System.out.println("INSTAGRAM: " + getInstagram(response, jsonNode));
		System.out.println("LIVEJOURNAL: " + getLivejournal(response, jsonNode));
			
	}

	private static String getUserHash(String response, JsonNode jsonNode) throws IOException {
		if ((response == null) || response.isEmpty() || (jsonNode == null)) {
			return null;
		}
		JsonNode firstName = jsonNode.get("first_name");
		JsonNode lastName = jsonNode.get("last_name");
		JsonNode id = jsonNode.get("id");
		String common = firstName.asText() + lastName.asText() + id.asText();
		return EncryptionUtil.getMD5(common);
	}

	private static int getSex(String response, JsonNode jsonNode) throws IOException {
		if ((response == null) || response.isEmpty() || (jsonNode == null)) {
			return -1;
		}
		JsonNode sex = jsonNode.findValue("sex");
		if ((sex == null) || (sex.asInt() == 0)) {
			return -1;
		}
		return (sex.asInt() == 1) ? 0 : 1;
	}

	private static int getAge(String response, JsonNode jsonNode) throws IOException, ParseException {
		if ((response == null) || response.isEmpty() || (jsonNode == null)) {
			return -1;
		}
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
	
	private static String getCity(String response, JsonNode jsonNode) throws IOException {
		if ((response == null) || response.isEmpty() || (jsonNode == null)) {
			return null;
		}
		JsonNode city = jsonNode.findPath("city").get("title");
		return (city == null) ? null : city.asText();
	}
	
	private static String getCountry(String response, JsonNode jsonNode) throws IOException {
		if ((response == null) || response.isEmpty() || (jsonNode == null)) {
			return null;
		}
		JsonNode country = jsonNode.findPath("country").get("title");
		return (country == null) ? null : country.asText();
	}
	
	private static int getElementCount(String response, JsonNode jsonNode, String element) throws IOException {
		if ((response == null) || response.isEmpty() || (jsonNode == null)) {
			return -1;
		}
		JsonNode count = jsonNode.findPath("counters").get(element);
		return (count == null) ? null : count.asInt();
	}
	
	private static int getAlbumsCount(String response, JsonNode jsonNode) throws IOException {
		return getElementCount(response, jsonNode, "albums");
	}
	
	private static int getVideoCount(String response, JsonNode jsonNode) throws IOException {
		return getElementCount(response, jsonNode, "videos");
	}
	
	private static int getAudioCount(String response, JsonNode jsonNode) throws IOException {
		return getElementCount(response, jsonNode, "audios");
	}
	
	private static int getNotesCount(String response, JsonNode jsonNode) throws IOException {
		return getElementCount(response, jsonNode, "notes");
	}
	
	private static int getPhotoCount(String response, JsonNode jsonNode) throws IOException {
		return getElementCount(response, jsonNode, "photos");
	}
	
	private static int getGroupsCount(String response, JsonNode jsonNode) throws IOException {
		return getElementCount(response, jsonNode, "groups");
	}
	
	private static int getFriendsCount(String response, JsonNode jsonNode) throws IOException {
		return getElementCount(response, jsonNode, "friends");
	}
	
	private static int getFollowersCount(String response, JsonNode jsonNode) throws IOException {
		return getElementCount(response, jsonNode, "followers");
	}
	
	private static String getRelatives(String response, JsonNode jsonNode) throws IOException {
		if ((response == null) || response.isEmpty() || (jsonNode == null)) {
			return "";
		}
		List<String> relatives = jsonNode.get("relatives").findValuesAsText("id");
		
		if((relatives == null) || relatives.isEmpty()) {
			return "";
		}
		String relativesIds = "";
		for (String id : relatives) {
			relativesIds += id + " ";
		}
		return relativesIds;
	}
	
	private static String getFamilyStatus(String response, JsonNode jsonNode) throws IOException {
		if ((response == null) || response.isEmpty() || (jsonNode == null)) {
			return null;
		}
		JsonNode familyStatus = jsonNode.get("relation");
		if(familyStatus == null) { 
			return null;
		}
		switch(familyStatus.asInt()) {
			case 1 : return "не женат/не замужем";
			case 2 : return "есть друг/есть подруга";
			case 3 : return "помолвлен/помолвлена";
			case 4 : return "женат/замужем";
			case 5 : return "всё сложно";
			case 6 : return "в активном поиске";
			case 7 : return "влюблён/влюблена";
			default: return null;
		}
	}
	
	private static String getElement(String response, JsonNode jsonNode, String nodeStr) throws IOException {
		if ((response == null) || response.isEmpty() || (jsonNode == null)) {
			return null;
		}
		JsonNode node = jsonNode.get(nodeStr);
		return (node == null) ? null : node.asText();
	}
	
	private static String getSkype(String response, JsonNode jsonNode) throws IOException {
		return getElement(response, jsonNode, "skype");
	}
	
	private static String getFacebook(String response, JsonNode jsonNode) throws IOException {
		return getElement(response, jsonNode, "facebook");
	}
	
	private static String getTwitter(String response, JsonNode jsonNode) throws IOException {
		return getElement(response, jsonNode, "twitter");
	}
	
	private static String getInstagram(String response, JsonNode jsonNode) throws IOException {
		return getElement(response, jsonNode, "instagram");
	}
	
	private static String getLivejournal(String response, JsonNode jsonNode) throws IOException {
		return getElement(response, jsonNode, "livejournal");
	}
	
	
	
	
}
