package dataobjects;

public class Pixel {

	private int xPositionInPicture;
	private int yPositionInPicture;
	private int colorValue;

	public Pixel(int xPositionInPicture, int yPositionInPicture, int colorValue) {
		this.setxPositionInPicture(xPositionInPicture);
		this.setyPositionInPicture(yPositionInPicture);
		this.setColorValue(colorValue);
	}

	public int getXPositionInPicture() {
		return xPositionInPicture;
	}

	public void setxPositionInPicture(int xPositionInPicture) {
		this.xPositionInPicture = xPositionInPicture;
	}

	public int getYPositionInPicture() {
		return yPositionInPicture;
	}

	public void setyPositionInPicture(int yPositionInPicture) {
		this.yPositionInPicture = yPositionInPicture;
	}

	public int getColorValue() {
		return colorValue;
	}

	public void setColorValue(int colorValue) {
		this.colorValue = colorValue;
	}
}
