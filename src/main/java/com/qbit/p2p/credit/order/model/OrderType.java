package com.qbit.p2p.credit.order.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

/**
 * @author Alexander_Sergeev
 */
@XmlEnum(Integer.class) 
public enum OrderType {
	@XmlEnumValue("1")
	CREDIT, 
	@XmlEnumValue("2")
	BORROW,
	@XmlEnumValue("3")
	EXCHANGE
}