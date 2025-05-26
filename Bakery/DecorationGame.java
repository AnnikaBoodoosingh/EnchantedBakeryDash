import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

public class DecorationGame extends JPanel implements KeyListener, MouseMotionListener, MouseListener {
    
    private boolean gameStarted = false;
    private boolean gameFinished = false;
    private boolean isTracing = false;
    private boolean curveTracedSuccessfully = false;
    private int nearPointsCount = 0;
    private final int POINTS_THRESHOLD = 170; // how many near-points you need to succeed

    private Point[] bezierPoints;
    private GeneralPath userPath = new GeneralPath();
    
    private Pastry pastry;
    private int selectedRecipeIndex;
    static int recipe;

    private BufferedImage originalImage, grayImage;
    private int imageX = 140, imageY = 100, imageWidth = 120, imageHeight = 120;
    
    public DecorationGame(int selectedRecipeIndex) {
        this.selectedRecipeIndex = selectedRecipeIndex;
        recipe = selectedRecipeIndex;
        
        String imageName;
        switch (selectedRecipeIndex) {
            case 1: imageName = "FairyCookie.png"; break;
            case 2: imageName = "pastry.png"; break;
            case 3: imageName = "FairyCake.png"; break;
            default: imageName = "FairyCookie.png"; break;  // Fallback
        }
        pastry = new Pastry(imageName, selectedRecipeIndex);

        setPreferredSize(new Dimension(400, 500));
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);

        loadImages(imageName);
        //setupBezierCurve();
        
