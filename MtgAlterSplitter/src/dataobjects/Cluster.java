package dataobjects;

import java.util.ArrayList;

public class Cluster {
	private int clusterBeginningX;
	private int clusterBeginningY;
	private int redValue;
	private int greenValue;
	private int blueValue;
	private int value;
	private ArrayList<Pixel> pixels = new ArrayList<>();

	public Cluster(int clusterBeginningX, int clusterBeginningY) {
		this.clusterBeginningX = clusterBeginningX;
		this.clusterBeginningY = clusterBeginningY;
	}

	public void setRedValue(int redValue) {
		this.redValue = redValue;
	}

	public int getRedValue() {
		return redValue;
	}

	public void setGreenValue(int greenValue) {
		this.greenValue = greenValue;
	}

	public int getGreenValue() {
		return greenValue;
	}

	public void setBlueValue(int blueValue) {
		this.blueValue = blueValue;
	}

	public int getBlueValue() {
		return blueValue;
	}

	public int getClusterBeginningX() {
		return clusterBeginningX;
	}

	public int getClusterBeginningY() {
		return clusterBeginningY;
	}

	public int getValue() {
		return value;
	}

	public void addPixel(Pixel pixel) {
		this.pixels.add(pixel);
	}

	public ArrayList<Pixel> getPixels() {
		return this.pixels;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
