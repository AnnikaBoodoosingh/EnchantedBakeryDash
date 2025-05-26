import java.awt.*;
import java.awt.image.BufferedImage;

public class Pastry {
    private BufferedImage pastryImg;
    private Image scaledPastry;

    public Pastry(String pastryImagePath, int recipeIndex) {
        try {
            pastryImg = ImageManager.loadBufferedImage(pastryImagePath);
            scalePastry(recipeIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void scalePastry(int recipeIndex) {
        double scaleFactor = 0.2; // default for cupcake

        if (recipeIndex == 1) { // cookie
            scaleFactor = 0.4;
        } else if (recipeIndex == 3) { // cake
            scaleFactor = 0.4;
        }

        int pastryW = (int) (pastryImg.getWidth() * scaleFactor);
        int pastryH = (int) (pastryImg.getHeight() * scaleFactor);
        scaledPastry = pastryImg.getScaledInstance(pastryW, pastryH, Image.SCALE_SMOOTH);
    }

    public Image getScaledPastry() {
        return scaledPastry;
    }
}
