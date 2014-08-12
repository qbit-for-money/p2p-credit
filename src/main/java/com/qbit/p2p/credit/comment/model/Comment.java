package com.qbit.p2p.credit.comment.model;

import javax.persistence.EmbeddedId;

/**
 * @author Alexander_Sergeev
 */
public class Comment {
	@EmbeddedId
	private EntityPartId id;
	private String text;

	public Comment() {
	}

	public Comment(EntityPartId id, String text) {
		this.id = id;
		this.text = text;
	}

	public EntityPartId getId() {
		return id;
	}

	public String getText() {
		return text;
	}
}
