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
    private float currentStrokeWidth = 1.0f;
    private boolean selectMode = false;
    
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
                
                if (selectMode || e.isControlDown()) {
                    for (int i = layers.size() - 1; i >= 0; i--) {
                        Layer layer = layers.get(i);
                        if (layer.isVisible()) {
                            Shape shape = layer.getShapeAt(startX, startY);
                            if (shape != null) {
                                if (selectedShape != null) {
                                    selectedShape.setSelected(false);
                                }
                                
                                selectedShape = shape;
                                selectedShape.setSelected(true);
                                currentLayer = layer;
                                
                                if (shape.isResizeHandle(startX, startY)) {
                                    isResizing = true;
                                } else {
                                    isMoving = true;
                                }
                                
                                repaint();
                                return;
                            }
                        }
                    }
                    
                    if (selectedShape != null) {
                        selectedShape.setSelected(false);
                        selectedShape = null;
                        repaint();
                    }
                    
                    if (selectMode) {
                        return;
                    }
                }
                
                if (currentLayer != null && !selectMode) {
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
                        case "Free":
                            currentDrawing = new FreeDrawing(currentColor, startX, startY, startX, startY, filled);
                            currentDrawing.setStrokeWidth(currentStrokeWidth);
                            break;
                        default:
                            currentDrawing = new Line(currentColor, startX, startY, startX, startY, filled);
                            currentDrawing.setStrokeWidth(currentStrokeWidth);
                    }
                    
                    if (currentDrawing != null) {
                        currentDrawing.setStrokeWidth(currentStrokeWidth);
                    }
                }
            }
            
            public void mouseReleased(MouseEvent e) {
                if (currentDrawing != null && !currentShape.equals("Text") && currentLayer != null) {
                    currentLayer.addShape(currentDrawing);
                    saveState();
                    currentDrawing = null;
                    repaint();
                }
                isResizing = false;
                isMoving = false;
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                requestFocusInWindow();
                int x = e.getX();
                int y = e.getY();
                
                if (selectedShape != null) {
                    if (isResizing) {
                        selectedShape.setEndPoint(x, y);
                    } else if (isMoving) {
                        selectedShape.move(x - lastX, y - lastY);
                    }
                    lastX = x;
                    lastY = y;
                    repaint();
                } else if (currentDrawing != null && !currentShape.equals("Text")) {
                    currentDrawing.setEndPoint(x, y);
                    repaint();
                }
            }
        });
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if ((e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE) && selectedShape != null && currentLayer != null) {
                    deleteSelectedShape();
                }
            }
        });
    }
    
    public void setSelectMode(boolean selectMode) {
        this.selectMode = selectMode;
        if (!selectMode && selectedShape != null) {
            selectedShape.setSelected(false);
            selectedShape = null;
            repaint();
        }
    }
    
    public void deleteSelectedShape() {
        if (selectedShape != null && currentLayer != null) {
            currentLayer.removeShape(selectedShape);
            saveState();
            selectedShape = null;
            repaint();
        }
    }
    
    public void bringSelectedShapeForward() {
        if (selectedShape != null && currentLayer != null) {
            currentLayer.bringShapeForward(selectedShape);
            saveState();
            repaint();
        }
    }
    
    public void sendSelectedShapeBackward() {
        if (selectedShape != null && currentLayer != null) {
            currentLayer.sendShapeBackward(selectedShape);
            saveState();
            repaint();
        }
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
        
        for (Layer layer : layers) {
            layer.draw(g2d);
        }
        
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
    
    public void setStrokeWidth(float width) {
        this.currentStrokeWidth = width;
        if (selectedShape != null) {
            selectedShape.setStrokeWidth(width);
            repaint();
        }
    }
    
    public float getStrokeWidth() {
        return currentStrokeWidth;
    }
    
    public Layer getCurrentLayer() {
        return currentLayer;
    }
} 