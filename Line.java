import java.awt.*;
import java.io.Serializable;

public class Line extends Shape implements Serializable {
    public Line(Color color, int x1, int y1, int x2, int y2, boolean filled) {
        super(color, x1, y1, x2, y2, filled);
    }
    
    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(strokeWidth));
        g2d.drawLine(x1, y1, x2, y2);
        drawSelectionHandles(g);
    }
} 