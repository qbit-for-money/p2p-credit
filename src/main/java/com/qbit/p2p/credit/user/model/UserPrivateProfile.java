package com.qbit.p2p.credit.user.model;

import java.io.Serializable;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;
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
	@NamedQuery(name = "UserPrivateProfile.findByUser",
			query = "SELECT u FROM UserPrivateProfile u WHERE u.publicKey = :userPublicKey")})
@Access(AccessType.FIELD)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UserPrivateProfile implements Serializable {

	@Id
	private String publicKey;
	private String phone;
	private boolean phoneEnabled;
	private boolean phoneVisible;

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
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

	public boolean isPhoneVisible() {
		return phoneVisible;
	}

	public void setPhoneVisible(boolean phoneVisible) {
		this.phoneVisible = phoneVisible;
	}
}
