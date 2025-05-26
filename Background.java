import javax.swing.JPanel;
import java.awt.Graphics2D;
import java.awt.Image;

public class Background {
    private Image bgImage;
    private int bgImageWidth;
    private GamePanel panel;
    private int bgX;
    private int bg1X;
    private int bg2X;
    private int bgDX;

    public Background(GamePanel panel, String imageFile, int bgDX) {
        this.panel = panel;
        this.bgImage = ImageManager.loadImage(imageFile);
        bgImageWidth = bgImage.getWidth(null);
        this.bgDX = 200;
        bgX = 0;
        bg1X = 0;
        bg2X = bgImageWidth;
    }

    public void move(int direction) {
        if (direction == 1)
            moveRight();
        else if (direction == 2)
            moveLeft();
    }

    public void moveLeft() {
        bgX -= bgDX;
        bg1X -= bgDX;
        bg2X -= bgDX;

        if (bg1X < (bgImageWidth * -1)) {
            bg1X = 0;
            bg2X = bgImageWidth;
        }
    }

    public void moveRight() {
        bgX += bgDX;
        bg1X += bgDX;
        bg2X += bgDX;

        if (bg1X > 0) {
            bg1X = bgImageWidth * -1;
            bg2X = 0;
        }
    }

    public void draw(Graphics2D g2) {
        g2.drawImage(bgImage, bg1X, 0, null);
        g2.drawImage(bgImage, bg2X, 0, null);
    }
}