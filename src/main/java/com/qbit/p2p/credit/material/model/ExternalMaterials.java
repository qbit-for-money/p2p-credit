package com.qbit.p2p.credit.material.model;

import java.io.Serializable;

/**
 * @author Alexander_Sergeev
 */
public class ExternalMaterials implements Serializable {

	private String link;

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@Override
	public String toString() {
		return "ExternalMaterials{" + "link=" + link + '}';
	}
}
