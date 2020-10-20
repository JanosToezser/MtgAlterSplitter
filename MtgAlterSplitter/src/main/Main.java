package main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import calculationutils.ClusterUtils;

public class Main {

	public static void main(String[] args) throws IOException {
		File file = new File("C:\\Users\\jt\\Pictures\\FINAL.png");
		BufferedImage alteredImage = ImageIO.read(file);
		int width = alteredImage.getWidth();
		int height = alteredImage.getHeight();
		int analyzedClusterSizeX = 2;
		int analyzedClusterSizeY = 2;

		// "69 Dudes!" Bill S. Preston, Esq., and Ted "Theodore" Logan aka the "Wyld
		// Stallyns"
		for (int tolerance = 696969; tolerance < 777777; tolerance = tolerance + 111111) {

			ArrayList<BufferedImage> processedImages = new ArrayList<>();

			// Create an array of images with all combinations of pixel shift that are
			// possible for the given
			// cluster sizes x and y.
			ClusterUtils.generatePreliminaryImageCollection(alteredImage, width, height, analyzedClusterSizeX,
					analyzedClusterSizeY, tolerance, processedImages);

			BufferedImage finalProcessedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

			// Compare pixels in a given Position in all preliminary images of provided
			// image array.
			// Returns image with pixels that where the same in all images.
			ClusterUtils.vectorilPixelCompare(width, height, processedImages, finalProcessedImage);

			// Removes artifacts, small areas of pixels which where found to be similar but
			// can't possibly be, as they are to small to be hand painted.
			ClusterUtils.removeSpecs(finalProcessedImage, analyzedClusterSizeX, analyzedClusterSizeY, 41);

			// Fills gaps in image, where no matching pixels have been found but where they
			// should be.
			ClusterUtils.fillGaps(alteredImage, finalProcessedImage, 69);

			// Save the resulting image.
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
