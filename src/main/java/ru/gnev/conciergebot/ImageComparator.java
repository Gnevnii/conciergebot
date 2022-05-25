package ru.gnev.conciergebot;

import com.github.romankh3.image.comparison.ImageComparison;
import com.github.romankh3.image.comparison.model.ImageComparisonResult;
import com.github.romankh3.image.comparison.model.ImageComparisonState;

import java.awt.image.BufferedImage;

public class ImageComparator {

    public boolean isTheSameImages(BufferedImage image1, BufferedImage image2) {
        ImageComparison imageComparison = new ImageComparison(image1, image2);
        ImageComparisonResult imageComparisonResult = imageComparison.compareImages();
        return ImageComparisonState.MATCH == imageComparisonResult.getImageComparisonState();
    }
}
