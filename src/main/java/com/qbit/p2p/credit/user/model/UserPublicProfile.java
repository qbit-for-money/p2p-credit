package com.qbit.p2p.credit.user.model;

import com.qbit.p2p.credit.commons.model.Currency;
import com.qbit.p2p.credit.commons.model.MapAdapter;
import com.qbit.p2p.credit.money.model.serialization.CurrencyAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author Alexander_Sergeev
 */
@Entity
/*@NamedQueries({
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
 query = "SELECT count(u) FROM UserPublicProfile u")})*/
@NamedQueries({
	@NamedQuery(name = "UserPublicProfile.findByOrder",
		query = "SELECT u FROM UserPublicProfile u, OrderInfo o WHERE "
		+ " o.userPublicKey = u.publicKey AND o.id = :orderId")})
@Access(AccessType.FIELD)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UserPublicProfile implements Serializable {

	private static final int MAX_LENGTH = 37;

	@Id
	private String publicKey;
	private String name;
	private String mail;
	private boolean mailEnabled;
	private String phone;
	private boolean phoneEnabled;
	private List<String> languages;
	private boolean languagesEnabled;
	@XmlJavaTypeAdapter(CurrencyAdapter.class)
	private List<Currency> currencies;
	private boolean currenciesEnabled;
	@Lob
	private String personalData;
	private boolean personalDataEnabled;
	@Embedded
	private Statistic statistic;

	private List<DataLink> phones;
	/*@ElementCollection
	 @MapKeyColumn(name = "name")
	 @Column(name = "phones_value")
	 @CollectionTable(name = "phones_map", joinColumns = @JoinColumn(name = "id"))
	 private Map<String, String> phonesMap = new HashMap<>();*/

	private List<DataLink> socialLinks;
	/*@XmlAnyElement
	 @XmlJavaTypeAdapter(MapAdapter.class)
	 @ElementCollection
	 @MapKeyColumn(name = "name")
	 @Column(name = "links_value")
	 @CollectionTable(name = "social_links_map", joinColumns = @JoinColumn(name = "id"))
	 private Map<String, String> socialLinksMap = new HashMap<>();*/
	private boolean passportEnabled;
	private List<DataLink> videos;
	private List<DataLink> namesLinks;
	@Lob
	private String bkiData;

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public boolean isMailEnabled() {
		return mailEnabled;
	}

	public void setMailEnabled(boolean mailEnabled) {
		this.mailEnabled = mailEnabled;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public boolean isPhoneEnabled() {
		return phoneEnabled;
	}

	public void setPhoneEnabled(boolean phoneEnabled) {
		this.phoneEnabled = phoneEnabled;
	}

	public List<String> getLanguages() {
		return languages;
	}

	public void setLanguages(List<String> languages) {
		this.languages = languages;
	}

	public List<Currency> getCurrencies() {
		return currencies;
	}

	public void setCurrencies(List<Currency> currencies) {
		this.currencies = currencies;
	}

	public String getPersonalData() {
		return personalData;
	}

	public void setPersonalData(String personalData) {
		this.personalData = personalData;
	}

	public boolean isPersonalDataEnabled() {
		return personalDataEnabled;
	}

	public void setPersonalDataEnabled(boolean personalDataEnabled) {
		this.personalDataEnabled = personalDataEnabled;
	}

	public boolean isLanguagesEnabled() {
		return languagesEnabled;
	}

	public void setLanguagesEnabled(boolean languagesEnabled) {
		this.languagesEnabled = languagesEnabled;
	}

	public boolean isCurrenciesEnabled() {
		return currenciesEnabled;
	}

	public void setCurrenciesEnabled(boolean currenciesEnabled) {
		this.currenciesEnabled = currenciesEnabled;
	}

	public Statistic getStatistic() {
		return statistic;
	}

	public void setStatistic(Statistic statistic) {
		this.statistic = statistic;
	}

	public boolean isPassportEnabled() {
		return passportEnabled;
	}

	public void setPassportEnabled(boolean passportEnabled) {
		this.passportEnabled = passportEnabled;
	}

	public List<DataLink> getPhones() {
		return phones;
	}

	public void setPhones(List<DataLink> phones) {
		this.phones = phones;
	}

	public List<DataLink> getSocialLinks() {
		return socialLinks;
	}

	public void setSocialLinks(List<DataLink> socialLinks) {
		this.socialLinks = socialLinks;
	}

	public List<DataLink> getVideos() {
		return videos;
	}

	public void setVideos(List<DataLink> videos) {
		this.videos = videos;
	}

	public List<DataLink> getNamesLinks() {
		return namesLinks;
	}

	public void setNamesLinks(List<DataLink> namesLinks) {
		this.namesLinks = namesLinks;
	}


	public String getBkiData() {
		return bkiData;
	}

	public void setBkiData(String bkiData) {
		this.bkiData = bkiData;
	}

	public boolean isValid() {
		return true;
	}

	/*
	 public boolean isValid() {
	 return (firstName == null || firstName.length() <= MAX_LENGTH)
	 && (lastName == null || lastName.length() <= MAX_LENGTH)
	 && (country == null || country.length() <= MAX_LENGTH)
	 && (city == null || city.length() <= MAX_LENGTH)
	 && (hobby == null || hobby.length() <= MAX_LENGTH * 2);
	 }*/
	@Override
	public String toString() {
		return "UserPublicProfile{" + "publicKey=" + publicKey + ", name=" + name + ", mail=" + mail + ", mailEnabled=" + mailEnabled + ", phone=" + phone + ", phoneEnabled=" + phoneEnabled + ", languages=" + languages + ", languagesEnabled=" + languagesEnabled + ", currencies=" + currencies + ", currenciesEnabled=" + currenciesEnabled + ", personalData=" + personalData + ", personalDataEnabled=" + personalDataEnabled + ", statistic=" + statistic + ", phones=" + phones + ", socialLinks=" + socialLinks + ", passportEnabled=" + passportEnabled + ", videos=" + videos + ", namesLinks=" + namesLinks + ", bkiData=" + bkiData + '}';
	}
}
