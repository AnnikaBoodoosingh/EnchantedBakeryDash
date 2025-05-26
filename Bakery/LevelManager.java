import java.awt.*;

public class LevelManager {
    public static int currentLevel;
    private boolean levelTwoActivated;
    private boolean gameCompleted;
    private boolean gameOver; // new flag to indicate the game is over
    private boolean showLevelPopup;
    private boolean showCompletionPopup;
    private long popupStartTime;
    private final int popupDuration = 5000; // show for 5 seconds
    private int score;
    
    public LevelManager() {
        currentLevel = 1;
        levelTwoActivated = false;
        gameCompleted = false;
        gameOver = false; // initialize game as not over
        showLevelPopup = false;
        showCompletionPopup = false;
    }
    
    public void calculateScore(int score) {
        this.score = score;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public boolean isGameCompleted() {
        return gameCompleted;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void checkLevelTransition(int moneyEarned) {
        if (gameOver) {
            return; // Don't allow any further gameplay when the game is over
        }

        // Transition to level 2 when money reaches 100
        if (!levelTwoActivated && moneyEarned == 100) {
            currentLevel = 2;
            levelTwoActivated = true;
            showLevelPopup = true;
            popupStartTime = System.currentTimeMillis();
        }

        // Game completion when money reaches 200
        if (currentLevel == 2 && moneyEarned == 200) {
            gameCompleted = true;
            showCompletionPopup = true;
            popupStartTime = System.currentTimeMillis();
            gameOver = true; // Mark the game as over
        }
    }

    public void paint(Graphics2D g2, int panelWidth, int panelHeight) {
        if (showLevelPopup) {
            long elapsed = System.currentTimeMillis() - popupStartTime;
            if (elapsed > popupDuration) {
                showLevelPopup = false;
            } else {
                int boxWidth = 200;
                int boxHeight = 100;
                int x = (panelWidth - boxWidth) / 2;
                int y = (panelHeight - boxHeight) / 2;

                g2.setColor(new Color(0, 0, 0, 180)); // semi-transparent background
                g2.fillRoundRect(x, y, boxWidth, boxHeight, 20, 20);

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 20));
                String message = "Level 2!";
                int strWidth = g2.getFontMetrics().stringWidth(message);
                g2.drawString(message, x + (boxWidth - strWidth) / 2, y + boxHeight / 2 + 7);
            }
        }

    if (showCompletionPopup) {
        long elapsed = System.currentTimeMillis() - popupStartTime;
        if (elapsed > popupDuration) {
            showCompletionPopup = false;
            
            // Exit after 3 seconds
            System.exit(0); // <--- This will end the game
        } else {
            int boxWidth = 300;
            int boxHeight = 150;
            int x = (panelWidth - boxWidth) / 2;
            int y = (panelHeight - boxHeight) / 2;
    
            g2.setColor(new Color(0, 0, 0, 180)); // semi-transparent background
            g2.fillRoundRect(x, y, boxWidth, boxHeight, 20, 20);
    
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            String message = "Game Completed! Score: " + score;
            int strWidth = g2.getFontMetrics().stringWidth(message);
            g2.drawString(message, x + (boxWidth - strWidth) / 2, y + boxHeight / 2 + 7);
        }
    }
    }
}
