import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Layer class represents a single layer in the drawing
 * Each layer can contain multiple shapes and can be shown or hidden
 * Similar to layers in Photoshop or other graphics programs
 */
public class Layer implements Serializable {
    private String name;            // Name of the layer (shown in the layers panel)
    private ArrayList<Shape> shapes; // List of shapes in this layer
    private boolean visible;        // Whether the layer is visible or hidden
    private boolean selected;       // Whether the layer is currently selected
    
    /**
     * Constructor for creating a new layer
     * 
     * @param name The name of the layer
     */
    public Layer(String name) {
        this.name = name;
        this.shapes = new ArrayList<>();
        this.visible = true;
        this.selected = false;
    }
    
    /**
     * Adds a shape to this layer
     * 
     * @param shape The shape to add
     */
    public void addShape(Shape shape) {
        shapes.add(shape);
    }
    
    /**
     * Removes a shape from this layer
     * 
     * @param shape The shape to remove
     */
    public void removeShape(Shape shape) {
        shapes.remove(shape);
    }
    
    /**
     * Moves the specified shape one layer forward (in Z-axis direction)
     * This makes the shape appear on top of the shape that was previously above it
     * 
     * @param shape The shape to move forward
     */
    public void bringShapeForward(Shape shape) {
        int index = shapes.indexOf(shape);
        if (index >= 0 && index < shapes.size() - 1) {
            shapes.remove(index);
            shapes.add(index + 1, shape);
        }
    }
    
    /**
     * Moves the specified shape one layer backward (in Z-axis direction)
     * This makes the shape appear behind the shape that was previously below it
     * 
     * @param shape The shape to move backward
     */
    public void sendShapeBackward(Shape shape) {
        int index = shapes.indexOf(shape);
        if (index > 0) {
            shapes.remove(index);
            shapes.add(index - 1, shape);
        }
    }
    
    /**
     * Moves the specified shape to the very front
     * This makes the shape appear on top of all other shapes
     * 
     * @param shape The shape to bring to front
     */
    public void bringToFront(Shape shape) {
        if (shapes.contains(shape)) {
            shapes.remove(shape);
            shapes.add(shape);
        }
    }
    
    /**
     * Moves the specified shape to the very back
     * This makes the shape appear behind all other shapes
     * 
     * @param shape The shape to send to back
     */
    public void sendToBack(Shape shape) {
        if (shapes.contains(shape)) {
            shapes.remove(shape);
            shapes.add(0, shape);
        }
    }
    
    /**
     * Draws all shapes in this layer if the layer is visible
     * 
     * @param g The graphics context to draw on
     */
    public void draw(Graphics g) {
        if (visible) {
            for (Shape shape : shapes) {
                shape.draw(g);
            }
        }
    }
    
    /**
     * Finds the topmost shape at the given coordinates
     * Used for selecting shapes by clicking on them
     * 
     * @param x The x-coordinate to check
     * @param y The y-coordinate to check
     * @return The shape at the given position, or null if no shape is there
     */
    public Shape getShapeAt(int x, int y) {
        // Loop through shapes from top to bottom (last to first)
        for (int i = shapes.size() - 1; i >= 0; i--) {
            Shape shape = shapes.get(i);
            if (shape.containsPoint(x, y)) {
                return shape;
            }
        }
        return null;
    }
    
    /**
     * Sets whether this layer is visible
     * 
     * @param visible true to show the layer, false to hide it
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    /**
     * Checks if this layer is currently visible
     * 
     * @return true if the layer is visible, false if hidden
     */
    public boolean isVisible() {
        return visible;
    }
    
    /**
     * Sets whether this layer is selected
     * 
     * @param selected true to select the layer, false to deselect it
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    /**
     * Checks if this layer is currently selected
     * 
     * @return true if the layer is selected, false otherwise
     */
    public boolean isSelected() {
        return selected;
    }
    
    /**
     * Gets the name of this layer
     * 
     * @return The layer name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the name of this layer
     * 
     * @param name The new name for the layer
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Gets all shapes in this layer
     * 
     * @return ArrayList containing all shapes in the layer
     */
    public ArrayList<Shape> getShapes() {
        return shapes;
    }
} 