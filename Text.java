import java.awt.*;
import java.io.Serializable;

public class Text extends Shape implements Serializable {
    private String text;
    private Font font;
    
    public Text(Color color, int x1, int y1, int x2, int y2, String text, Font font) {
        super(color, x1, y1, x2, y2, false);
        this.text = text;
        this.font = font;
    }
    
    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(color);
        g2d.setFont(font);
        g2d.drawString(text, x1, y1);
        drawSelectionHandles(g);
    }
    
    @Override
    public boolean containsPoint(int x, int y) {
        FontMetrics fm = new FontMetrics(font) {};
        int width = fm.stringWidth(text);
        int height = fm.getHeight();
        return x >= x1 && x <= x1 + width && y >= y1 - height && y <= y1;
    }
} 