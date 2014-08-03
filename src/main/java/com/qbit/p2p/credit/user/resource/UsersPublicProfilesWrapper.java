package com.qbit.p2p.credit.user.resource;

import com.qbit.p2p.credit.user.model.UserPublicProfile;
import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Александр
 */
@XmlRootElement
public class UsersPublicProfilesWrapper implements Serializable {
	
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
