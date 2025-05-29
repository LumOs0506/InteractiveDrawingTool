import java.awt.*;
import java.io.Serializable;

/**
 * TextShape class represents text that can be added to the drawing
 * This allows users to add text labels, captions, or annotations to their drawings
 */
public class TextShape extends Shape {
    private String text;  // The text content to display
    private Font font;    // The font used to display the text
    
    /**
     * Constructor for creating a new text shape
     * 
     * @param text   The text content to display
     * @param color  The color of the text
     * @param x1     X-coordinate where the text will be placed
     * @param y1     Y-coordinate where the text will be placed
     * @param font   The font to use for displaying the text
     */
    public TextShape(String text, Color color, int x1, int y1, Font font) {
        super(color, x1, y1, x1, y1, false); // filled parameter not used for text
        this.text = text;
        this.font = font;
    }
    
    /**
     * Draws the text on the screen
     * The text is drawn starting at position (x1,y1)
     */
    @Override
    public void draw(Graphics g) {
        g.setColor(color);
        g.setFont(font);
        g.drawString(text, x1, y1);
    }
    
    /**
     * Changes the text content
     * 
     * @param text The new text to display
     */
    public void setText(String text) {
        this.text = text;
    }
    
    /**
     * Gets the current text content
     * 
     * @return The text being displayed
     */
    public String getText() {
        return text;
    }
    
    /**
     * Changes the font used for the text
     * 
     * @param font The new font to use
     */
    public void setFont(Font font) {
        this.font = font;
    }
} 