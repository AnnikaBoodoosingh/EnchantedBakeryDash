import java.util.HashMap;
import java.util.Map;

public class Ingredient {
    String name;
    int x, y;

    public static final String[] ALL_INGREDIENTS = {"Egg", "Butter", "Sugar", "Flour", "Milk"};
    public static final String[] BAD_INGREDIENTS = {"BadEgg", "BadButter", "BadMilk", "BadFlour", "BadSugar"};

    public Ingredient(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    // Returns a random ingredient (either good or bad)
    public static Ingredient randomIngredient(int gameWidth, int gameHeight) {
        String ingredientName;
        if (Math.random() < 0.5) {
            ingredientName = ALL_INGREDIENTS[(int) (Math.random() * ALL_INGREDIENTS.length)];
        } else {
            ingredientName = BAD_INGREDIENTS[(int) (Math.random() * BAD_INGREDIENTS.length)];
        }

        // Generate random x within the game width, ensuring it's within bounds
        int x = (int) (Math.random() * (gameWidth - 40));

        // Initialize y position at the top of the screen
        int y = 0;

        return new Ingredient(ingredientName, x, y);
    }

    // Getter methods for x and y
    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    // Check if the ingredient is caught by the bowl
    public boolean isCaught(int bowlX, int bowlY, int bowlWidth, int bowlHeight) {
        return this.y + 40 >= bowlY && this.y + 40 <= bowlY + bowlHeight &&
               this.x + 40 >= bowlX && this.x <= bowlX + bowlWidth;
    }

    // Update ingredient position
    public void fall(int fallSpeed) {
        this.y += fallSpeed;
    }
}