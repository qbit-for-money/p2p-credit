package com.qbit.p2p.credit.user.resource;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import com.qbit.commons.model.Point2;

/**
 *
 * @author Александр
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UserPhotoRequest implements Serializable {
	
	public static final int MAX_STRING_LENGTH = 1000000;
	public static final Point2 MAX_SIZE = new Point2(1000, 1000);
	public static final Point2 MIN_SIZE = new Point2(100, 100);
	
	private Point2 startPoint;
	private Point2 endPoint;
	private String imageString;

	public Point2 getStartPoint() {
		return startPoint;
	}
	
	public Point2 getSize() {
		return endPoint.sub(startPoint);
	}

	public void setStartPoint(Point2 startPoint) {
		this.startPoint = startPoint;
	}

	public Point2 getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(Point2 endPoint) {
		this.endPoint = endPoint;
	}

	public String getImageString() {
		return imageString;
	}

	public void setImageString(String imageString) {
		this.imageString = imageString;
	}
	
	public boolean isValid() {
		if ((imageString == null) || (imageString.isEmpty()) || imageString.length() > MAX_STRING_LENGTH) {
			return false;
		}
		if ((startPoint == null) || (endPoint == null)) {
			return false;
		}
		Point2 size = getSize();
		return (size.isPositive() && size.isInsideSquare(MAX_SIZE) && MIN_SIZE.isInsideSquare(size));
	}

	@Override
	public String toString() {
		return "UserPhotoRequest{" + "startPoint=" + startPoint + ", endPoint=" + endPoint + ", imageString=" + imageString + '}';
	}
    
}
