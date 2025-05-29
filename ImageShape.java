import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * ImageShape class represents an image that can be added to the drawing
 * This allows users to insert photos or other images into their drawings
 * Images can be resized and moved like other shapes
 */
public class ImageShape extends Shape implements Serializable {
    private BufferedImage image;  // The actual image data
    private int width;            // Width of the displayed image
    private int height;           // Height of the displayed image
    private static final int HANDLE_SIZE = 8;  // Size of resize handles
    
    /**
     * Constructor for creating a new image shape
     * 
     * @param image The image to display
     * @param x1    X-coordinate of the top-left corner
     * @param y1    Y-coordinate of the top-left corner
     * @param x2    X-coordinate of the bottom-right corner
     * @param y2    Y-coordinate of the bottom-right corner
     */
    public ImageShape(BufferedImage image, int x1, int y1, int x2, int y2) {
        super(Color.BLACK, x1, y1, x2, y2, false);
        this.image = image;
        this.width = Math.abs(x2 - x1);
        this.height = Math.abs(y2 - y1);
    }
    
    /**
     * Draws the image on the screen
     * The image is scaled to fit within the specified width and height
     */
    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        // Calculate the top-left corner and dimensions
        int x = Math.min(x1, x2);
        int y = Math.min(y1, y2);
        int width = Math.abs(x2 - x1);
        int height = Math.abs(y2 - y1);
        // Draw the image scaled to fit the specified dimensions
        g2d.drawImage(image, x, y, width, height, null);
        // Draw selection handles if this image is selected
        drawSelectionHandles(g);
    }
    
    /**
     * Updates the end point of the image (used when resizing)
     * Also updates the width and height properties
     */
    @Override
    public void setEndPoint(int x2, int y2) {
        super.setEndPoint(x2, y2);
        this.width = Math.abs(x2 - x1);
        this.height = Math.abs(y2 - y1);
    }
    
    /**
     * Checks if a point is inside the image
     * Used for selecting the image by clicking on it
     */
    @Override
    public boolean containsPoint(int x, int y) {
        int minX = Math.min(x1, x2);
        int minY = Math.min(y1, y2);
        int maxX = Math.max(x1, x2);
        int maxY = Math.max(y1, y2);
        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }
    
    /**
     * Checks if a point is on one of the resize handles
     * Used to determine if the user is trying to resize the image
     */
    @Override
    public boolean isResizeHandle(int x, int y) {
        if (!selected) return false;
        
        int left = Math.min(x1, x2);
        int top = Math.min(y1, y2);
        int right = left + width;
        int bottom = top + height;
        
        // Check if the point is near any of the four corner handles
        return (Math.abs(x - left) <= HANDLE_SIZE && Math.abs(y - top) <= HANDLE_SIZE) ||
               (Math.abs(x - right) <= HANDLE_SIZE && Math.abs(y - top) <= HANDLE_SIZE) ||
               (Math.abs(x - left) <= HANDLE_SIZE && Math.abs(y - bottom) <= HANDLE_SIZE) ||
               (Math.abs(x - right) <= HANDLE_SIZE && Math.abs(y - bottom) <= HANDLE_SIZE);
    }
    
    /**
     * Moves the image by the specified amount
     * 
     * @param dx Amount to move horizontally
     * @param dy Amount to move vertically
     */
    @Override
    public void move(int dx, int dy) {
        x1 += dx;
        y1 += dy;
        x2 += dx;
        y2 += dy;
    }
    
    /**
     * Draws the selection handles and outline around a selected image
     * This shows the user that the image is currently selected
     */
    @Override
    protected void drawSelectionHandles(Graphics g) {
        if (selected) {
            Graphics2D g2d = (Graphics2D) g;
            // Draw a blue rectangle around the selected image
            g2d.setColor(Color.BLUE);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(x1, y1, width, height);
            
            // Draw white squares at each corner for resizing
            g2d.setColor(Color.WHITE);
            g2d.fillRect(x1 - HANDLE_SIZE/2, y1 - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
            g2d.fillRect(x2 - HANDLE_SIZE/2, y1 - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
            g2d.fillRect(x1 - HANDLE_SIZE/2, y2 - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
            g2d.fillRect(x2 - HANDLE_SIZE/2, y2 - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
            
            // Draw blue outlines around the white handles
            g2d.setColor(Color.BLUE);
            g2d.drawRect(x1 - HANDLE_SIZE/2, y1 - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
            g2d.drawRect(x2 - HANDLE_SIZE/2, y1 - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
            g2d.drawRect(x1 - HANDLE_SIZE/2, y2 - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
            g2d.drawRect(x2 - HANDLE_SIZE/2, y2 - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
        }
    }
} 