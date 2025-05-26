import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Customer {
    private BufferedImage image;
    private final int x, y; // Fixed x and y coordinates for the customer's position
    private final int width, height; // To scale the customer image size

    public Customer(String imagePath, int startX, int startY, int width, int height) {
        try {
            image = ImageIO.read(new File(imagePath)); // Load the image (not a sprite sheet)
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.x = startX;  // Set initial X position
        this.y = startY;  // Set initial Y position
        this.width = width; // Set scaled width for customer
        this.height = height; // Set scaled height for customer
    }

    // Getter for X position
    public int getX() {
        return x;
    }

    // Getter for Y position
    public int getY() {
        return y;
    }

    public void draw(Graphics g, int cameraX) {
        // Apply camera offset to the X position, keeping the customer fixed relative to the camera
        if (image != null) {
            g.drawImage(image, x + cameraX, y, width, height, null); // Apply camera offset to the X position
        }
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}