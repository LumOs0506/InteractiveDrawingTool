import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class Layer implements Serializable {
    private String name;
    private ArrayList<Shape> shapes;
    private boolean visible;
    private boolean selected;
    
    public Layer(String name) {
        this.name = name;
        this.shapes = new ArrayList<>();
        this.visible = true;
        this.selected = false;
    }
    
    public void addShape(Shape shape) {
        shapes.add(shape);
    }
    
    public void removeShape(Shape shape) {
        shapes.remove(shape);
    }
    
    public void draw(Graphics g) {
        if (visible) {
            for (Shape shape : shapes) {
                shape.draw(g);
            }
        }
    }
    
    public Shape getShapeAt(int x, int y) {
        for (int i = shapes.size() - 1; i >= 0; i--) {
            Shape shape = shapes.get(i);
            if (shape.containsPoint(x, y)) {
                return shape;
            }
        }
        return null;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    public boolean isSelected() {
        return selected;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public ArrayList<Shape> getShapes() {
        return shapes;
    }
} 