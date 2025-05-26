import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.io.IOException;

public class StartScreen extends JPanel {
    private GameWindow gameWindow; // Reference to the GameWindow
    private BufferedImage image;
    private Image backgroundImage;
    private Image startButtonImage;
    private Image exitButtonImage;
    private Rectangle startButtonArea;
    private Rectangle exitButtonArea;
    private SoundManager soundManager;

    public StartScreen(GameWindow gameWindow) {
        this.gameWindow = gameWindow;  // Store the reference to GameWindow

        backgroundImage = ImageManager.loadImage("Images/menu.png");
        startButtonImage = ImageManager.loadImage("Images/start.png");
        exitButtonImage = ImageManager.loadImage("Images/exit.png");

        // Set button sizes
        int buttonWidth = startButtonImage.getWidth(null) / 2;
        int buttonHeight = startButtonImage.getHeight(null) / 2;
        
        // Initially position buttons outside of the screen (we will update them once the panel is shown)
        startButtonArea = new Rectangle(0, 0, buttonWidth, buttonHeight);
        exitButtonArea = new Rectangle(0, 0, buttonWidth, buttonHeight);
        
        soundManager = SoundManager.getInstance();  // Ensure SoundManager is initialized
        soundManager.loadClip("Sounds/jazz.wav");
        soundManager.playClip("Sounds/jazz.wav", true);

        // Add mouse listener for button clicks
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();

                if (startButtonArea.contains(mouseX, mouseY)) {
                    // Switch to game screen
                    gameWindow.switchToGamePanel();  // Use the GameWindow's method to switch
                } else if (exitButtonArea.contains(mouseX, mouseY)) {
                    System.exit(0); // Exit the game
                }
            }
        });

        // Add component listener to update button positions when the panel is shown
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateButtonPositions();
            }
        });
    }

    // Update button positions when the panel is resized
    private void updateButtonPositions() {
        int buttonWidth = startButtonImage.getWidth(null) / 2;
        int buttonHeight = startButtonImage.getHeight(null) / 2;
        
        // Center the buttons on the screen
        int startButtonX = (getWidth() - buttonWidth) / 2;
        int startButtonY = getHeight() / 3;
        int exitButtonX = (getWidth() - buttonWidth) / 2;
        int exitButtonY = getHeight() / 2;

        // Define button areas with smaller sizes and centered position
        startButtonArea.setBounds(startButtonX, startButtonY, buttonWidth, buttonHeight);
        exitButtonArea.setBounds(exitButtonX, exitButtonY, buttonWidth, buttonHeight);

        // Repaint to reflect the changes
        repaint();
    }

    // Paint the background and buttons using double buffering
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Create the BufferedImage only after the panel is properly sized
        if (image == null || image.getWidth() != getWidth() || image.getHeight() != getHeight()) {
            // Create the new BufferedImage based on the current size of the panel
            image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        }

        // Create an off-screen image (double buffering)
        Graphics2D imageContext = (Graphics2D) image.getGraphics();

        // Draw background
        if (backgroundImage != null) {
            imageContext.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        // Draw buttons
        if (startButtonImage != null) {
            imageContext.drawImage(startButtonImage, startButtonArea.x, startButtonArea.y, startButtonArea.width, startButtonArea.height, this);
        }
        if (exitButtonImage != null) {
            imageContext.drawImage(exitButtonImage, exitButtonArea.x, exitButtonArea.y, exitButtonArea.width, exitButtonArea.height, this);
        }

        // Transfer the off-screen image to the panel's screen
        g.drawImage(image, 0, 0, this);
        
        imageContext.dispose();
    }
}
