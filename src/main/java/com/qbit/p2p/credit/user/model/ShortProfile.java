package com.qbit.p2p.credit.user.model;

import com.qbit.p2p.credit.commons.model.Currency;
import com.qbit.p2p.credit.money.model.serialization.CurrencyAdapter;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author Alexander_Sergeev
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ShortProfile {
	private String publicKey;
	private String name;
	private String mail;
	private String phone;
	private List<Language> languages;
	@XmlJavaTypeAdapter(CurrencyAdapter.class)
	private List<Currency> currencies;

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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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
}
