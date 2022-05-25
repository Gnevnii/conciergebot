package ru.gnev.conciergebot;

import com.github.romankh3.image.comparison.ImageComparisonUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

@DisplayName("Unit-level testing for ImageComparator")
class ImageComparatorTest {
    private static final ImageComparator comparator = new ImageComparator();

    @Test
    public void shouldConfirmImagesNotTheSame() {
        BufferedImage image1 = ImageComparisonUtil.readImageFromResources("images/image-1.jpg");
        BufferedImage image2 = ImageComparisonUtil.readImageFromResources("images/image-2.jpg");
        Assertions.assertFalse(comparator.isTheSameImages(image1, image2));
    }

    @Test
    public void shouldConfirmImagesTheSame() {
        BufferedImage image1 = ImageComparisonUtil.readImageFromResources("images/image-1.jpg");
        boolean theSameImages = comparator.isTheSameImages(image1, image1);
        Assertions.assertTrue(theSameImages);
    }
}