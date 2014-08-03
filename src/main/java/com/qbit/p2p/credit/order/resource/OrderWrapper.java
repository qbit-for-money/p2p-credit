package com.qbit.p2p.credit.order.resource;

import com.qbit.p2p.credit.commons.model.Currency;
import com.qbit.p2p.credit.money.model.serialization.CurrencyAdapter;
import com.qbit.p2p.credit.order.model.OrderInfo;
import java.util.List;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author Alexander_Sergeev
 */
public class OrderWrapper {

	private OrderInfo order;
	private String id;
	private long opennessRating;
	private long ordersValue;
	private long successTransactionsCount;
	private long partnersRating;
	private String successValue;
	private String userName;
	private String userPhone;
	private String userMail;
	@XmlJavaTypeAdapter(CurrencyAdapter.class)
	private List<Currency> userCurrencies;
	private List<String> userLanguages;

	public OrderWrapper() {
	}

	public OrderWrapper(OrderInfo order) {
		this.order = order;
	}

	public OrderInfo getOrder() {
		return order;
	}

	public void setOrder(OrderInfo order) {
		this.order = order;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getOpennessRating() {
		return opennessRating;
	}

	public void setOpennessRating(long opennessRating) {
		this.opennessRating = opennessRating;
	}

	public long getOrdersValue() {
		return ordersValue;
	}

	public void setOrdersValue(long ordersValue) {
		this.ordersValue = ordersValue;
	}

	public long getSuccessTransactionsCount() {
		return successTransactionsCount;
	}

	public void setSuccessTransactionsCount(long successTransactionsCount) {
		this.successTransactionsCount = successTransactionsCount;
	}

	public long getPartnersRating() {
		return partnersRating;
	}

	public void setPartnersRating(long partnersRating) {
		this.partnersRating = partnersRating;
	}

	public String getSuccessValue() {
		return successValue;
	}

	public void setSuccessValue(String successValue) {
		this.successValue = successValue;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	public List<Currency> getUserCurrencies() {
		return userCurrencies;
	}

	public void setUserCurrencies(List<Currency> userCurrencies) {
		this.userCurrencies = userCurrencies;
	}

	public List<String> getUserLanguages() {
		return userLanguages;
	}

	public void setUserLanguages(List<String> userLanguages) {
		this.userLanguages = userLanguages;
	}

	public String getUserMail() {
		return userMail;
	}

	public void setUserMail(String userMail) {
		this.userMail = userMail;
	}

	@Override
	public String toString() {
		return "OrderWrapper{" + "order=" + order + ", id=" + id + ", opennessRating=" + opennessRating + ", ordersValue=" + ordersValue + ", successTransactionsCount=" + successTransactionsCount + ", partnersRating=" + partnersRating + ", successValue=" + successValue + ", userName=" + userName + ", userPhone=" + userPhone + ", userMail=" + userMail + ", userCurrencies=" + userCurrencies + ", userLanguages=" + userLanguages + '}';
	}
}
