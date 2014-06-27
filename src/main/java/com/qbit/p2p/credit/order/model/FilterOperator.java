package com.qbit.p2p.credit.order.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

/**
 * @author Alexander_Sergeev
 */
@XmlEnum(Integer.class) 
public enum FilterOperator {
	@XmlEnumValue("1")
	AND,
	@XmlEnumValue("2")
	OR
}
