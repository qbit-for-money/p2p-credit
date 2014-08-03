package com.qbit.p2p.credit.user.resource;

import com.qbit.p2p.credit.user.model.Language;
import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Александр
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class LanguagesWrapper implements Serializable {
	
	@XmlElement
    @XmlList
	private List<Language> languages;

	public LanguagesWrapper() {
	}

	public LanguagesWrapper(List<Language> languages) {
		this.languages = languages;
	}

	public List<Language> getLanguages() {
		return languages;
	}
}
