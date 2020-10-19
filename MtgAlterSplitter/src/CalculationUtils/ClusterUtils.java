package CalculationUtils;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import DataObjects.Cluster;
import DataObjects.Pixel;

public class ClusterUtils {

	public static void generatePreliminaryImageCollection(BufferedImage alteredImage, int width, int height,
			int analyzedClusterSizeX, int analyzedClusterSizeY, int tolerance,
			ArrayList<BufferedImage> processedImages) {
		for (int pixelShiftX = 0; pixelShiftX < analyzedClusterSizeX; pixelShiftX++) {
			for (int pixelShiftY = 0; pixelShiftY < analyzedClusterSizeY; pixelShiftY++) {
				Cluster[][] reducedPictureMatrix = ClusterUtils.createClusterMatrix(alteredImage, analyzedClusterSizeX,
						analyzedClusterSizeY, pixelShiftX, pixelShiftY);

				BufferedImage processedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

				Cluster[][] matchingClusterMatrix = ClusterUtils.findSimilarHorrizontalAndVerticalNeighboringClusters(2,
						tolerance, reducedPictureMatrix);

				for (int i = 0; i < matchingClusterMatrix.length; i++) {
					for (int j = 0; j < matchingClusterMatrix[0].length; j++) {
						if (matchingClusterMatrix[i][j] == null) {
							continue;
						}
						ClusterUtils.copyMatchingPixelsToNewImage(processedImage, alteredImage,
								matchingClusterMatrix[i][j]);
					}
				}
				processedImages.add(processedImage);
			}
		}
	}

	public static Cluster[][] createClusterMatrix(BufferedImage alteredImage, int analyzedClusterSizeX,
			int analyzedClusterSizeY, int pixelShiftX, int pixelShiftY) {

		int clusterMatrixWidth = ((alteredImage.getWidth()) / analyzedClusterSizeX);
		int clusterMatricHeight = ((alteredImage.getHeight()) / analyzedClusterSizeY);
		Cluster[][] clusterMatrix = new Cluster[clusterMatrixWidth][clusterMatricHeight];

		for (int x = 0; x < clusterMatrixWidth; x++) {
			for (int y = 0; y < clusterMatricHeight; y++) {

				Cluster cluster = calculateClusterValues(alteredImage, x, y, pixelShiftX, pixelShiftY,
						analyzedClusterSizeX, analyzedClusterSizeY);

				clusterMatrix[x][y] = cluster;

			}
		}

		return clusterMatrix;
	}

	private static Cluster calculateClusterValues(BufferedImage image, int currentX, int currentY, int pixelShiftX,
			int pixelShiftY, int analyzedClusterSizeX, int analyzedClusterSizeY) {

		int clusterBeginningX = currentX * analyzedClusterSizeX + pixelShiftX;
		int clusterBeginningY = currentY * analyzedClusterSizeY + pixelShiftY;

		Cluster calculatedCluster = new Cluster(clusterBeginningX, clusterBeginningY);

		int greenValue = 0;
		int blueValue = 0;
		int redValue = 0;

		for (int x = clusterBeginningX; x < clusterBeginningX + analyzedClusterSizeX; x++) {
			for (int y = clusterBeginningY; y < clusterBeginningY + analyzedClusterSizeY; y++) {
				if ((x < image.getWidth()) && (y < image.getHeight())) {

					int currentPixelColorValue = image.getRGB(x, y);
					Pixel currentPixel = new Pixel(x, y, currentPixelColorValue);
					calculatedCluster.addPixel(currentPixel);

					// TODO CHECK IF THAT CAN BE DONE BETTER WITH DIFFERENT BIT OPPERATOR
					int redMaskForAlteredImage = ((currentPixelColorValue >> 24) << 8);
					int greenMaskForAlteredImage = ((currentPixelColorValue >> 16) << 8);
					int blueMaskForAlteredImage = ((currentPixelColorValue >> 8) << 8);

					redValue = redValue + ((currentPixelColorValue >> 16) ^ redMaskForAlteredImage);
					greenValue = greenValue + ((currentPixelColorValue >> 8) ^ greenMaskForAlteredImage);
					blueValue = blueValue + (currentPixelColorValue ^ blueMaskForAlteredImage);
				}
			}
		}

		redValue = (int) (redValue / (analyzedClusterSizeX * analyzedClusterSizeY));
		calculatedCluster.setRedValue(redValue);

		greenValue = (int) (greenValue / (analyzedClusterSizeX * analyzedClusterSizeY));
		calculatedCluster.setGreenValue(greenValue);

		blueValue = (int) (blueValue / (analyzedClusterSizeX * analyzedClusterSizeY));
		calculatedCluster.setBlueValue(blueValue);

		calculatedCluster.setValue(redValue << 16 | greenValue << 8 | blueValue);

		return calculatedCluster;
	}

