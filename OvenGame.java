import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class OvenGame extends JPanel implements ActionListener, KeyListener {

    private BufferedImage ovenBg, flameImg;
    private Image scaledFlame;
    private Timer gameTimer;

    private int timePassed = 0;
    private int flameAngle = 0;
    private int perfectBakeTime = 27;
    private final int maxTime = 35;
    private boolean isBaked = false;
    private boolean isBurnt = false;
    private boolean isRaw = false;
    private boolean gameStarted = false;
    static int recipe;
    private int selectedRecipeIndex;
    private Pastry pastry;  // Instance of the Pastry class
    
    public OvenGame(int selectedRecipeIndex) {
        this.selectedRecipeIndex = selectedRecipeIndex;
        recipe = selectedRecipeIndex;
        
        String imageName;
            switch (selectedRecipeIndex) {
                case 1: imageName = "Images/FairyCookie.png"; break;
                case 2: imageName = "Images/pastry.png"; break;
                case 3: imageName = "Images/FairyCake.png"; break;
                default: imageName = "Images/FairyCookie.png"; break;  // Fallback
        }
        pastry = new Pastry(imageName,selectedRecipeIndex);

        //pastry = new Pastry("pastry.png");  // Initialize Pastry object

        setPreferredSize(new Dimension(400, 500));
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(this);
        setDoubleBuffered(true);

        try {
            ovenBg = ImageManager.loadBufferedImage("Images/oven.png");
            flameImg = ImageManager.loadBufferedImage("Images/flame.png");

            int flameW = flameImg.getWidth() / 6;
            int flameH = flameImg.getHeight() / 6;
            scaledFlame = flameImg.getScaledInstance(flameW, flameH, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            e.printStackTrace();
        }

        gameTimer = new Timer(1000, this);
    }

    public static int getSelectedRecipe() {
        return recipe;
    }

    public void startGame() {
        gameTimer.start();
    }

    public boolean gameEnded() {
        return isBaked || isBurnt || isRaw;
    }
    
    public boolean isWon() {
        return isBaked;  // Player wins if the pastry is perfectly baked
    }

    public void stopEarly() {
        if (timePassed == perfectBakeTime) {
            isBaked = true;
            isWon();
        } else if (timePassed > perfectBakeTime) {
            isBurnt = true;
        } else {
            isRaw = true;
        }
        gameTimer.stop();
        repaint();
    }
    
    public int getScore() {
        if (isBaked) {
            return 75; // Perfect bake
        } else if (isRaw) {
            return 0;  // Raw pastry
        } else if (isBurnt) {
            return 0;   // Burnt pastry
        }
        return 0; // No score yet if not finished
    }

    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        BufferedImage offScreenBuffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = offScreenBuffer.createGraphics();

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        
        if (LevelManager.currentLevel == 2){
            perfectBakeTime = 15;
        }else{
            perfectBakeTime = 27;
        }

        if (ovenBg != null) {
            int bgX = (getWidth() - ovenBg.getWidth()) / 2;
            int bgY = (getHeight() - ovenBg.getHeight()) / 2;
            g2d.drawImage(ovenBg, bgX, bgY, null);
        }

        if (pastry != null && pastry.getScaledPastry() != null) {
            Image scaledPastry = pastry.getScaledPastry();
            int w = scaledPastry.getWidth(null);
            int h = scaledPastry.getHeight(null);
            g2d.drawImage(scaledPastry, centerX - w / 2, centerY - h / 2 + 25, null);
        }

        if (!gameStarted) {
            g.setFont(new Font("Serif", Font.BOLD, 16));
            g.setColor(Color.DARK_GRAY);
            g.drawString("Press ENTER to start baking!", 90, 30);

            String instructionsLine1 = "Press SPACE to stop";
            String instructionsLine2 = "when the pastry is perfectly done!";

            int line1Width = g.getFontMetrics().stringWidth(instructionsLine1);
            int line2Width = g.getFontMetrics().stringWidth(instructionsLine2);

            g.drawString(instructionsLine1, (getWidth() - line1Width) / 2, getHeight() - 50);
            g.drawString(instructionsLine2, (getWidth() - line2Width) / 2, getHeight() - 30);

            g.drawImage(offScreenBuffer, 0, 0, null);
            return;
        }

        if (!gameEnded()) {
            if (scaledFlame != null) {
                double radius = 80;
                double angleRad = Math.toRadians(flameAngle);
                int fx = (int) (centerX + radius * Math.cos(angleRad)) - scaledFlame.getWidth(null) / 2;
                int fy = (int) (centerY + radius * Math.sin(angleRad)) - scaledFlame.getHeight(null) / 2;
                g2d.drawImage(scaledFlame, fx, fy, null);
            }

            g.setFont(new Font("SansSerif", Font.BOLD, 18));
            g.setColor(Color.BLACK);
            g.drawString("Timer: " + timePassed + "s", 20, 30);

            if (timePassed < perfectBakeTime) {
                g.drawString("Status: Still raw...", 20, 60);
            } else if (timePassed == perfectBakeTime && !isBaked) {
                g.drawString("Status: Perfect Bake!", 20, 60);
            } else {
                g.drawString("Status: Danger! Burn risk!", 20, 60);
            }

            g.setFont(new Font("SansSerif", Font.PLAIN, 14));
            g.drawString("Press SPACE to stop", 20, getHeight() - 30);
        } else {
            g.setFont(new Font("SansSerif", Font.BOLD, 22));
            g.setColor(Color.BLACK);
            String status = isBaked ? "Perfect Bake!" : isBurnt ? "Burnt!" : "Raw!";
            g.drawString("Status: " + status, 20, 60);
        }

        g.drawImage(offScreenBuffer, 0, 0, null);
        g2d.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameEnded()) {
            timePassed++;
            flameAngle = (flameAngle + 10) % 360;

            if (timePassed > maxTime) {
                isBurnt = true;
                gameTimer.stop();
            }

            repaint();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!gameStarted && e.getKeyCode() == KeyEvent.VK_ENTER) {
            gameStarted = true;
            startGame();
        }

        if (gameStarted && !gameEnded()) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                stopEarly();
            }
        }

        repaint();
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        int selectedRecipeIndex = getSelectedRecipe();
        JFrame frame = new JFrame("Oven Minigame");
        OvenGame ovenGame = new OvenGame(selectedRecipeIndex);
        frame.add(ovenGame);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
        ovenGame.requestFocusInWindow();
    }
}
