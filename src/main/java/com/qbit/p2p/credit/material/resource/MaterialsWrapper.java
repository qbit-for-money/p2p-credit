package com.qbit.p2p.credit.material.resource;

import com.qbit.p2p.credit.material.model.Material;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Alexander_Sergeev
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MaterialsWrapper {
	@XmlElement
	@XmlList
	private List<Material> materials;

	public MaterialsWrapper() {
	}

	public MaterialsWrapper(List<Material> materials) {
		this.materials = materials;
	}

	public List<Material> getMaterials() {
		return materials;
	}
}
