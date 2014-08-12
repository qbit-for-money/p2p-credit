package com.qbit.p2p.credit.user.model;

import com.qbit.p2p.credit.commons.model.Currency;
import com.qbit.p2p.credit.commons.model.CurrencyAdapter;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author Alexander_Sergeev
 */
@Entity
@Access(AccessType.FIELD)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UserPublicProfile implements Serializable {

	@Id
	private String userId;
	
	private String name;
	private String mail;
	private boolean mailEnabled;
	private String phone;
	private boolean phoneEnabled;
	@ManyToMany(cascade = CascadeType.ALL)
	private List<Language> languages;
	private boolean languagesEnabled;
	@XmlJavaTypeAdapter(CurrencyAdapter.class)
	private List<Currency> currencies;
	private boolean currenciesEnabled;
	@Lob
	private String personalData;
	private boolean personalDataEnabled;

	private List<DataLink> socialLinks;
	private boolean passportEnabled;
	private List<DataLink> videos;

	public UserPublicProfile() {
	}

	public UserPublicProfile(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
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

	public List<Language> getLanguages() {
		return languages;
	}

	public void setLanguages(List<Language> languages) {
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

	public boolean isPassportEnabled() {
		return passportEnabled;
	}

	public void setPassportEnabled(boolean passportEnabled) {
		this.passportEnabled = passportEnabled;
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

	@Override
	public String toString() {
		return "UserPublicProfile{" + "userId=" + userId + ", name=" + name + ", mail=" + mail + ", mailEnabled=" + mailEnabled + ", phone=" + phone + ", phoneEnabled=" + phoneEnabled + ", languages=" + languages + ", languagesEnabled=" + languagesEnabled + ", currencies=" + currencies + ", currenciesEnabled=" + currenciesEnabled + ", personalData=" + personalData + ", personalDataEnabled=" + personalDataEnabled + ", socialLinks=" + socialLinks + ", passportEnabled=" + passportEnabled + ", videos=" + videos + '}';
	}
}
