import java.awt.*;


/**
 * Line class represents a straight line shape
 * This allows users to draw lines by clicking at a start point and dragging to an end point
 */
public class Line extends Shape {
    /**
     * Constructor for creating a new line
     * 
     * @param color  The color of the line
     * @param x1     X-coordinate of the starting point
     * @param y1     Y-coordinate of the starting point
     * @param x2     X-coordinate of the ending point
     * @param y2     Y-coordinate of the ending point
     * @param filled Whether the line should be filled (not used for lines)
     */
    public Line(Color color, int x1, int y1, int x2, int y2, boolean filled) {
        super(color, x1, y1, x2, y2, filled);
    }
    
    /**
     * Draws the line on the screen
     * The line is drawn from point (x1,y1) to point (x2,y2)
     */
    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(color);
        // Set the line thickness based on the strokeWidth property
        g2d.setStroke(new BasicStroke(strokeWidth));
        // Draw the line from start point to end point
        g2d.drawLine(x1, y1, x2, y2);
        // Draw selection handles if this line is selected
        drawSelectionHandles(g);
    }
} 