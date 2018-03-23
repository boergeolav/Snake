package package1;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;

public class Square {
	
	public static final int SNAKE = 1, EMPTY = 2, WALL = 3, FOOD = 4;
	public static final Dimension SIZE = new Dimension(15, 15);
	
	private int x, y;
	
	private int type;
	private Color color;
	
	public Square(int type, int x, int y) {
		this.x = x;
		this.y = y;
		this.type = type;
		
		switch(type) {
		case SNAKE:
			color = Color.BLACK;
			break;
		case EMPTY:
			color = Color.WHITE;
			break;
		case WALL:
			color = Color.GRAY;
			break;
		case FOOD:
			color = Color.GREEN;
			break;
		default:
			color = Color.WHITE;
			break;
		}
	}
	
	public int getType() {
		return this.type;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public Point getCoordinates() {
		return new Point(x, y);
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void setCoordinates(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Color getColor() {
		return this.color;
	}
	
}
