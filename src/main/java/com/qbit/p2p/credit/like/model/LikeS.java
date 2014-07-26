package com.qbit.p2p.credit.like.model;

import com.qbit.commons.model.Identifiable;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Alex
 */
@Entity
@Access(AccessType.FIELD)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class LikeS implements Identifiable<EntityPartId>, Serializable {

	@EmbeddedId
	private EntityPartId id;
	
	private long likeCount;
	private long dislikeCount;
	
	private Set<String> alreadyVotedUserPublicKeys = new HashSet<String>();

	public LikeS() {
	}

	public LikeS(EntityPartId id) {
		this.id = id;
	}
	
	@Override
	public EntityPartId getId() {
		return id;
	}

	public long getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(long likeCount) {
		this.likeCount = likeCount;
	}

	public long getDislikeCount() {
		return dislikeCount;
	}

	public void setDislikeCount(long dislikeCount) {
		this.dislikeCount = dislikeCount;
	}

	public Set<String> getAlreadyVotedUserPublicKeys() {
		return alreadyVotedUserPublicKeys;
	}

	public void setAlreadyVotedUserPublicKeys(Set<String> alreadyVotedUserPublicKeys) {
		this.alreadyVotedUserPublicKeys = alreadyVotedUserPublicKeys;
	}
	
	@Override
	public String toString() {
		return "Like{" + "id=" + id + ", likeCount=" + likeCount + ", dislikeCount=" + dislikeCount + '}';
	}

}