	public static Cluster[][] findSimilarHorrizontalAndVerticalNeighboringClusters(int analyzedClusterSize,
			int deviationTolerance, Cluster[][] clusterMatrix) {

		Cluster[][] matchingPixelsClusterMatrix = new Cluster[clusterMatrix.length][clusterMatrix[0].length];

		for (int x = 0; x < clusterMatrix.length; x++) {
			for (int y = 0; y < clusterMatrix[0].length; y++) {

				boolean isUpperNaighborSimilar = false;
				boolean isRightNaighborSimilar = false;
				boolean isLeftNaighborSimilar = false;
				boolean isLowerNaighborSimilar = false;

				Cluster upperMiddleCluster = null;
				Cluster middleLeftCluster = null;
				Cluster middleMiddleCluster = null;
				Cluster middleRightCluster = null;
				Cluster lowerMiddleCluster = null;

				// Upper middle cluster
				int upperMiddleX = x;
				int upperMiddleY = y - 1;
				if (upperMiddleY >= 0) {
					upperMiddleCluster = clusterMatrix[upperMiddleX][upperMiddleY];
				}

				// Middle left cluster
				int middleLefttX = x - 1;
				int middleLeftY = y;
				if ((middleLefttX >= 0)) { // && (middleLefttX < clusterMatrix.length)
					middleLeftCluster = clusterMatrix[middleLefttX][middleLeftY];
				}

				// Middle middle cluster
				int middleMiddleX = x;
				int middleMiddleY = y;
				middleMiddleCluster = clusterMatrix[middleMiddleX][middleMiddleY];

				// Middle right cluster
				int middleRightX = x + 1;
				int middleRightY = y;
				if (middleRightX < clusterMatrix.length) {
					middleRightCluster = clusterMatrix[middleRightX][middleRightY];
				}

				// Lower middle cluster
				int lowerMiddleX = x;
				int lowerMiddleY = y + 1;
				if (lowerMiddleY < clusterMatrix[0].length) {
					lowerMiddleCluster = clusterMatrix[lowerMiddleX][lowerMiddleY];
				}

				if (middleMiddleCluster == null) {
					continue;
				}

				// Find matching clusters
				if (upperMiddleCluster != null) {
					isUpperNaighborSimilar = (Math
							.abs(upperMiddleCluster.getValue() - middleMiddleCluster.getValue()) < deviationTolerance);
				}
				if (middleRightCluster != null) {
					isRightNaighborSimilar = (Math
							.abs(middleRightCluster.getValue() - middleMiddleCluster.getValue()) < deviationTolerance);

				}
				if (middleLeftCluster != null) {
					isLeftNaighborSimilar = (Math
							.abs(middleLeftCluster.getValue() - middleMiddleCluster.getValue()) < deviationTolerance);

				}
				if (lowerMiddleCluster != null) {
					isLowerNaighborSimilar = (Math
							.abs(lowerMiddleCluster.getValue() - middleMiddleCluster.getValue()) < deviationTolerance);
				}

				if ((isUpperNaighborSimilar ? 1 : 0) + (isRightNaighborSimilar ? 1 : 0)
						+ (isLeftNaighborSimilar ? 1 : 0) + (isLowerNaighborSimilar ? 1 : 0) >= 3) {
					matchingPixelsClusterMatrix[x][y] = middleMiddleCluster;
				}
			}
		}
		return matchingPixelsClusterMatrix;
	}

