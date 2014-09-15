package com.qbit.p2p.credit.message.resource;

import com.qbit.p2p.credit.message.model.Message;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Alex
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MessagesWrapper {
	@XmlElement
	@XmlList
	private List<Message> messages;
	private long length;

	public MessagesWrapper() {
	}

	public MessagesWrapper(List<Message> messages) {
		this.messages = messages;
	}

	public MessagesWrapper(List<Message> messages, long length) {
		this.messages = messages;
		this.length = length;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public long getLength() {
		return length;
	}
}
