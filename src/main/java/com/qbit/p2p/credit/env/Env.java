package com.qbit.p2p.credit.env;

import java.io.IOException;
import java.util.Properties;
import javax.inject.Singleton;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * P2P properties
 *
 * @author Alexander_Alexandrov
 */
@Singleton
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Env {

	private final Properties properties;

	public Env() {
		properties = new Properties();
		try {
			properties.load(Env.class.getResourceAsStream("/com/qbit/p2p/credit/p2p.properties"));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	@XmlElement
	public boolean isDemoEnabled() {
		return Boolean.TRUE.toString().equalsIgnoreCase(properties.getProperty("demo.enabled"));
	}

	@XmlElement
	public String getMailBotAddress() {
		return properties.getProperty("mail.bot.address");
	}
	
	@XmlTransient
	public String getMailBotPersonal() {
		return properties.getProperty("mail.bot.personal");
	}

	@XmlTransient
	public String getMailHost() {
		return properties.getProperty("mail.host");
	}
}
