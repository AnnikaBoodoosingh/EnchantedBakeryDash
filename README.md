# EnchantedBakeryDash
COMP 3609 - Game Programming Project

# Enchanted Bakery Dash

## Game Concept
In **Enchanted Bakery Dash**, players step into the flour-dusted shoes of a young magical baker-in-training who has just inherited their grandmother’s mystical bakery, nestled deep within a dreamy, pastel-lit forest. Fairy customers arrive in waves, twinkling with anticipation but each has very limited patience. Wait too long and they will poof, vanishing and leaving behind a grumpy little message.

Players race between stations, catching falling enchanted ingredients with a floating mixing bowl. One spoiled egg or pinch of cursed sugar can ruin the entire recipe. Each dish must follow ancient, animated instructions, and the magical oven hums with personality. Timing is everything. Bake too early and your treat is raw. Wait too long and it turns into a tragic blackened brick.

Once the treat is perfectly baked, the real artistry begins. Players trace glowing curves and swirls to decorate pastries into dazzling creations worthy of ice fairies or forest sprites. Each successful bake earns shimmering coins.

**Enchanted Bakery Dash** is a cozy fantasy adventure full of whimsical charm and fast-paced magic. It is a world where baking is a spell, timing is your wand, and happiness is served warm and frosted.

---

## How to Play the Game

- Use the **left** and **right arrow keys** on your keyboard to move across the bakery.
- After selecting **Start Game**, wait 5 seconds for the first customer to appear.
- Customers give orders and have a patience bar that decreases over time.
- Move left and right to visit stations:

  1. **Ingredient Station:**  
     - Click to start.  
     - Catch the correct ingredients using arrow keys, avoid bad ingredients.  
     - Press **Enter** to start the mini-game.

  2. **Oven Station:**  
     - Click to open the oven.  
     - Press **Enter** to start baking.  
     - Press **Spacebar** when the oven status says “Baked Perfectly” to bake correctly.

  3. **Decoration Station:**  
     - Click to decorate.  
     - Press **Enter** to begin.  
     - Use your mouse to trace the glowing curves on the pastry.

- Serve the pastry to the customer once decoration is complete.
- Earn money and points based on your performance and the customer's patience.
- Collect **$100** in Level 1 to unlock Level 2. Otherwise, the game ends.

---

## Scoring System

| Station            | Success Criteria                    | Points / Rewards            |
|--------------------|-----------------------------------|----------------------------|
| Ingredient Station  | Collect all correct ingredients    | Score increases by Time Left × 5 |
| Oven Station       | Perfect bake (right timing)         | +75 points                 |
| Oven Station       | Raw or burnt pastry (wrong timing) | 0 points                   |
| Decoration Station | Correct tracing of the curve        | +75 points                 |
| Serving Customer  | Successfully serving the pastry     | Earn $50 per customer      |
| Level Progression  | Earn $100 in Level 1                | Unlock Level 2             |

---

## How to Run `GameApplication.java` in VS Code

To run the game, run the `GameApplication.java` file
