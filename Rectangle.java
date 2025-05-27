import java.awt.*;
import java.io.Serializable;

public class Rectangle extends Shape implements Serializable {
    public Rectangle(Color color, int x1, int y1, int x2, int y2, boolean filled) {
        super(color, x1, y1, x2, y2, filled);
    }
    
    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(color);
        
        int x = Math.min(x1, x2);
        int y = Math.min(y1, y2);
        int width = Math.abs(x2 - x1);
        int height = Math.abs(y2 - y1);
        
        if (filled) {
            g2d.fillRect(x, y, width, height);
        } else {
            g2d.drawRect(x, y, width, height);
        }
        
        drawSelectionHandles(g);
    }
} 