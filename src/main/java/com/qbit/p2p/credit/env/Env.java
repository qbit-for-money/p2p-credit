package com.qbit.p2p.credit.env;

import java.io.IOException;
import java.util.Properties;
import javax.inject.Singleton;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
	public String getUserPhotoPathFolder() {
		return properties.getProperty("user.photo.pathFolder");
	}
	
	@XmlElement
	public String getLastOrdersPathFolder() {
		return properties.getProperty("lastOrders.pathFolder");
	}
	
	public int getUpdateLastOrdersPeriodSecs() {
		return Integer.parseInt(properties.getProperty("lastOrders.worker.period.hours"));
	}
	
	public int getUpdateGlobalStatisticsPeriodSecs() {
		return Integer.parseInt(properties.getProperty("globalStatistics.period.hours"));
	}
	
	@XmlElement
	public double getUserOpenessRatingFactor() {
		return Double.parseDouble(properties.getProperty("user.openessRating.factor"));
	}
	
	@XmlElement
	public double getUserAllTransactionsFactor() {
		return Double.parseDouble(properties.getProperty("user.allTransactions.factor"));
	}
	
	
}
