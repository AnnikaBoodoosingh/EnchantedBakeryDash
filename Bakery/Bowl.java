import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

public class Bowl {
    private int x;
    private final int width = 90;
    private final int height = 70;
    private Image image;

    private final int screenWidth;
    private final int screenHeight;

    public Bowl(int screenWidth, int screenHeight, Image image) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.image = image;
        this.x = screenWidth / 2 - width / 2;
    }

    public void moveLeft() {
        if (x > 0) x -= 20;
    }

    public void moveRight() {
        if (x < screenWidth - width) x += 20;
    }

    public void draw(Graphics g) {
        if (image != null) {
            g.drawImage(image, x, screenHeight - 100, width, height, null);
        } else {
            g.setColor(new Color(255, 182, 193));
            g.fillRoundRect(x, screenHeight - 100, width, height, 20, 20);
        }
    }

    public int getX() {
        return x;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getY() {
        return screenHeight - 100;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, getY(), width, height);
    }
}