        if (LevelManager.currentLevel == 2){
            setupComplexCurve();
        }else{
            setupSimpleCurve();
        }
    }

    public static int getSelectedRecipe() {
        return recipe;
    }
    
    private void loadImages(String imageName) {
        originalImage = ImageManager.loadBufferedImage(imageName);
        grayImage = ImageManager.copyImage(originalImage);
        convertToGrayscale(grayImage);
    }

    private void convertToGrayscale(BufferedImage image) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = image.getRGB(x, y);

                int alpha = (pixel >> 24) & 255;
                int red = (pixel >> 16) & 255;
                int green = (pixel >> 8) & 255;
                int blue = pixel & 255;

                int gray = (red + green + blue) / 3;
                int newPixel = (alpha << 24) | (gray << 16) | (gray << 8) | gray;

                image.setRGB(x, y, newPixel);
            }
        }
    }
    /*
    private void setupBezierCurve() {
        bezierPoints = new Point[] {
            new Point(100, 400),
            new Point(150, 300),
            new Point(250, 300),
            new Point(300, 400)
        };
    }*/
    
    private void setupSimpleCurve() {
        bezierPoints = new Point[] {
            new Point(100, 400),
            new Point(150, 300),
            new Point(250, 300),
            new Point(300, 400)
        };
    }
    /*
    private void setupComplexCurve() {
        
        int offsetX = 30;
        int offsetY = -20;
        
        bezierPoints = new Point[] {
            new Point(100 + offsetX, 400 + offsetY),   // Start

            // Segment 1
            new Point(130 + offsetX, 300 + offsetY),   // Control 1
            new Point(170 + offsetX, 500 + offsetY),   // Control 2
            new Point(200 + offsetX, 400 + offsetY),   // End of Segment 1
    
            // Segment 2
            new Point(230 + offsetX, 300 + offsetY),   // Control 1
            new Point(270 + offsetX, 500 + offsetY),   // Control 2
            new Point(300 + offsetX, 400 + offsetY),   // End of Segment 2
    
            // Optional Segment 3
            new Point(330 + offsetX, 300 + offsetY),
            new Point(370 + offsetX, 500 + offsetY),
            new Point(400 + offsetX, 400 + offsetY)
        };
    }*/
    
    private void setupComplexCurve() {
        int originX = 150;
        int originY = 350;
        double scale = 1.5;
    
        bezierPoints = new Point[] {
            scalePoint(new Point(100, 400), originX, originY, scale),
    
            scalePoint(new Point(130, 300), originX, originY, scale),
            scalePoint(new Point(170, 500), originX, originY, scale),
            scalePoint(new Point(200, 400), originX, originY, scale),
    
            scalePoint(new Point(230, 300), originX, originY, scale),
            scalePoint(new Point(270, 500), originX, originY, scale),
            scalePoint(new Point(300, 400), originX, originY, scale),
    
            scalePoint(new Point(330, 300), originX, originY, scale),
            scalePoint(new Point(370, 500), originX, originY, scale),
            scalePoint(new Point(400, 400), originX, originY, scale)
        };
    }
    
    private Point scalePoint(Point p, int originX, int originY, double scale) {
        int newX = (int)(originX + scale * (p.x - originX));
        int newY = (int)(originY + scale * (p.y - originY));
        return new Point(newX, newY);
    }

    

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (!gameStarted) {
            g2.setFont(new Font("Serif", Font.BOLD, 16));
            g2.setColor(Color.BLACK);
            g2.drawString("Welcome to Decoration Station!", 90, 150);
            g2.drawString("Trace the curve to decorate the pastry!", 70, 180);
            g2.drawString("Press ENTER to begin.", 120, 210);
            return;
        }

        BufferedImage imageToDraw = curveTracedSuccessfully ? originalImage : grayImage;
        g2.drawImage(imageToDraw, imageX, imageY, imageWidth, imageHeight, null);

        // Draw curve
        g2.setColor(Color.LIGHT_GRAY);
        drawBezier(g2, bezierPoints[0], bezierPoints[1], bezierPoints[2], bezierPoints[3]);

        // Draw user's path
        g2.setColor(Color.BLUE);
        g2.draw(userPath);

        if (gameFinished) {
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            g2.setColor(Color.GREEN);
            g2.drawString("Decorated Successfully!", 100, 460);
        }
    }
    
    /*
    private void drawBezier(Graphics2D g2, Point p0, Point p1, Point p2, Point p3) {
        CubicCurve2D curve = new CubicCurve2D.Double(
            p0.x, p0.y, p1.x, p1.y,
            p2.x, p2.y, p3.x, p3.y
        );
        g2.draw(curve);
    }*/
    
    private void drawBezier(Graphics2D g2, Point... pts) {
        if (pts.length == 4) {
            // Simple curve
            CubicCurve2D simple = new CubicCurve2D.Double(
                pts[0].x, pts[0].y,
                pts[1].x, pts[1].y,
                pts[2].x, pts[2].y,
                pts[3].x, pts[3].y
            );
            g2.draw(simple);
            
        } else if (pts.length > 4) {
            for (int i = 0; i + 3 < bezierPoints.length; i += 3) {
                Point p0 = bezierPoints[i];
                Point p1 = bezierPoints[i + 1];
                Point p2 = bezierPoints[i + 2];
                Point p3 = bezierPoints[i + 3];
        
                Path2D path = new Path2D.Double();
                path.moveTo(p0.x, p0.y);
                path.curveTo(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
                g2.draw(path);
            }
        }

    }
    

    /*
    private boolean isNearCurve(Point p) {
        double t = 0;
        while (t <= 1.0) {
            double x = Math.pow(1 - t, 3) * bezierPoints[0].x +
                       3 * Math.pow(1 - t, 2) * t * bezierPoints[1].x +
                       3 * (1 - t) * Math.pow(t, 2) * bezierPoints[2].x +
                       Math.pow(t, 3) * bezierPoints[3].x;

            double y = Math.pow(1 - t, 3) * bezierPoints[0].y +
                       3 * Math.pow(1 - t, 2) * t * bezierPoints[1].y +
                       3 * (1 - t) * Math.pow(t, 2) * bezierPoints[2].y +
                       Math.pow(t, 3) * bezierPoints[3].y;

            if (p.distance(x, y) < 15) return true;
            t += 0.01;
        }
        return false;
    }*/
        
    
    private double cubicBezier(double t, double p0, double p1, double p2, double p3) {
        return Math.pow(1 - t, 3) * p0 +
               3 * Math.pow(1 - t, 2) * t * p1 +
               3 * (1 - t) * Math.pow(t, 2) * p2 +
               Math.pow(t, 3) * p3;
    }

    private boolean isNearCurve(Point p) {
        if (bezierPoints.length == 4) {
            // Single curve
            for (double t = 0; t <= 1.0; t += 0.01) {
                double x = cubicBezier(t, bezierPoints[0].x, bezierPoints[1].x, bezierPoints[2].x, bezierPoints[3].x);
                double y = cubicBezier(t, bezierPoints[0].y, bezierPoints[1].y, bezierPoints[2].y, bezierPoints[3].y);
                if (p.distance(x, y) < 10) return true;
            }
        } else if (bezierPoints.length > 4) {
            // Multiple connected segments
            for (int i = 0; i + 3 < bezierPoints.length; i += 3) {
                Point p0 = bezierPoints[i];
                Point p1 = bezierPoints[i + 1];
                Point p2 = bezierPoints[i + 2];
                Point p3 = bezierPoints[i + 3];
    
                for (double t = 0; t <= 1.0; t += 0.01) {
                    double x = cubicBezier(t, p0.x, p1.x, p2.x, p3.x);
                    double y = cubicBezier(t, p0.y, p1.y, p2.y, p3.y);
                    if (p.distance(x, y) < 10) return true;
                }
            }
        }
    
        return false;
    }


    
    public boolean isWon() {
        return curveTracedSuccessfully;  // Player wins if the curve is traced successfully
    }
    
    public int getScore() {
        if (curveTracedSuccessfully) {
            return 75;  // Full score for successful trace
        }
        return 0;  // No score if the trace wasn't completed
    }

    
    // ========== INPUT METHODS ==========

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER && !gameStarted) {
            gameStarted = true;
            repaint();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!gameStarted || gameFinished) return;
        isTracing = true;
        userPath.reset();
        userPath.moveTo(e.getX(), e.getY());
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        if (!isTracing || gameFinished) return;
    
        userPath.lineTo(e.getX(), e.getY());
    
        if (isNearCurve(e.getPoint())) {
            nearPointsCount++;
        }
    
        if (nearPointsCount >= POINTS_THRESHOLD) {
            curveTracedSuccessfully = true;
            gameFinished = true;
            isTracing = false;
        }
    
        repaint();
    }


    @Override
    public void mouseReleased(MouseEvent e) {
        isTracing = false;
        repaint();
    }

    // ========== UNUSED METHODS ==========
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void mouseMoved(MouseEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    // ========== MAIN METHOD ==========

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int selectedRecipeIndex = getSelectedRecipe();
            JFrame frame = new JFrame("Decoration Station");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            DecorationGame game = new DecorationGame(selectedRecipeIndex);
            frame.add(game);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