	static Cluster[][] findSimilarDiagonalNeighboringClusters(Cluster[][] matchingClusterMatrix,
			int analyzedClusterSize, int deviationTolerance, Cluster[][] clusterMatrix) {

		for (int x = 0; x < clusterMatrix.length; x++) {
			for (int y = 0; y < clusterMatrix[0].length; y++) {

				boolean isUpperLeftNaighborSimilar = false;
				boolean isUpperRightNaighborSimilar = false;
				boolean isLowerLeftNaighborSimilar = false;
				boolean isLowerRightNaighborSimilar = false;

				Cluster upperLeftCluster = null;
				Cluster upperRightCluster = null;
				Cluster middleMiddleCluster = null;
				Cluster lowerLeftCluster = null;
				Cluster lowerRighCluster = null;

				// Upper left cluster
				int upperLeftX = x - 1;
				int upperLeytY = y - 1;
				if ((upperLeytY >= 0) && (upperLeftX >= 0)) {
					upperLeftCluster = clusterMatrix[upperLeftX][upperLeytY];
				}

				// Upper right cluster
				int upperRightX = x + 1;
				int upperRightY = y - 1;
				if ((upperRightY >= 0) && (upperRightX < clusterMatrix.length)) {
					upperRightCluster = clusterMatrix[upperRightX][upperRightY];
				}

				// Middle middle cluster
				int middleMiddleX = x;
				int middleMiddleY = y;
				middleMiddleCluster = clusterMatrix[middleMiddleX][middleMiddleY];

				// Lower left cluster
				int lowerLeftX = x - 1;
				int lowerLeytY = y + 1;
				if ((lowerLeytY < clusterMatrix[0].length) && (lowerLeftX >= 0)) {
					lowerLeftCluster = clusterMatrix[lowerLeftX][lowerLeytY];
				}

				// Lower right cluster
				int lowerRightX = x + 1;
				int lowerRightY = y + 1;
				if ((lowerRightY < clusterMatrix[0].length) && (lowerRightX < clusterMatrix.length)) {
					lowerRighCluster = clusterMatrix[lowerRightX][lowerRightY];
				}

				if (middleMiddleCluster == null) {
					continue;
				}

				// Find matching clusters
				if (upperLeftCluster != null) {
					isUpperLeftNaighborSimilar = (Math
							.abs(upperLeftCluster.getValue() - middleMiddleCluster.getValue()) < deviationTolerance);
				}
				if (upperRightCluster != null) {
					isUpperRightNaighborSimilar = (Math
							.abs(upperRightCluster.getValue() - middleMiddleCluster.getValue()) < deviationTolerance);

				}
				if (lowerLeftCluster != null) {
					isLowerLeftNaighborSimilar = (Math
							.abs(lowerLeftCluster.getValue() - middleMiddleCluster.getValue()) < deviationTolerance);

				}
				if (lowerRighCluster != null) {
					isLowerRightNaighborSimilar = (Math
							.abs(lowerRighCluster.getValue() - middleMiddleCluster.getValue()) < deviationTolerance);
				}

				if ((isUpperLeftNaighborSimilar ? 1 : 0) + (isUpperRightNaighborSimilar ? 1 : 0)
						+ (isLowerLeftNaighborSimilar ? 1 : 0) + (isLowerRightNaighborSimilar ? 1 : 0) >= 3) {
					matchingClusterMatrix[x][y] = middleMiddleCluster;
				}
			}
		}
		return matchingClusterMatrix;
	}

	public static void copyMatchingPixelsToNewImage(BufferedImage extractedImage, BufferedImage alteredImage,
			Cluster cluster) {

		for (Pixel pixel : cluster.getPixels()) {
			extractedImage.setRGB(pixel.getXPositionInPicture(), pixel.getYPositionInPicture(), pixel.getColorValue());
		}
	}

