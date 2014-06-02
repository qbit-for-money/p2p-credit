package com.qbit.p2p.credit.user.model;

import java.io.Serializable;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Alexander_Sergeev
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "UserPublicProfile.findByType",
			query = "SELECT u FROM UserPublicProfile u WHERE u.userType = :type"),
	@NamedQuery(name = "UserPublicProfile.findByOrdersNoMoreThan",
			query = "SELECT u FROM UserPublicProfile u WHERE "
			+ "(SELECT count(o) FROM OrderInfo o WHERE o.userPublicKey = u.publicKey) <= :number"),
	@NamedQuery(name = "UserPublicProfile.findByOrdersNoLessThan",
			query = "SELECT u FROM UserPublicProfile u WHERE "
			+ "(SELECT count(o) FROM OrderInfo o WHERE o.userPublicKey = u.publicKey) >= :number"),
	@NamedQuery(name = "UserPublicProfile.findByRatingNoMoreThan",
			query = "SELECT u FROM UserPublicProfile u WHERE u.rating <= :rating"),
	@NamedQuery(name = "UserPublicProfile.findByRatingNoLessThan",
			query = "SELECT u FROM UserPublicProfile u WHERE u.rating >= :rating"),
	@NamedQuery(name = "UserPublicProfile.count",
			query = "SELECT count(u) FROM UserPublicProfile u")})
@Access(AccessType.FIELD)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UserPublicProfile implements Serializable {

	private static final int MAX_LENGTH = 37;

	@Id
	private String publicKey;
	private String firstName;
	private String lastName;
	private String country;
	private boolean countryEnabled;
	private String city;
	private boolean cityEnabled;
	private int age;
	private boolean ageEnabled;
	private GenderType gender;
	private String hobby;
	private boolean hobbyEnabled;
	private long rating;
	private UserType userType;
	@Lob
	private String personalPageData;

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

	public long getRating() {
		return rating;
	}

	public void setRating(long rating) {
		this.rating = rating;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getHobby() {
		return hobby;
	}

	public void setHobby(String hobby) {
		this.hobby = hobby;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public GenderType getGender() {
		return gender;
	}

	public void setGender(GenderType gender) {
		this.gender = gender;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	public boolean isCountryEnabled() {
		return countryEnabled;
	}

	public void setCountryEnabled(boolean countryEnabled) {
		this.countryEnabled = countryEnabled;
	}

	public boolean isCityEnabled() {
		return cityEnabled;
	}

	public void setCityEnabled(boolean cityEnabled) {
		this.cityEnabled = cityEnabled;
	}

	public boolean isAgeEnabled() {
		return ageEnabled;
	}

	public void setAgeEnabled(boolean ageEnabled) {
		this.ageEnabled = ageEnabled;
	}

	public boolean isHobbyEnabled() {
		return hobbyEnabled;
	}

	public void setHobbyEnabled(boolean hobbyEnabled) {
		this.hobbyEnabled = hobbyEnabled;
	}

	public String getPersonalPageData() {
		return personalPageData;
	}

	public void setPersonalPageData(String personalPageData) {
		this.personalPageData = personalPageData;
	}
	
	public boolean isValid() {
		return (firstName == null || firstName.length() <= MAX_LENGTH) 
				&& (lastName == null || lastName.length() <= MAX_LENGTH)
				&& (country == null || country.length() <= MAX_LENGTH)
				&& (city == null || city.length() <= MAX_LENGTH)
				&& (hobby == null || hobby.length() <= MAX_LENGTH * 2);
	}
	@Override
	public String toString() {
		return "UserPublicProfile{" + "publicKey=" + publicKey + ", firstName=" + firstName + ", lastName=" + lastName + ", country=" + country + ", countryEnabled=" + countryEnabled + ", city=" + city + ", cityEnabled=" + cityEnabled + ", age=" + age + ", ageEnabled=" + ageEnabled + ", gender=" + gender + ", hobby=" + hobby + ", hobbyEnabled=" + hobbyEnabled + ", rating=" + rating + ", userType=" + userType + ", personalPageData=" + personalPageData + '}';
	}
}
