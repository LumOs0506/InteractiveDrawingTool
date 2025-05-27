import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;

public class DrawingPanel extends JPanel {
    private ArrayList<Layer> layers;
    private Layer currentLayer;
    private Stack<ArrayList<Layer>> undoStack;
    private Stack<ArrayList<Layer>> redoStack;
    private Color currentColor;
    private String currentShape;
    private Shape currentDrawing;
    private int startX, startY;
    private boolean filled;
    private BufferedImage currentImage;
    private String currentText;
    private Font currentFont;
    private Shape selectedShape;
    private boolean isResizing;
    private boolean isMoving;
    private int lastX, lastY;
    private boolean isSelecting = false;
    private boolean isDrawing = false;
    private boolean isFirstClick = true;
    
    public DrawingPanel() {
        layers = new ArrayList<>();
        undoStack = new Stack<>();
        redoStack = new Stack<>();
        currentColor = Color.BLACK;
        currentShape = "Line";
        filled = false;
        currentImage = null;
        currentText = "";
        currentFont = new Font("Arial", Font.PLAIN, 12);
        selectedShape = null;
        isResizing = false;
        isMoving = false;
        isSelecting = false;
        isDrawing = false;
        isFirstClick = true;
        
        setBackground(Color.WHITE);
        setFocusable(true);
        requestFocusInWindow();
        
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
                startX = e.getX();
                startY = e.getY();
                lastX = startX;
                lastY = startY;
                
                if (currentShape.equals("Select")) {
                    // Selection mode
                    boolean clickedOnShape = false;
                    for (int i = layers.size() - 1; i >= 0; i--) {
                        Layer layer = layers.get(i);
                        if (layer.isVisible()) {
                            Shape shape = layer.getShapeAt(startX, startY);
                            if (shape != null) {
                                clickedOnShape = true;
                                // Deselect previously selected shape
                                if (selectedShape != null) {
                                    selectedShape.setSelected(false);
                                }
                                
                                selectedShape = shape;
                                selectedShape.setSelected(true);
                                currentLayer = layer;
                                
                                // Check if clicking on resize handle
                                if (shape.isResizeHandle(startX, startY)) {
                                    isResizing = true;
                                } else {
                                    isMoving = true;
                                }
                                
                                repaint();
                                break;
                            }
                        }
                    }
                    
                    // If not clicking on a shape, deselect current shape
                    if (!clickedOnShape && selectedShape != null) {
                        selectedShape.setSelected(false);
                        selectedShape = null;
                        repaint();
                    }
                } else {
                    // Drawing mode
                    if (selectedShape != null) {
                        selectedShape.setSelected(false);
                        selectedShape = null;
                    }
                    
                    if (currentLayer != null) {
                        isDrawing = true;
                        switch (currentShape) {
                            case "Circle":
                                currentDrawing = new Circle(currentColor, startX, startY, startX, startY, filled);
                                break;
                            case "Rectangle":
                                currentDrawing = new Rectangle(currentColor, startX, startY, startX, startY, filled);
                                break;
                            case "Image":
                                if (currentImage != null) {
                                    currentDrawing = new ImageShape(currentImage, startX, startY, startX, startY);
                                }
                                break;
                            case "Text":
                                if (!currentText.isEmpty()) {
                                    currentDrawing = new TextShape(currentText, currentColor, startX, startY, currentFont);
                                    currentLayer.addShape(currentDrawing);
                                    saveState();
                                    currentDrawing = null;
                                    repaint();
                                } else {
                                    // If no text is entered, show a dialog to input text
                                    String text = JOptionPane.showInputDialog(this, "Enter text:");
                                    if (text != null && !text.isEmpty()) {
                                        currentText = text;
                                        currentDrawing = new TextShape(currentText, currentColor, startX, startY, currentFont);
                                        currentLayer.addShape(currentDrawing);
                                        saveState();
                                        currentDrawing = null;
                                        repaint();
                                    }
                                }
                                break;
                            default:
                                currentDrawing = new Line(currentColor, startX, startY, startX, startY, filled);
                        }
                    }
                }
            }
            