	public static void vectorilPixelCompare(int width, int height, ArrayList<BufferedImage> processedImages,
			BufferedImage finalProcessedImage) {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int[] pixelsAtPosition = new int[processedImages.size() - 1];
				for (int imagePositionCounter = 0; imagePositionCounter < processedImages.size()
						- 1; imagePositionCounter++) {

					pixelsAtPosition[imagePositionCounter] = processedImages.get(imagePositionCounter).getRGB(x, y);
				}

				boolean allPixelsAreSimilar = true;
				unsimmilarPixel: for (int similarrityOne = 0; similarrityOne < pixelsAtPosition.length; similarrityOne++) {
					for (int similarrityTwo = 0; similarrityTwo < pixelsAtPosition.length; similarrityTwo++) {
						if (pixelsAtPosition[similarrityOne] != pixelsAtPosition[similarrityTwo]) {
							allPixelsAreSimilar = false;
							break unsimmilarPixel;
						}
					}
				}
				if (allPixelsAreSimilar) {
					finalProcessedImage.setRGB(x, y, pixelsAtPosition[0]);
				} else {
					finalProcessedImage.setRGB(x, y, 0);
				}
			}
		}
	}

	public static void removeSpecs(BufferedImage image, int analyzedClusterSizeX, int analyzedClusterSizeY,
			int clusterEnlargmentFactor) {
		for (int pixelShiftX = 0; pixelShiftX < analyzedClusterSizeX * clusterEnlargmentFactor; pixelShiftX++) {
			for (int pixelShiftY = 0; pixelShiftY < analyzedClusterSizeY * clusterEnlargmentFactor; pixelShiftY++) {
				for (; clusterEnlargmentFactor < 41; clusterEnlargmentFactor++) {
					Cluster[][] clusterMatrix = createClusterMatrix(image, analyzedClusterSizeX, analyzedClusterSizeY,
							pixelShiftX, pixelShiftY);

					for (int x = 0; x < clusterMatrix.length - 1; x++) {
						for (int y = 0; y < clusterMatrix[0].length - 1; y++) {

							Cluster upperLeftCluster = null;
							Cluster upperMiddleCluster = null;
							Cluster upperRightCluster = null;
							Cluster middleLeftCluster = null;
							Cluster middleMiddleCluster = null;
							Cluster middleRightCluster = null;
							Cluster lowerLeftCluster = null;
							Cluster lowerMiddleCluster = null;
							Cluster lowerRighCluster = null;

							// Upper left cluster
							int upperLeftX = x - 1;
							int upperLeytY = y - 1;
							if ((upperLeytY >= 0) && (upperLeftX >= 0)) {
								upperLeftCluster = clusterMatrix[upperLeftX][upperLeytY];
							}

							// Upper middle cluster
							int upperMiddleX = x;
							int upperMiddleY = y - 1;
							if (upperMiddleY >= 0) {
								upperMiddleCluster = clusterMatrix[upperMiddleX][upperMiddleY];
							}

							// Upper right cluster
							int upperRightX = x + 1;
							int upperRightY = y - 1;
							if ((upperRightY >= 0) && (upperRightX < clusterMatrix.length)) {
								upperRightCluster = clusterMatrix[upperRightX][upperRightY];
							}

							// Middle left cluster
							int middleLefttX = x - 1;
							int middleLeftY = y;
							if ((middleLefttX >= 0)) { // && (middleLefttX < clusterMatrix.length)
								middleLeftCluster = clusterMatrix[middleLefttX][middleLeftY];
							}

							// Middle middle cluster
							int middleMiddleX = x;
							int middleMiddleY = y;
							middleMiddleCluster = clusterMatrix[middleMiddleX][middleMiddleY];

							// Middle right cluster
							int middleRightX = x + 1;
							int middleRightY = y;
							if (middleRightX < clusterMatrix.length) {
								middleRightCluster = clusterMatrix[middleRightX][middleRightY];
							}

							// Lower left cluster
							int lowerLeftX = x - 1;
							int lowerLeytY = y + 1;
							if ((lowerLeytY < clusterMatrix[0].length) && (lowerLeftX >= 0)) {
								lowerLeftCluster = clusterMatrix[lowerLeftX][lowerLeytY];
							}

							// Lower middle cluster
							int lowerMiddleX = x;
							int lowerMiddleY = y + 1;
							if (lowerMiddleY < clusterMatrix[0].length) {
								lowerMiddleCluster = clusterMatrix[lowerMiddleX][lowerMiddleY];
							}

							// Lower right cluster
							int lowerRightX = x + 1;
							int lowerRightY = y + 1;
							if ((lowerRightY < clusterMatrix[0].length) && (lowerRightX < clusterMatrix.length)) {
								lowerRighCluster = clusterMatrix[lowerRightX][lowerRightY];
							}

							if ((middleMiddleCluster == null) || (upperLeftCluster == null)
									|| (upperMiddleCluster == null) || (upperRightCluster == null)
									|| (middleLeftCluster == null) || (middleMiddleCluster == null)
									|| (middleRightCluster == null) || (lowerLeftCluster == null)
									|| (lowerMiddleCluster == null) || (lowerRighCluster == null)) {
								continue;
							}

							if (middleMiddleCluster.getValue() == 0) {
								continue;
							}

							if ((((upperLeftCluster.getValue() == 0) ? 1 : 0)
									+ ((upperMiddleCluster.getValue() == 0) ? 1 : 0)
									+ ((upperRightCluster.getValue() == 0) ? 1 : 0)
									+ ((middleLeftCluster.getValue() == 0) ? 1 : 0)
									+ ((middleRightCluster.getValue() == 0) ? 1 : 0)
									+ ((lowerLeftCluster.getValue() == 0) ? 1 : 0)
									+ ((lowerMiddleCluster.getValue() == 0) ? 1 : 0)
									+ ((lowerRighCluster.getValue() == 0) ? 1 : 0)) > 5) {
								for (Pixel pixel : middleMiddleCluster.getPixels()) {
									image.setRGB(pixel.getXPositionInPicture(), pixel.getYPositionInPicture(), 0);
								}
							}
						}
					}
				}
			}
		}
	}

	public static void fillGaps(BufferedImage oldImage, BufferedImage newImage, int gapFillingCycles) {
		for (; gapFillingCycles < 69; gapFillingCycles++) {
			for (int x = 0; x < newImage.getWidth() - 1; x++) {
				for (int y = 0; y < newImage.getHeight() - 1; y++) {

					int upperLeftPixel = -16777216;
					int upperMiddlePixel = -16777216;
					int upperRightPixel = -16777216;
					int middleLeftPixel = -16777216;
					int middleMiddlePixel = -16777216;
					int middleRightPixel = -16777216;
					int lowerLeftPixel = -16777216;
					int lowerMiddlePixel = -16777216;
					int lowerRighPixel = -16777216;

					// Middle middle cluster
					int middleMiddleX = x;
					int middleMiddleY = y;
					middleMiddlePixel = newImage.getRGB(middleMiddleX, middleMiddleY);

					if (middleMiddlePixel != -16777216) {
						continue;
					}

					// Upper left cluster
					int upperLeftX = x - 1;
					int upperLeytY = y - 1;
					if ((upperLeytY >= 0) && (upperLeftX >= 0)) {
						upperLeftPixel = newImage.getRGB(upperLeftX, upperLeytY);
					}

					// Upper middle cluster
					int upperMiddleX = x;
					int upperMiddleY = y - 1;
					if (upperMiddleY >= 0) {
						upperMiddlePixel = newImage.getRGB(upperMiddleX, upperMiddleY);
					}

					// Upper right cluster
					int upperRightX = x + 1;
					int upperRightY = y - 1;
					if ((upperRightY >= 0) && (upperRightX < newImage.getWidth())) {
						upperRightPixel = newImage.getRGB(upperRightX, upperRightY);
					}

					// Middle left cluster
					int middleLefttX = x - 1;
					int middleLeftY = y;
					if ((middleLefttX >= 0)) { // && (middleLefttX < clusterMatrix.length)
						middleLeftPixel = newImage.getRGB(middleLefttX, middleLeftY);
					}

					// Middle right cluster
					int middleRightX = x + 1;
					int middleRightY = y;
					if (middleRightX < newImage.getWidth()) {
						middleRightPixel = newImage.getRGB(middleRightX, middleRightY);
					}

					// Lower left cluster
					int lowerLeftX = x - 1;
					int lowerLeytY = y + 1;
					if ((lowerLeytY < newImage.getHeight()) && (lowerLeftX >= 0)) {
						lowerLeftPixel = newImage.getRGB(lowerLeftX, lowerLeytY);
					}

					// Lower middle cluster
					int lowerMiddleX = x;
					int lowerMiddleY = y + 1;
					if (lowerMiddleY < newImage.getHeight()) {
						lowerMiddlePixel = newImage.getRGB(lowerMiddleX, lowerMiddleY);
					}

					// Lower right cluster
					int lowerRightX = x + 1;
					int lowerRightY = y + 1;
					if ((lowerRightY < newImage.getHeight()) && (lowerRightX < newImage.getWidth())) {
						lowerRighPixel = newImage.getRGB(lowerRightX, lowerRightY);
					}

					if ((((upperLeftPixel != -16777216) ? 1 : 0) + ((upperMiddlePixel != -16777216) ? 1 : 0)
							+ ((upperRightPixel != -16777216) ? 1 : 0) + ((middleLeftPixel != -16777216) ? 1 : 0)
							+ ((middleRightPixel != -16777216) ? 1 : 0) + ((lowerLeftPixel != -16777216) ? 1 : 0)
							+ ((lowerMiddlePixel != -16777216) ? 1 : 0)
							+ ((lowerRighPixel != -16777216) ? 1 : 0)) > 4) {

						newImage.setRGB(x, y, oldImage.getRGB(x, y));

					}
				}
			}
		}
	}
}
