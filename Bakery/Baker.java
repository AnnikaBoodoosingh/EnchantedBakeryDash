import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Baker {
    private BufferedImage spriteSheet;
    private BufferedImage scaledSpriteSheet;
    private int x, y;
    private int currentFrame;
    private int totalFrames = 8;
    private int frameWidth, frameHeight;
    private int scaledWidth, scaledHeight;
    private int speed = 10; // Baker's movement speed
    private int leftBoundary = 0;  // Left screen boundary
    private int rightBoundary; // Will be dynamically set

    // Constructor without position, now you set it later
    public Baker(String spriteSheetPath, int newWidth, int newHeight) {
        try {
            spriteSheet = ImageIO.read(new File(spriteSheetPath));

            // Scale the image to the new size
            scaledSpriteSheet = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = scaledSpriteSheet.createGraphics();
            g2d.drawImage(spriteSheet, 0, 0, newWidth, newHeight, null);
            g2d.dispose();

        } catch (IOException e) {
            e.printStackTrace();
        }

        this.scaledWidth = newWidth;
        this.scaledHeight = newHeight;
        frameWidth = scaledWidth / totalFrames;
        frameHeight = scaledHeight;
        
        // Default position for the baker, but can be set later
        this.x = 0;  
        this.y = 0; 
        this.currentFrame = 0;
    }

    // Set the baker's position
    public void setPosition(int startX, int startY) {
        this.x = startX;
        this.y = startY;
    }

    // Dynamically update the right boundary based on the current panel width
    public void updateBoundaries(int panelWidth) {
        this.rightBoundary = panelWidth - frameWidth; // Right boundary is the width of the panel minus baker's sprite width
    }

    // Move the baker to the right
    public void moveRight() {
        if (x + speed <= rightBoundary) {  // Check if moving right stays within screen bounds
            x += speed;
            currentFrame = (currentFrame + 1) % totalFrames; // Cycle animation
        }
    }

    // Move the baker to the left
    public void moveLeft() {
        if (x - speed >= leftBoundary) {  // Check if moving left stays within screen bounds
            x -= speed;
            currentFrame = (currentFrame + 1) % totalFrames; // Cycle animation
        }
    }

    public void draw(Graphics g) {
        int xOffset = currentFrame * frameWidth;
        g.drawImage(scaledSpriteSheet, x, y, x + frameWidth, y + frameHeight, xOffset, 0, xOffset + frameWidth, frameHeight, null);
    }

    public int getX() { return x; }
    
    public int getY() { return y; }
}