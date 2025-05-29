import java.awt.*;
import java.io.Serializable;

public abstract class Shape implements Serializable {
    protected Color color;
    protected int x1, y1, x2, y2;
    protected boolean filled;
    protected boolean selected;
    protected static final int HANDLE_SIZE = 8;
    protected float strokeWidth = 1.0f;
    
    public Shape(Color color, int x1, int y1, int x2, int y2, boolean filled) {
        this.color = color;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.filled = filled;
        this.selected = false;
    }
    
    public abstract void draw(Graphics g);
    
    public void setEndPoint(int x2, int y2) {
        this.x2 = x2;
        this.y2 = y2;
    }
    
    public void setFilled(boolean filled) {
        this.filled = filled;
    }
    
    public boolean isFilled() {
        return filled;
    }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    public boolean isSelected() {
        return selected;
    }
    
    public void move(int dx, int dy) {
        x1 += dx;
        y1 += dy;
        x2 += dx;
        y2 += dy;
    }
    
    public void setStrokeWidth(float width) {
        this.strokeWidth = width;
    }
    
    public float getStrokeWidth() {
        return strokeWidth;
    }
    
    public boolean containsPoint(int x, int y) {
        int left = Math.min(x1, x2);
        int right = Math.max(x1, x2);
        int top = Math.min(y1, y2);
        int bottom = Math.max(y1, y2);
        return x >= left && x <= right && y >= top && y <= bottom;
    }
    
    public boolean isResizeHandle(int x, int y) {
        if (!selected) return false;
        
        int left = Math.min(x1, x2);
        int top = Math.min(y1, y2);
        int right = Math.max(x1, x2);
        int bottom = Math.max(y1, y2);
        
        // Check each corner handle
        return (Math.abs(x - left) <= HANDLE_SIZE && Math.abs(y - top) <= HANDLE_SIZE) ||
               (Math.abs(x - right) <= HANDLE_SIZE && Math.abs(y - top) <= HANDLE_SIZE) ||
               (Math.abs(x - left) <= HANDLE_SIZE && Math.abs(y - bottom) <= HANDLE_SIZE) ||
               (Math.abs(x - right) <= HANDLE_SIZE && Math.abs(y - bottom) <= HANDLE_SIZE);
    }
    
    protected void drawSelectionHandles(Graphics g) {
        if (!selected) return;
        
        Graphics2D g2d = (Graphics2D) g;
        int left = Math.min(x1, x2);
        int top = Math.min(y1, y2);
        int right = Math.max(x1, x2);
        int bottom = Math.max(y1, y2);
        
        // Draw selection rectangle
        g2d.setColor(Color.BLUE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(left, top, right - left, bottom - top);
        
        // Draw resize handles
        g2d.setColor(Color.WHITE);
        g2d.fillRect(left - HANDLE_SIZE/2, top - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
        g2d.fillRect(right - HANDLE_SIZE/2, top - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
        g2d.fillRect(left - HANDLE_SIZE/2, bottom - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
        g2d.fillRect(right - HANDLE_SIZE/2, bottom - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
        
        g2d.setColor(Color.BLUE);
        g2d.drawRect(left - HANDLE_SIZE/2, top - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
        g2d.drawRect(right - HANDLE_SIZE/2, top - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
        g2d.drawRect(left - HANDLE_SIZE/2, bottom - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
        g2d.drawRect(right - HANDLE_SIZE/2, bottom - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
    }
} 