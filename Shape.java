import java.awt.*;
import java.io.Serializable;

/**
 * Abstract Shape class that serves as the base for all drawable shapes
 * All specific shapes (Rectangle, Circle, Line, etc.) inherit from this class
 * Provides common functionality like selection, moving, and resizing
 */
public abstract class Shape implements Serializable {
    // Basic properties all shapes have
    protected Color color;          // The color of the shape
    protected int x1, y1, x2, y2;   // Coordinates defining the shape (start and end points)
    protected boolean filled;       // Whether the shape should be filled with color
    protected boolean selected;     // Whether the shape is currently selected
    protected static final int HANDLE_SIZE = 8;  // Size of selection handles for resizing
    protected float strokeWidth = 1.0f;  // Width of the shape's outline
    
    /**
     * Constructor for creating a new shape
     * 
     * @param color  The color of the shape
     * @param x1     Starting x-coordinate
     * @param y1     Starting y-coordinate
     * @param x2     Ending x-coordinate
     * @param y2     Ending y-coordinate
     * @param filled Whether the shape should be filled with color
     */
    public Shape(Color color, int x1, int y1, int x2, int y2, boolean filled) {
        this.color = color;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.filled = filled;
        this.selected = false;
    }
    
    /**
     * Abstract method that each shape must implement to draw itself
     * Different shapes (circle, rectangle, etc.) will draw differently
     */
    public abstract void draw(Graphics g);
    
    /**
     * Updates the end point of the shape (used while drawing or resizing)
     */
    public void setEndPoint(int x2, int y2) {
        this.x2 = x2;
        this.y2 = y2;
    }
    
    /**
     * Sets whether the shape should be filled with color
     */
    public void setFilled(boolean filled) {
        this.filled = filled;
    }
    
    /**
     * Checks if the shape is filled with color
     */
    public boolean isFilled() {
        return filled;
    }
    
    /**
     * Sets whether the shape is selected
     * Selected shapes show resize handles and can be modified
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    /**
     * Checks if the shape is currently selected
     */
    public boolean isSelected() {
        return selected;
    }
    
    /**
     * Moves the shape by the specified amount
     * 
     * @param dx Amount to move horizontally (positive = right, negative = left)
     * @param dy Amount to move vertically (positive = down, negative = up)
     */
    public void move(int dx, int dy) {
        x1 += dx;
        y1 += dy;
        x2 += dx;
        y2 += dy;
    }
    
    /**
     * Sets the width of the shape's outline
     */
    public void setStrokeWidth(float width) {
        this.strokeWidth = width;
    }
    
    /**
     * Gets the width of the shape's outline
     */
    public float getStrokeWidth() {
        return strokeWidth;
    }
    
    /**
     * Checks if the given point is inside the shape
     * Used for selecting shapes by clicking on them
     * 
     * @param x X-coordinate to check
     * @param y Y-coordinate to check
     * @return true if the point is inside the shape, false otherwise
     */
    public boolean containsPoint(int x, int y) {
        // Calculate the bounding box of the shape
        int left = Math.min(x1, x2);
        int right = Math.max(x1, x2);
        int top = Math.min(y1, y2);
        int bottom = Math.max(y1, y2);
        
        // Check if the point is inside the bounding box
        return x >= left && x <= right && y >= top && y <= bottom;
    }
    
    /**
     * Checks if the given point is on one of the resize handles
     * Used to determine if the user is trying to resize the shape
     * 
     * @param x X-coordinate to check
     * @param y Y-coordinate to check
     * @return true if the point is on a resize handle, false otherwise
     */
    public boolean isResizeHandle(int x, int y) {
        // Only selected shapes have resize handles
        if (!selected) return false;
        
        // Calculate the bounding box of the shape
        int left = Math.min(x1, x2);
        int top = Math.min(y1, y2);
        int right = Math.max(x1, x2);
        int bottom = Math.max(y1, y2);
        
        // Check if the point is near any of the four corner handles
        return (Math.abs(x - left) <= HANDLE_SIZE && Math.abs(y - top) <= HANDLE_SIZE) ||
               (Math.abs(x - right) <= HANDLE_SIZE && Math.abs(y - top) <= HANDLE_SIZE) ||
               (Math.abs(x - left) <= HANDLE_SIZE && Math.abs(y - bottom) <= HANDLE_SIZE) ||
               (Math.abs(x - right) <= HANDLE_SIZE && Math.abs(y - bottom) <= HANDLE_SIZE);
    }
    
    /**
     * Draws the selection handles and outline around a selected shape
     * This shows the user which shape is currently selected
     */
    protected void drawSelectionHandles(Graphics g) {
        // Only draw handles if the shape is selected
        if (!selected) return;
        
        Graphics2D g2d = (Graphics2D) g;
        int left = Math.min(x1, x2);
        int top = Math.min(y1, y2);
        int right = Math.max(x1, x2);
        int bottom = Math.max(y1, y2);
        
        // Draw a blue rectangle around the selected shape
        g2d.setColor(Color.BLUE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(left, top, right - left, bottom - top);
        
        // Draw white squares at each corner for resizing
        g2d.setColor(Color.WHITE);
        g2d.fillRect(left - HANDLE_SIZE/2, top - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
        g2d.fillRect(right - HANDLE_SIZE/2, top - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
        g2d.fillRect(left - HANDLE_SIZE/2, bottom - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
        g2d.fillRect(right - HANDLE_SIZE/2, bottom - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
        
        // Draw blue outlines around the white handles
        g2d.setColor(Color.BLUE);
        g2d.drawRect(left - HANDLE_SIZE/2, top - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
        g2d.drawRect(right - HANDLE_SIZE/2, top - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
        g2d.drawRect(left - HANDLE_SIZE/2, bottom - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
        g2d.drawRect(right - HANDLE_SIZE/2, bottom - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
    }
} 