            public void mouseReleased(MouseEvent e) {
                if (isDrawing && currentDrawing != null && !currentShape.equals("Text") && currentLayer != null) {
                    currentLayer.addShape(currentDrawing);
                    saveState();
                    currentDrawing = null;
                }
                isResizing = false;
                isMoving = false;
                isDrawing = false;
                repaint();
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                requestFocusInWindow();
                int x = e.getX();
                int y = e.getY();
                
                if (currentShape.equals("Select") && selectedShape != null) {
                    if (isResizing) {
                        selectedShape.setEndPoint(x, y);
                    } else if (isMoving) {
                        selectedShape.move(x - lastX, y - lastY);
                    }
                    lastX = x;
                    lastY = y;
                    repaint();
                } else if (isDrawing && currentDrawing != null && !currentShape.equals("Text")) {
                    currentDrawing.setEndPoint(x, y);
                    repaint();
                }
            }
        });
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if ((e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE) && selectedShape != null && currentLayer != null) {
                    currentLayer.removeShape(selectedShape);
                    saveState();
                    selectedShape = null;
                    repaint();
                }
            }
        });
    }
    
    public void setLayers(ArrayList<Layer> layers) {
        this.layers = layers;
        if (!layers.isEmpty() && currentLayer == null) {
            currentLayer = layers.get(0);
        }
    }
    
    public void setCurrentLayer(Layer layer) {
        this.currentLayer = layer;
    }
    
    private void saveState() {
        ArrayList<Layer> state = new ArrayList<>();
        for (Layer layer : layers) {
            Layer newLayer = new Layer(layer.getName());
            newLayer.setVisible(layer.isVisible());
            for (Shape shape : layer.getShapes()) {
                newLayer.addShape(shape);
            }
            state.add(newLayer);
        }
        undoStack.push(state);
        redoStack.clear();
    }
    
    public void undo() {
        if (!undoStack.isEmpty()) {
            ArrayList<Layer> currentState = new ArrayList<>(layers);
            redoStack.push(currentState);
            layers = undoStack.pop();
            repaint();
        }
    }
    
    public void redo() {
        if (!redoStack.isEmpty()) {
            ArrayList<Layer> currentState = new ArrayList<>(layers);
            undoStack.push(currentState);
            layers = redoStack.pop();
            repaint();
        }
    }
    
    public void reset() {
        layers.clear();
        undoStack.clear();
        redoStack.clear();
        currentDrawing = null;
        if (selectedShape != null) {
            selectedShape.setSelected(false);
            selectedShape = null;
        }
        
        // Create new initial layer
        Layer initialLayer = new Layer("Layer 1");
        layers.add(initialLayer);
        currentLayer = initialLayer;
        
        repaint();
    }
    
    public void saveDrawing(File file) {
        try {
            BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            paint(g2d);
            g2d.dispose();
            ImageIO.write(image, "PNG", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public boolean isFilled() {
        return filled;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Draw all visible layers
        for (Layer layer : layers) {
            layer.draw(g2d);
        }
        
        // Draw current shape being drawn
        if (currentDrawing != null) {
            currentDrawing.draw(g2d);
        }
    }
    
    public void setColor(Color color) {
        currentColor = color;
    }
    
    public void setShape(String shape) {
        currentShape = shape;
    }
    
    public void setFilled(boolean filled) {
        this.filled = filled;
    }
    
    public void setCurrentImage(BufferedImage image) {
        this.currentImage = image;
        if (image != null) {
            currentShape = "Image";
        }
    }
    
    public void setCurrentText(String text) {
        this.currentText = text;
        if (!text.isEmpty()) {
            currentShape = "Text";
        }
    }
    
    public void setCurrentFont(Font font) {
        this.currentFont = font;
    }
    
    public Font getCurrentFont() {
        return currentFont;
    }
    
    public Color getCurrentColor() {
        return currentColor;
    }
} 