package com.qbit.p2p.credit.commons.model;

/**
 * @author Alexander_Sergeev
 */
public class Point2 implements Comparable<Point2> {

	private final int x;
	private final int y;

	public Point2(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int len() {
		return (int) Math.sqrt(x * x + y * y);
	}

	public Point2 add(Point2 o) {
		return new Point2(x + o.getX(), y + o.getY());
	}
	
	public Point2 subtraction(Point2 o) {
		return new Point2(x - o.getX(), y - o.getY());
	}

	public Point2 mul(int k) {
		return new Point2(k * x, k * y);
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 79 * hash + this.x;
		hash = 79 * hash + this.y;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Point2 other = (Point2) obj;
		if (this.x != other.x) {
			return false;
		}
		if (this.y != other.y) {
			return false;
		}
		return true;
	}
	
	@Override
	public int compareTo(Point2 o) {
		if (this.equals(o)) return 0;
		return ((this.x + this.y) < (o.x + o.y)) ? -1 : 1;
	}

	@Override
	public String toString() {
		return "Point2{" + "x=" + x + ", y=" + y + '}';
	}
}
