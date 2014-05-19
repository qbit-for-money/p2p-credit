package com.qbit.p2p.credit.user.model;

import com.qbit.commons.user.UserInfo;
import java.io.Serializable;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Alexander_Sergeev
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "UserPublicProfile.findByType",
			query = "SELECT u FROM UserPublicProfile u WHERE u.type = :type"),
	@NamedQuery(name = "UserPublicProfile.findByOrdersNoMoreThan",
			query = "SELECT u FROM UserPublicProfile u WHERE "
			+ "(SELECT count(o) FROM OrderInfo o WHERE o.userPublicKey = u.publicKey) <= :number"),
	@NamedQuery(name = "UserPublicProfile.findByOrdersNoLessThan",
			query = "SELECT u FROM UserPublicProfile u WHERE "
			+ "(SELECT count(o) FROM OrderInfo o WHERE o.userPublicKey = u.publicKey) >= :number"),
	@NamedQuery(name = "UserPublicProfile.findByRatingNoMoreThan",
			query = "SELECT u FROM UserPublicProfile u WHERE u.rating <= :rating"),
	@NamedQuery(name = "UserPublicProfile.findByRatingNoLessThan",
			query = "SELECT u FROM UserPublicProfile u WHERE u.rating >= :rating")})
@Access(AccessType.FIELD)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UserPublicProfile implements Serializable {

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@PrimaryKeyJoinColumn
	private UserInfo user;

	@Id
	private String publicKey;

	private String firstName;
	private String lastName;
	private String login;
	private long rating;
	private UserType type;

	public UserInfo getUser() {
		return user;
	}

	public void setUser(UserInfo user) {
		this.user = user;
	}

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

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public long getRating() {
		return rating;
	}

	public void setRating(long rating) {
		this.rating = rating;
	}

	public UserType getType() {
		return type;
	}

	public void setType(UserType type) {
		this.type = type;
	}
}
