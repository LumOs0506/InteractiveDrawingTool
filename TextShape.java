import java.awt.*;
import java.io.Serializable;

public class TextShape extends Shape {
    private String text;
    private Font font;
    
    public TextShape(String text, Color color, int x1, int y1, Font font) {
        super(color, x1, y1, x1, y1, false); // filled parameter not used for text
        this.text = text;
        this.font = font;
    }
    
    @Override
    public void draw(Graphics g) {
        g.setColor(color);
        g.setFont(font);
        g.drawString(text, x1, y1);
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public String getText() {
        return text;
    }
    
    public void setFont(Font font) {
        this.font = font;
    }
} 