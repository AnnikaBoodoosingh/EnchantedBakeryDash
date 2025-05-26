import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;

public class IngredientGame extends JPanel implements ActionListener, KeyListener {

    Timer gameTimer;
    Timer countdownTimer;
    Timer mixingAnimationTimer;

    Random rand = new Random();
    List<Ingredient> fallingIngredients = new ArrayList<>();

    List<String> shoppingList = new ArrayList<>();
    Map<String, Integer> caughtMap = new HashMap<>();
    Map<String, Image> ingredientImages = new HashMap<>();

    boolean won = false;
    boolean timeUp = false;
    int timeLeft = 90;
    boolean gameStarted = false;
    static int recipe;
    
    int score = 0;
    
    final int GAME_WIDTH = 400;
    final int GAME_HEIGHT = 500;

    private SoundManager soundManager;
    private Bowl bowl;
    
    // Animation variables
    private Image mixingImage;
    private int mixingFrame = 0;
    private final int FRAME_COUNT = 2; // Assuming 2 frames in the animation
    private final int ANIMATION_INTERVAL = 500; // 500ms per frame change

    private int baseScoreFromPanel;
    private boolean levelTwoActivated = false;

    public IngredientGame(int selectedRecipeIndex) {
    
        //this.baseScoreFromPanel = gamePanelScore;
        
        recipe = selectedRecipeIndex;
        
        setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        setBackground(new Color(250, 245, 235)); // creamy beige
        setFocusable(true);
        addKeyListener(this);

        soundManager = SoundManager.getInstance();
        soundManager.loadClip("collect.wav");
        soundManager.loadClip("wrong.wav");
        //soundManager.loadClip("stir.wav");

        try {
            ingredientImages.put("Egg", ImageManager.loadImage("egg.png"));
            ingredientImages.put("Butter", ImageManager.loadImage("butter.png"));
            ingredientImages.put("Sugar", ImageManager.loadImage("sugarCube.png"));
            ingredientImages.put("Flour", ImageManager.loadImage("flour.png"));
            ingredientImages.put("Milk", ImageManager.loadImage("milk.png"));
            ingredientImages.put("BadEgg", ImageManager.loadImage("badEgg.png"));
            ingredientImages.put("BadMilk", ImageManager.loadImage("badMilk.png"));
            ingredientImages.put("BadSugar", ImageManager.loadImage("sugarCubeBad.png"));
            ingredientImages.put("BadButter", ImageManager.loadImage("badButter.png"));
            ingredientImages.put("BadFlour", ImageManager.loadImage("badFlour.png"));

            mixingImage = ImageManager.loadImage("mixing.png"); // Assuming this is the sprite sheet
            Image bowlImage = ImageManager.loadImage("bowl.png");
            bowl = new Bowl(GAME_WIDTH, GAME_HEIGHT, bowlImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        generateShoppingList(selectedRecipeIndex);
        spawnIngredient();

        gameTimer = new Timer(30, this);
        countdownTimer = new Timer(1000, e -> {
            if (!won && timeLeft > 0) {
                timeLeft--;
                if (timeLeft < 0) {
                    timeLeft = Math.max(timeLeft, 0);
                }
                repaint();
                if (timeLeft == 0) {
                    timeUp = true;
                    gameTimer.stop();
                }
            }
        });

        mixingAnimationTimer = new Timer(ANIMATION_INTERVAL, e -> {
            mixingFrame = (mixingFrame + 1) % FRAME_COUNT;
            repaint();
        });
    }

    public static int getSelectedRecipe() {
        return recipe;
    }

    public void generateShoppingList(int selectedRecipeIndex) {
        shoppingList.clear();
        caughtMap.clear();

        Map<String, List<String>> recipes = new HashMap<>();
        recipes.put("Cupcake", Arrays.asList("Egg", "Egg", "Sugar", "Flour"));
        recipes.put("Cookie", Arrays.asList("Butter", "Sugar", "Flour", "Milk"));
        recipes.put("Cake", Arrays.asList("Egg", "Sugar", "Sugar", "Flour", "Flour"));

        String selectedRecipe = getRecipeByIndex(selectedRecipeIndex);
        List<String> selectedIngredients = recipes.get(selectedRecipe);
        shoppingList.addAll(selectedIngredients);
    }

    public String getRecipeByIndex(int index) {
        switch (index) {
            case 1: return "Cookie";
            case 2: return "Cupcake";
            case 3: return "Cake";
            default: return "Cookie";
        }
    }

    public void spawnIngredient() {
        while (fallingIngredients.size() < 3) {
            Ingredient ingredient = Ingredient.randomIngredient(GAME_WIDTH, GAME_HEIGHT);

            boolean overlap = false;
            for (Ingredient existingIngredient : fallingIngredients) {
                if (Math.abs(existingIngredient.getX() - ingredient.getX()) < 40 &&
                    Math.abs(existingIngredient.getY() - ingredient.getY()) < 40) {
                    overlap = true;
                    break;
                }
            }

            if (!overlap) {
                fallingIngredients.add(ingredient);
            }
        }
    }

    public void paintComponent(Graphics g) {
    super.paintComponent(g);

    if (!gameStarted) {
        // Instructions for starting the game
        g.setColor(new Color(255, 182, 193));
        g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Serif", Font.BOLD, 15));

        String[] instructions = {
            "Welcome to Ingredient Workstation!",
            "Catch the falling ingredients in the bowl.",
            "Use the LEFT and RIGHT arrow keys to move the bowl.",
            "Press ENTER to start the game."
        };

        int yPosition = 150;
        for (String line : instructions) {
            int textWidth = g.getFontMetrics().stringWidth(line);
            g.drawString(line, (GAME_WIDTH - textWidth) / 2, yPosition);
            yPosition += 40;
        }

        int ingredientYPosition = yPosition + 30;
        g.setFont(new Font("Serif", Font.BOLD, 18));
        g.drawString("Order: " + getRecipeByIndex(recipe), (GAME_WIDTH - g.getFontMetrics().stringWidth("Order: " + getRecipeByIndex(recipe))) / 2, yPosition);

        String ingredients = switch (getRecipeByIndex(recipe)) {
            case "Cupcake" -> "Ingredients: Egg, Egg, Sugar, Flour";
            case "Cookie" -> "Ingredients: Butter, Milk, Sugar, Flour";
            case "Cake" -> "Ingredients: Egg, Sugar, Sugar, Flour, Flour";
            default -> "";
        };
        g.drawString(ingredients, (GAME_WIDTH - g.getFontMetrics().stringWidth(ingredients)) / 2, ingredientYPosition);
        return;
    }

    if (won) {
        // Draw the score above the animation
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        String scoreText = "Score: " + score;
        int scoreWidth = g.getFontMetrics().stringWidth(scoreText);
        g.drawString(scoreText, (GAME_WIDTH - scoreWidth) / 2, GAME_HEIGHT / 2 + 20); // slightly above the animation
    
        // Determine the frame width and height based on the mixingImage
        int frameWidth = mixingImage.getWidth(null) / FRAME_COUNT;
        int frameHeight = mixingImage.getHeight(null);
    
        // Make the animation smaller (scale by 0.5x)
        int scaledWidth = (int) (frameWidth * 0.5);
        int scaledHeight = (int) (frameHeight * 0.5);
    
        // Center animation horizontally
        int xPosition = (GAME_WIDTH - scaledWidth) / 2;
        int yPosition = GAME_HEIGHT / 2 + 40; // keep animation lower
    
        // Draw mixing animation
        g.drawImage(mixingImage, xPosition, yPosition, xPosition + scaledWidth, yPosition + scaledHeight,
                    mixingFrame * frameWidth, 0, (mixingFrame + 1) * frameWidth, frameHeight, this);
                    
        isWon();
    }

    else if (!timeUp) {
        // Normal game drawing
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("Recipe: " + shoppingList, 20, 30);
        g.drawString("Caught: " + caughtMap, 20, 50);
        g.drawString("Order: " + getRecipeByIndex(recipe), 20, 70);
        g.drawString("Time Left: " + timeLeft + "s", GAME_WIDTH - 100, 30);
    
        for (Ingredient ingredient : fallingIngredients) {
            Image img = ingredientImages.get(ingredient.name);
            if (img != null) {
                g.drawImage(img, ingredient.x, ingredient.y, 40, 40, this);
            } else {
                g.drawString(ingredient.name, ingredient.x, ingredient.y);
            }
        }
        bowl.draw(g);
    }

    // End of game message
    if (won || timeUp) {
        g.setFont(new Font("Arial", Font.BOLD, 30));
        String message = won ? "Collected All Ingredients!" : "Ingredients Missing!";
        int messageWidth = g.getFontMetrics().stringWidth(message);
        g.setColor(Color.PINK);
        g.drawString(message, (GAME_WIDTH - messageWidth) / 2, (GAME_HEIGHT / 2)-20);
    }
    
    
    if (timeUp == true){
        g.setFont(new Font("Arial", Font.BOLD, 30));
        String message = won ? "Collected All Ingredients!" : "Ingredients Missing!";
        int messageWidth = g.getFontMetrics().stringWidth(message);
        g.setColor(Color.PINK);
        g.drawString(message, (GAME_WIDTH - messageWidth) / 2, (GAME_HEIGHT / 2)-20);
        
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        String scoreText = "Score: " + score;
        int scoreWidth = g.getFontMetrics().stringWidth(scoreText);
        g.drawString(scoreText, (GAME_WIDTH - scoreWidth) / 2, GAME_HEIGHT / 2 + 20);
    }
   }

    public int getScore() {
        return score;
    }
    
    public boolean isWon() {
        return won;
    }
    
    
    public void actionPerformed(ActionEvent e) {
        if (!won && !timeUp) {
            List<Ingredient> caughtOrMissed = new ArrayList<>();
            for (Ingredient ingredient : fallingIngredients) {
                //ingredient.fall(5);
                if (LevelManager.currentLevel == 2){
                    ingredient.fall(8);
                } else{
                    ingredient.fall(4);
                }


                if (ingredient.isCaught(bowl.getX(), bowl.getY(), bowl.getWidth(), bowl.getHeight())) {
                    long required = shoppingList.stream().filter(i -> i.equals(ingredient.name)).count();
                    int caughtSoFar = caughtMap.getOrDefault(ingredient.name, 0);

                    if (Arrays.asList(Ingredient.BAD_INGREDIENTS).contains(ingredient.name) || caughtSoFar >= required) {
                        soundManager.playClip("wrong.wav", false);
                            if (timeLeft > 0) {
                                timeLeft -= 5; // Penalize the player
                                timeLeft = Math.max(timeLeft, 0); // Prevent going negative
                            }
                        if (!Arrays.asList(Ingredient.BAD_INGREDIENTS).contains(ingredient.name)) {
                            //timeLeft -= 5;
                        }
                    } else {
                        soundManager.playClip("collect.wav", false);
                        caughtMap.put(ingredient.name, caughtSoFar + 1);
                    }

                    caughtOrMissed.add(ingredient);
                } else if (ingredient.y > GAME_HEIGHT) {
                    caughtOrMissed.add(ingredient);
                }
            }

            fallingIngredients.removeAll(caughtOrMissed);
            spawnIngredient();

            boolean allCollected = true;
            for (String item : shoppingList) {
                int needed = (int) shoppingList.stream().filter(i -> i.equals(item)).count();
                int have = caughtMap.getOrDefault(item, 0);
                if (have < needed) {
                    allCollected = false;
                    break;
                }
            }

            if (allCollected) {
                won = true;
                score = timeLeft * 5; 
                mixingAnimationTimer.start(); // Start animation when game starts
                //gameTimer.stop();
            }
            repaint();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!gameStarted && e.getKeyCode() == KeyEvent.VK_ENTER) {
            gameStarted = true;
            gameTimer.start();
            countdownTimer.start();
        }

        if (gameStarted && !won && !timeUp) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                bowl.moveLeft();
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                bowl.moveRight();
            }
        }

        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
    
    public static void main(String[] args) {
        int selectedRecipeIndex = getSelectedRecipe();
        JFrame frame = new JFrame("Ingredient Workstation");
        
        IngredientGame game = new IngredientGame(selectedRecipeIndex);
        //game.setBaseScore(totalScore);
        
        frame.add(game);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    // Helper class for falling ingredient
    static class FallingIngredient {
        String name;
        int x, y;

        FallingIngredient(String name, int x, int y) {
            this.name = name;
            this.x = x;
            this.y = y;
        }
    }
}
