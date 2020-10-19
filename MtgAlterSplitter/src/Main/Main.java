package Main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import CalculationUtils.ClusterUtils;
import DataObjects.Cluster;

public class Main {

	public static void main(String[] args) throws IOException {
		File file = new File("C:\\Users\\jt\\Pictures\\FINAL.png");
		BufferedImage alteredImage = ImageIO.read(file);
		int width = alteredImage.getWidth();
		int height = alteredImage.getHeight();
		int analyzedClusterSizeX = 1;
		int analyzedClusterSizeY = 2;

		for (int tolerance = 333333; tolerance < 999999; tolerance = tolerance + 111111) {
			ArrayList<BufferedImage> processedImages = new ArrayList<>();
			for (int pixelShiftX = 0; pixelShiftX < analyzedClusterSizeX; pixelShiftX++) {
				for (int pixelShiftY = 0; pixelShiftY < analyzedClusterSizeY; pixelShiftY++) {
					Cluster[][] reducedPictureMatrix = ClusterUtils.createClusterMatrix(alteredImage,
							analyzedClusterSizeX, analyzedClusterSizeY, pixelShiftX, pixelShiftY);

					BufferedImage processedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

					Cluster[][] matchingClusterMatrix = ClusterUtils
							.findSimilarHorrizontalAndVerticalNeighboringClusters(2, tolerance, reducedPictureMatrix);

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

			BufferedImage finalProcessedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

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

			for (int pixelShiftX = 0; pixelShiftX < analyzedClusterSizeX; pixelShiftX++) {
				for (int pixelShiftY = 0; pixelShiftY < analyzedClusterSizeY; pixelShiftY++) {
					for (int clusterEnlargmentFactor = 1; clusterEnlargmentFactor < 31; clusterEnlargmentFactor++)
						ClusterUtils.removeSpecs(finalProcessedImage, analyzedClusterSizeX * clusterEnlargmentFactor,
								analyzedClusterSizeY * clusterEnlargmentFactor, pixelShiftX, pixelShiftY);
				}
			}

			for (int gapFillings = 0; gapFillings < 10; gapFillings++) {
				ClusterUtils.fillGaps(alteredImage, finalProcessedImage);
			}

			File extractedFile = new File("C:\\Users\\jt\\Pictures\\FINAL_altered_calculatd_tollerance_" + tolerance
					+ "_specRemovalWith_ASYMMETRIC_CLUSTER_SIZE_ClusterSizeToTen_AND_GAP_FILLING_10" + ".png");
			try {
				ImageIO.write(finalProcessedImage, "png", extractedFile);
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}
}
