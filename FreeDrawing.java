import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * FreeDrawing class represents a freehand drawing tool
 * This allows users to draw smooth curves by tracking mouse movement
 * Similar to the brush or pencil tool in graphics programs
 */
public class FreeDrawing extends Shape implements Serializable {
    private ArrayList<Point> points;  // List of points that make up the drawing
    
    /**
     * Constructor for creating a new free drawing
     * 
     * @param color  The color of the drawing
     * @param x1     Starting x-coordinate
     * @param y1     Starting y-coordinate
     * @param x2     Current x-coordinate
     * @param y2     Current y-coordinate
     * @param filled Whether the shape should be filled (not used for free drawing)
     */
    public FreeDrawing(Color color, int x1, int y1, int x2, int y2, boolean filled) {
        super(color, x1, y1, x2, y2, filled);
        points = new ArrayList<>();
        points.add(new Point(x1, y1));  // Add the starting point
        points.add(new Point(x2, y2));  // Add the current point
    }
    
    /**
     * Adds a new point to the free drawing path
     * Called as the mouse is dragged to create a smooth curve
     * 
     * @param x The x-coordinate of the new point
     * @param y The y-coordinate of the new point
     */
    public void addPoint(int x, int y) {
        points.add(new Point(x, y));
        // Update the bounding box
        x1 = Math.min(x1, x);
        y1 = Math.min(y1, y);
        x2 = Math.max(x2, x);
        y2 = Math.max(y2, y);
    }
    
    /**
     * Updates the end point of the drawing as the mouse is dragged
     * Overrides the parent class method to add points to the path
     */
    @Override
    public void setEndPoint(int x, int y) {
        super.setEndPoint(x, y);
        addPoint(x, y);
    }
    
    /**
     * Draws the free drawing path
     * Connects all points with lines to create a smooth curve
     */
    @Override
    public void draw(Graphics g) {
        if (points.size() < 2) return;  // Need at least 2 points to draw a line
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(color);
        // Use round caps and joins for smooth lines
        g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        // Draw lines between consecutive points
        for (int i = 0; i < points.size() - 1; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);
            g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
        
        // Draw selection handles if selected
        drawSelectionHandles(g);
    }
    
    /**
     * Moves the entire free drawing path
     * Updates all points in the path by the specified amount
     */
    @Override
    public void move(int dx, int dy) {
        super.move(dx, dy);  // Move the bounding box
        // Move each point in the path
        for (Point p : points) {
            p.x += dx;
            p.y += dy;
        }
    }
    
    /**
     * Checks if a point is on or near the free drawing path
     * Used for selecting the drawing by clicking on it
     */
    @Override
    public boolean containsPoint(int x, int y) {
        // For free drawing, check if point is near any segment of the path
        for (int i = 0; i < points.size() - 1; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);
            
            // Calculate distance from point to line segment
            double distance = pointToLineDistance(x, y, p1.x, p1.y, p2.x, p2.y);
            if (distance < 5) {  // 5 pixel tolerance for selection
                return true;
            }
        }
        return false;
    }
    
    /**
     * Calculates the shortest distance from a point to a line segment
     * Used to determine if a click is near enough to select the drawing
     * 
     * @param x  X-coordinate of the point to check
     * @param y  Y-coordinate of the point to check
     * @param x1 X-coordinate of the line segment's start
     * @param y1 Y-coordinate of the line segment's start
     * @param x2 X-coordinate of the line segment's end
     * @param y2 Y-coordinate of the line segment's end
     * @return The shortest distance from the point to the line segment
     */
    private double pointToLineDistance(int x, int y, int x1, int y1, int x2, int y2) {
        // Calculate the length of the line segment
        double normalLength = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        if (normalLength == 0) return Math.sqrt((x - x1) * (x - x1) + (y - y1) * (y - y1));
        
        // Calculate projection of point onto line
        double t = ((x - x1) * (x2 - x1) + (y - y1) * (y2 - y1)) / (normalLength * normalLength);
        
        // If projection is outside the line segment, return distance to nearest endpoint
        if (t < 0) return Math.sqrt((x - x1) * (x - x1) + (y - y1) * (y - y1));
        if (t > 1) return Math.sqrt((x - x2) * (x - x2) + (y - y2) * (y - y2));
        
        // Calculate the projected point on the line
        double projX = x1 + t * (x2 - x1);
        double projY = y1 + t * (y2 - y1);
        
        // Return distance from point to projection
        return Math.sqrt((x - projX) * (x - projX) + (y - projY) * (y - projY));
    }
} 