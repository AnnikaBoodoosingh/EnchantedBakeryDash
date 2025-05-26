import javax.swing.*;

public class GameWindow {
    private JFrame frame;
    private StartScreen startScreen;
    private GamePanel gamePanel;

    public GameWindow() {
        frame = new JFrame("Bakery Cooking Game");

        // Initialize the start screen and pass this GameWindow instance
        startScreen = new StartScreen(this);
        
        // Initialize the game panel
        gamePanel = new GamePanel();

        // Set up the frame for start screen initially
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600); // Window size
        frame.add(startScreen);  // Add start screen
        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Switch to the game panel when the start button is clicked
    public void switchToGamePanel() {
        frame.remove(startScreen);  // Remove start screen
        frame.add(gamePanel);        // Add game panel
        frame.revalidate();          // Revalidate the frame
        //frame.pack();
        frame.repaint();             // Repaint the frame
        gamePanel.requestFocusInWindow(); // Ensure GamePanel gets keyboard focus
    }
}
