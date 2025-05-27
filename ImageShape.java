import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;

public class ImageShape extends Shape implements Serializable {
    private BufferedImage image;
    private int width;
    private int height;
    private static final int HANDLE_SIZE = 8;
    
    public ImageShape(BufferedImage image, int x1, int y1, int x2, int y2) {
        super(Color.BLACK, x1, y1, x2, y2, false);
        this.image = image;
        this.width = Math.abs(x2 - x1);
        this.height = Math.abs(y2 - y1);
    }
    
    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int x = Math.min(x1, x2);
        int y = Math.min(y1, y2);
        int width = Math.abs(x2 - x1);
        int height = Math.abs(y2 - y1);
        g2d.drawImage(image, x, y, width, height, null);
        drawSelectionHandles(g);
    }
    
    @Override
    public void setEndPoint(int x2, int y2) {
        super.setEndPoint(x2, y2);
        this.width = Math.abs(x2 - x1);
        this.height = Math.abs(y2 - y1);
    }
    
    @Override
    public boolean containsPoint(int x, int y) {
        int minX = Math.min(x1, x2);
        int minY = Math.min(y1, y2);
        int maxX = Math.max(x1, x2);
        int maxY = Math.max(y1, y2);
        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }
    
    @Override
    public boolean isResizeHandle(int x, int y) {
        if (!selected) return false;
        
        int left = Math.min(x1, x2);
        int top = Math.min(y1, y2);
        int right = left + width;
        int bottom = top + height;
        
        // Check each corner handle
        return (Math.abs(x - left) <= HANDLE_SIZE && Math.abs(y - top) <= HANDLE_SIZE) ||
               (Math.abs(x - right) <= HANDLE_SIZE && Math.abs(y - top) <= HANDLE_SIZE) ||
               (Math.abs(x - left) <= HANDLE_SIZE && Math.abs(y - bottom) <= HANDLE_SIZE) ||
               (Math.abs(x - right) <= HANDLE_SIZE && Math.abs(y - bottom) <= HANDLE_SIZE);
    }
    
    @Override
    public void move(int dx, int dy) {
        x1 += dx;
        y1 += dy;
        x2 += dx;
        y2 += dy;
    }
    
    @Override
    protected void drawSelectionHandles(Graphics g) {
        if (selected) {
            Graphics2D g2d = (Graphics2D) g;
            // Draw selection rectangle
            g2d.setColor(Color.BLUE);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(x1, y1, width, height);
            
            // Draw resize handles
            g2d.setColor(Color.WHITE);
            g2d.fillRect(x1 - HANDLE_SIZE/2, y1 - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
            g2d.fillRect(x2 - HANDLE_SIZE/2, y1 - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
            g2d.fillRect(x1 - HANDLE_SIZE/2, y2 - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
            g2d.fillRect(x2 - HANDLE_SIZE/2, y2 - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
            
            g2d.setColor(Color.BLUE);
            g2d.drawRect(x1 - HANDLE_SIZE/2, y1 - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
            g2d.drawRect(x2 - HANDLE_SIZE/2, y1 - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
            g2d.drawRect(x1 - HANDLE_SIZE/2, y2 - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
            g2d.drawRect(x2 - HANDLE_SIZE/2, y2 - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
        }
    }
} 