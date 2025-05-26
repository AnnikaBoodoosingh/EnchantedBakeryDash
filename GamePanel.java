import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.Random; 

public class GamePanel extends JPanel {
    
    private BufferedImage image;
    private Workstation mixingStation;
    private Workstation bakingStation;
    private Workstation decoratingStation;
    private Workstation orderingStation;
    private Image backgroundImage;
    private SoundManager soundManager;
    private Baker bakerLeft;
    private Baker bakerRight;
    private Customer customer;
    private Timer gameTimer;
    private boolean facingRight = true;
    private Background background;
    private int cameraX = 0; // Camera X position
    private Timer soundDelayTimer;
    private boolean customerVisible = false;
    private Workstation door;
    private int selectedRecipeName;
    private boolean orderReceived = false;
    private int patienceLevel = 100; // from 100 to 0
    private Timer patienceDrainTimer;
    
     // Booleans for station completion
    private boolean ingredientsDone = false;
    private boolean bakingDone = false;
    private boolean decorDone = false;
    private boolean isGameOver = false;

    private int totalScore = 0;
    private int moneyEarned = 0;
    private int customerCount = 0; // Tracks the number of customers served
    
    private LevelManager levelManager = new LevelManager();

    public GamePanel() {
        //setPreferredSize(new Dimension(800, 350));
        bakerRight = new Baker("Images/baker_right.png", 700, 90);
        bakerLeft = new Baker("Images/baker_left.png", 700, 90);
        bakerRight.setPosition(50, 250);
        bakerLeft.setPosition(50, 250);
        
        orderingStation = new Workstation(200, 100, 250, 200,"Images/OrderStation.png");
        mixingStation = new Workstation(550, 100, 250, 200, "Images/IngredientStation.png");
        bakingStation = new Workstation(850, 100, 250, 200,"Images/OvenStation.png");
        decoratingStation = new Workstation(1150, 100, 250, 200,"Images/DecorStation.png");
        //door = new Workstation(0,0,250,300,"Door", "door.png");
        
        background = new Background(this,"Images/wall.png",96);
        
        soundManager = SoundManager.getInstance();
        soundManager.loadClip("Sounds/jazz.wav");
        soundManager.playClip("Sounds/jazz.wav", true);
        soundManager.setVolume("Sounds/jazz.wav", 0.8f);
        
        soundManager.loadClip("Sounds/Cupcake.wav");
        soundManager.loadClip("Sounds/cookie.wav");
        soundManager.loadClip("Sounds/cake.wav");
        soundManager.loadClip("Sounds/AngryCupcake.wav");
        soundManager.loadClip("Sounds/AngryCookie.wav");
        soundManager.loadClip("Sounds/AngryCake.wav");
        soundManager.setVolume("Sounds/AngryCake.wav", 1.0f);
        soundManager.setVolume("Sounds/AngryCookie.wav", 1.0f);
        soundManager.setVolume("Sounds/AngryCupcake.wav", 1.0f);
        
        // Make sure the GamePanel can receive key events
        setFocusable(true);
        requestFocusInWindow(); // Ensures the panel is ready to accept key inputs
        
        // Set layout to null for absolute positioning
        setLayout(null);
        
        // Create exit button and position it at the top-right corner
        JButton exitButton = new JButton("Exit");
        exitButton.setBounds(10, 10, 70, 30); // Adjusted x and y to avoid overlap
        exitButton.setFocusable(false); // Prevents button from stealing focus
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Confirm exit
                int result = JOptionPane.showConfirmDialog(GamePanel.this, "Are you sure you want to exit?", "Exit Game", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
        add(exitButton);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();
                checkWorkstationInteraction(mouseX, mouseY);
            }
        });

        gameTimer = new Timer(300, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        });
        gameTimer.start();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!orderReceived) return; // Don't allow movement before order

                int screenWidth = getWidth(); 
                int bakerX = bakerRight.getX();
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    facingRight = true;
                    bakerRight.moveRight();
                    bakerLeft.setPosition(bakerRight.getX(), bakerRight.getY());
                    // Only move the background if the baker isn't at the right boundary
                    if (bakerX < screenWidth - 100) { 
                        background.moveRight();
                        cameraX -= 10;
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    facingRight = false;
                    bakerLeft.moveLeft();
                    bakerRight.setPosition(bakerLeft.getX(), bakerLeft.getY());
                    // Only move the background if the baker isn't at the left boundary
                    if (bakerX > 100) { 
                        background.moveLeft();
                        cameraX += 10;
                    }
                }
                
                // Clamp cameraX to keep workstations within screen
                int maxCameraX = 0; // furthest left
                int minCameraX = -(decoratingStation.getX() + decoratingStation.getWidth() - getWidth()); // furthest right
                cameraX = Math.min(maxCameraX, Math.max(cameraX, minCameraX));
                
                repaint();
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                bakerRight.updateBoundaries(getWidth());
            }
        });
        
       // Timer to play sound after 5 seconds and display random customer
    soundDelayTimer = new Timer(5000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
    
            // Randomly choose a sound and customer
            Random random = new Random();
            int soundChoice = random.nextInt(3) + 1;  // Random number between 1 and 3
            int customerChoice = random.nextInt(3) + 1; // Random customer choice between 1 and 3
    
            // Play random sound based on soundChoice
            if (soundChoice == 1) {
                soundManager.playClip("Sounds/cookie.wav", false);
                selectedRecipeName = 1;
            } else if (soundChoice == 2) {
                soundManager.playClip("Sounds/Cupcake.wav", false);
                selectedRecipeName = 2;
            } else if (soundChoice == 3) {
                soundManager.playClip("Sounds/cake.wav", false);
                selectedRecipeName = 3;
            }
    
            // Create random customer based on customerChoice
            if (customerChoice == 1) {
                customer = new Customer("Images/fairy1.png", 90, 70, 80, 80);
            } else if (customerChoice == 2) {
                customer = new Customer("Images/fairy2.png", 90, 70, 80, 80);
            } else if (customerChoice == 3) {
                customer = new Customer("Images/fairy3.png", 90, 70, 80, 80);
            }
    
            // Make the customer visible
            customerVisible = true;
            orderReceived = true;
            // Start draining patience
            startPatienceDrain();
            
            // Stop the timer to prevent further customer creation or sound playback
            soundDelayTimer.stop();
        }
    });
    soundDelayTimer.setRepeats(false); // Ensures the timer only runs once
    soundDelayTimer.start(); 
    }
 
     private void startPatienceDrain() {
        patienceLevel = 100; // Reset to full
        if (patienceDrainTimer != null && patienceDrainTimer.isRunning()) {
            patienceDrainTimer.stop();
    }

    patienceDrainTimer = new Timer(3000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (patienceLevel > 0) {
                patienceLevel -= 1; // Adjust drain speed here
                repaint();
            } else {
                ((Timer) e.getSource()).stop();
                
                // Play "angry" sound based on recipe
                switch (selectedRecipeName) {
                    case 1: soundManager.playClip("Sounds/AngryCookie.wav", false); break;
                    case 2: soundManager.playClip("Sounds/AngryCupcake.wav", false); break;
                    case 3: soundManager.playClip("Sounds/AngryCake.wav", false); break;
                    default: break;
                }
                
                // Patience ran out — make customer leave
                orderReceived = false;
                repaint();

                // Delay customer disappearance for sound to finish
                Timer leaveTimer = new Timer(7000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        customerVisible = false;
                        orderReceived = false;
                        repaint();
                        
                        ingredientsDone = false;  // Unlock the mixing station
                        //bakingDone = false;       // Unlock the baking station
                        //decorDone = false;        // Unlock the decorating station

                        // Prepare next customer
                        soundDelayTimer.restart();
                    }
                });
                leaveTimer.setRepeats(false);
                leaveTimer.start();
            }
        }
    });
    patienceDrainTimer.start();
}

    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (image == null || image.getWidth() != getWidth() || image.getHeight() != getHeight()) {
            image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        }

        Graphics2D imageContext = (Graphics2D) image.getGraphics();
        
        // Draw the background
        background.draw(imageContext);
        
        // Calculate the new positions of the workstations based on the cameraX
        // int stationOffset = cameraX % (getWidth() * 3);  // Ensure the loop happens seamlessly
        int stationOffset = cameraX;
        orderingStation.draw(imageContext, stationOffset);
        mixingStation.draw(imageContext, stationOffset);
        bakingStation.draw(imageContext, stationOffset);
        decoratingStation.draw(imageContext, stationOffset);
        //door.draw(imageContext, stationOffset);

        // Draw the baker based on the direction
        if (facingRight) {
            bakerRight.draw(imageContext);
        } else {
            bakerLeft.draw(imageContext);
        }
        
        // Draw the customer if the customer is visible
        if (customerVisible) {
            customer.draw(imageContext, cameraX);
        }
        
        if (customerVisible) {
            // Draw the patience bar above the customer
            int barX = 90 + cameraX; // Match customer x
            int barY = 60;           // Slightly above customer
            int barWidth = 80;
            int barHeight = 10;
        
            // Draw border
            imageContext.setColor(Color.GRAY);
            imageContext.drawRect(barX, barY, barWidth, barHeight);
        
            // Draw fill
            imageContext.setColor(new Color(255, 102, 102));
            int fillWidth = (int)(barWidth * (patienceLevel / 100.0));
            imageContext.fillRect(barX + 1, barY + 1, fillWidth, barHeight - 1);
        
            // Optional: Patience percentage
            imageContext.setColor(Color.BLACK);
            imageContext.setFont(new Font("SansSerif", Font.BOLD, 10));
            imageContext.drawString("Patience: " + patienceLevel + "%", barX, barY - 2);
        
            // Optional: Message when empty
            if (patienceLevel <= 0) {
                //imageContext.setColor(Color.RED.darker());
                //imageContext.drawString("Patience Empty!", barX + 10, barY + barHeight + 15);
            }
        }
        
        g.drawImage(image, 0, 0, this);
        imageContext.dispose();
        
        
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.setColor(Color.BLACK);
        g.drawString("Score: " + totalScore, 20, getHeight() - 20);
        g.drawString("Money: $" + moneyEarned, getWidth() - 120, getHeight() - 20);
        
        levelManager.paint((Graphics2D) g, getWidth(), getHeight());
        
    }
    
    public void checkWorkstationInteraction(int mouseX, int mouseY) {
        if (!orderReceived) return; // Don't allow movement before order

        // Adjust mouseX with camera offset to ensure correct interaction
        if (mixingStation.isMouseOver(mouseX - cameraX, mouseY) && !ingredientsDone) {
            //ingredientsDone = true;
            SwingUtilities.invokeLater(() -> {
                JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Mixing Minigame", Dialog.ModalityType.APPLICATION_MODAL);
            
                IngredientGame miniGame = new IngredientGame(selectedRecipeName);
                dialog.getContentPane().add(miniGame);
                dialog.pack();
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                
                 if (miniGame.isWon()) {
                    ingredientsDone = true;
                }
                
                
                if (miniGame.isWon()) {
                    ingredientsDone = true;
                    totalScore += miniGame.getScore(); // Add oven game score
                }
                
            });
        } else if (bakingStation.isMouseOver(mouseX - cameraX, mouseY) && ingredientsDone && !bakingDone) {
            //bakingDone = true;
            SwingUtilities.invokeLater(() -> {
                JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Oven Minigame - Don't Burn It!", Dialog.ModalityType.APPLICATION_MODAL);
                OvenGame ovenGame = new OvenGame(selectedRecipeName); 
                dialog.getContentPane().add(ovenGame);
                dialog.pack();
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                
                 if (ovenGame.isWon()) {
                    bakingDone = true;  // Allow progress to the next stage
                }
                
                
                if (ovenGame.isWon()) {
                    bakingDone = true;
                    totalScore += ovenGame.getScore(); // Add oven game score
                }
                
                //bakingDone = true;
            });
        } else if (decoratingStation.isMouseOver(mouseX - cameraX, mouseY) && bakingDone && !decorDone) {
            //decorDone = true;
            SwingUtilities.invokeLater(() -> {
                JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Decoration Minigame", Dialog.ModalityType.APPLICATION_MODAL);
                DecorationGame decorationGame = new DecorationGame(selectedRecipeName);
                dialog.getContentPane().add(decorationGame);
                dialog.pack();
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                
                if (decorationGame.isWon()) {
                    decorDone = true;  // Allow progress to the next stage
                }
                
                if (decorationGame.isWon()) {
                    decorDone = true;
                    totalScore += decorationGame.getScore(); // Add oven game score
                }
            });
        } else if (orderingStation.isMouseOver(mouseX - cameraX, mouseY)) {
            if (ingredientsDone && bakingDone && decorDone) {
                // Serve the customer
                moneyEarned += 50;
                customerVisible = false;
                orderReceived = false;
                ingredientsDone = false;
                bakingDone = false;
                decorDone = false;
                
                //moneyEarned += 50;
                totalScore += 25;
                
                if (patienceLevel > 70){
                    totalScore += 15;
                }
                else if (patienceLevel > 50){
                    totalScore += 10;
                }
                else if (patienceLevel >30){
                    totalScore += 5;
                }
                
                
                levelManager.checkLevelTransition(moneyEarned);
                levelManager.calculateScore(totalScore);

                
                repaint();
    
                JOptionPane.showMessageDialog(this, "Customer served!\n+ $50 earned!", "Order Complete", JOptionPane.INFORMATION_MESSAGE);
                
               // new Timer(2000, e -> {
                    soundDelayTimer.restart();
               // });
    
            } else {
                JOptionPane.showMessageDialog(this, "Finish all tasks before serving!", "Not Ready", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
}