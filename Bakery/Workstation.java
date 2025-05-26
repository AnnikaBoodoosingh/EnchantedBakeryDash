import java.awt.*;

public class Workstation {
    private int x, y, width, height;
    private Image image; // Workstation image

    public Workstation(int x, int y, int width, int height, String imagePath) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = Toolkit.getDefaultToolkit().getImage(imagePath); // Load image
    }

    public void draw(Graphics g, int cameraX) {
        g.drawImage(image, x + cameraX, y, width, height, null); // Draw workstation image
        g.setColor(Color.BLACK);
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
    
    // Getter methods for x and width
    public int getX() {
        return x;
    }

    public int getWidth() {
        return width;
    }
}