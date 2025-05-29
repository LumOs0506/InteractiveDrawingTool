import java.awt.*;

/**
 * Circle class represents a circle or oval shape
 * This allows users to draw circles by dragging from one corner to another
 * The shape will be an oval if width and height are different
 */
public class Circle extends Shape {
    /**
     * Constructor for creating a new circle/oval
     * 
     * @param color  The color of the circle
     * @param x1     X-coordinate of the starting point
     * @param y1     Y-coordinate of the starting point
     * @param x2     X-coordinate of the ending point
     * @param y2     Y-coordinate of the ending point
     * @param filled Whether the circle should be filled with color
     */
    public Circle(Color color, int x1, int y1, int x2, int y2, boolean filled) {
        super(color, x1, y1, x2, y2, filled);
    }
    
    /**
     * Draws the circle/oval on the screen
     * The circle is defined by a bounding box from (x1,y1) to (x2,y2)
     */
    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(color);
        
        // Calculate the top-left corner and dimensions of the bounding box
        int x = Math.min(x1, x2);
        int y = Math.min(y1, y2);
        int width = Math.abs(x2 - x1);
        int height = Math.abs(y2 - y1);
        
        // Draw either a filled or outlined circle/oval
        if (filled) {
            g2d.fillOval(x, y, width, height);
        } else {
            g2d.drawOval(x, y, width, height);
        }
        
        // Draw selection handles if this shape is selected
        drawSelectionHandles(g);
    }
} 