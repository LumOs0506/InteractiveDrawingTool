import java.awt.*;
import java.io.Serializable;

/**
 * Rectangle class represents a rectangular shape
 * This allows users to draw rectangles by dragging from one corner to another
 * Creates a square if width and height are equal
 */
public class Rectangle extends Shape implements Serializable {
    /**
     * Constructor for creating a new rectangle
     * 
     * @param color  The color of the rectangle
     * @param x1     X-coordinate of the starting corner
     * @param y1     Y-coordinate of the starting corner
     * @param x2     X-coordinate of the opposite corner
     * @param y2     Y-coordinate of the opposite corner
     * @param filled Whether the rectangle should be filled with color
     */
    public Rectangle(Color color, int x1, int y1, int x2, int y2, boolean filled) {
        super(color, x1, y1, x2, y2, filled);
    }
    
    /**
     * Draws the rectangle on the screen
     * The rectangle is defined by its top-left corner and dimensions
     */
    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(color);
        
        // Calculate the top-left corner and dimensions of the rectangle
        int x = Math.min(x1, x2);
        int y = Math.min(y1, y2);
        int width = Math.abs(x2 - x1);
        int height = Math.abs(y2 - y1);
        
        // Draw either a filled or outlined rectangle
        if (filled) {
            g2d.fillRect(x, y, width, height);
        } else {
            g2d.drawRect(x, y, width, height);
        }
        
        // Draw selection handles if this shape is selected
        drawSelectionHandles(g);
    }
} 