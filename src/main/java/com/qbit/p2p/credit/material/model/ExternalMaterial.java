package com.qbit.p2p.credit.material.model;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * @author Alexander_Sergeev
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ExternalMaterial implements Serializable {

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
