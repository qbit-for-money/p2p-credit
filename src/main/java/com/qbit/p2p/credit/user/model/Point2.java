package com.qbit.p2p.credit.user.model;

/**
 * @author Alexander_Sergeev
 */
public class Point2 {
	private int x;
	private int y;

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return "Point2{" + "x=" + x + ", y=" + y + '}';
	}
}
