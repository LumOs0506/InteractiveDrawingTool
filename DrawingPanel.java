import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
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
    
    // Canvas navigation properties
    private double zoomFactor = 1.0;
    private int panX = 0;
    private int panY = 0;
    private boolean isPanning = false;
    private int panStartX, panStartY;
    private static final double MIN_ZOOM = 0.1;
    private static final double MAX_ZOOM = 5.0;
    private static final double ZOOM_STEP = 0.1;
    
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
        
        // Mouse listener for drawing and selection
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
                
                // Convert screen coordinates to canvas coordinates
                int canvasX = screenToCanvasX(e.getX());
                int canvasY = screenToCanvasY(e.getY());
                
                startX = canvasX;
                startY = canvasY;
                lastX = startX;
                lastY = startY;
                
                // Start panning with space + drag or middle mouse button
                if (e.getButton() == MouseEvent.BUTTON2 || 
                    (e.isControlDown() && e.isShiftDown())) {
                    isPanning = true;
                    panStartX = e.getX();
                    panStartY = e.getY();
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    return;
                }
                
                if (selectMode || e.isControlDown()) {
                    for (int i = layers.size() - 1; i >= 0; i--) {
                        Layer layer = layers.get(i);
                        if (layer.isVisible()) {
                            Shape shape = layer.getShapeAt(canvasX, canvasY);
                            if (shape != null) {
                                if (selectedShape != null) {
                                    selectedShape.setSelected(false);
                                }
                                
                                selectedShape = shape;
                                selectedShape.setSelected(true);
                                currentLayer = layer;
                                
                                if (shape.isResizeHandle(canvasX, canvasY)) {
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
                            currentDrawing = new Circle(currentColor, canvasX, canvasY, canvasX, canvasY, filled);
                            break;
                        case "Rectangle":
                            currentDrawing = new Rectangle(currentColor, canvasX, canvasY, canvasX, canvasY, filled);
                            break;
                        case "Image":
                            if (currentImage != null) {
                                currentDrawing = new ImageShape(currentImage, canvasX, canvasY, canvasX, canvasY);
                            }
                            break;
                        case "Text":
                            if (!currentText.isEmpty()) {
                                currentDrawing = new TextShape(currentText, currentColor, canvasX, canvasY, currentFont);
                                currentLayer.addShape(currentDrawing);
                                saveState();
                                currentDrawing = null;
                                repaint();
                            } else {
                                String text = JOptionPane.showInputDialog(this, "Enter text:");
                                if (text != null && !text.isEmpty()) {
                                    currentText = text;
                                    currentDrawing = new TextShape(currentText, currentColor, canvasX, canvasY, currentFont);
                                    currentLayer.addShape(currentDrawing);
                                    saveState();
                                    currentDrawing = null;
                                    repaint();
                                }
                            }
                            break;
                        case "Free":
                            currentDrawing = new FreeDrawing(currentColor, canvasX, canvasY, canvasX, canvasY, filled);
                            currentDrawing.setStrokeWidth(currentStrokeWidth);
                            break;
                        default:
                            currentDrawing = new Line(currentColor, canvasX, canvasY, canvasX, canvasY, filled);
                            currentDrawing.setStrokeWidth(currentStrokeWidth);
                    }
                    
                    if (currentDrawing != null) {
                        currentDrawing.setStrokeWidth(currentStrokeWidth);
                    }
                }
            }
            
            public void mouseReleased(MouseEvent e) {
                if (isPanning) {
                    isPanning = false;
                    setCursor(Cursor.getDefaultCursor());
                    return;
                }
                
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
        
        // Mouse motion listener for drawing, selection and panning
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                requestFocusInWindow();
                
                // Handle panning
                if (isPanning) {
                    int dx = e.getX() - panStartX;
                    int dy = e.getY() - panStartY;
                    panX += dx;
                    panY += dy;
                    panStartX = e.getX();
                    panStartY = e.getY();
                    repaint();
                    return;
                }
                
                // Convert screen coordinates to canvas coordinates
                int canvasX = screenToCanvasX(e.getX());
                int canvasY = screenToCanvasY(e.getY());
                
                if (selectedShape != null) {
                    if (isResizing) {
                        selectedShape.setEndPoint(canvasX, canvasY);
                    } else if (isMoving) {
                        selectedShape.move(canvasX - lastX, canvasY - lastY);
                    }
                    lastX = canvasX;
                    lastY = canvasY;
                    repaint();
                } else if (currentDrawing != null && !currentShape.equals("Text")) {
                    currentDrawing.setEndPoint(canvasX, canvasY);
                    repaint();
                }
            }
        });
        
        // Mouse wheel listener for zooming
        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                // Zoom in/out with Ctrl + mouse wheel
                if (e.isControlDown()) {
                    int wheelRotation = e.getWheelRotation();
                    
                    // Get mouse position for zoom centering
                    int mouseX = e.getX();
                    int mouseY = e.getY();
                    
                    // Convert to canvas coordinates before zoom
                    double oldCanvasX = (mouseX - panX) / zoomFactor;
                    double oldCanvasY = (mouseY - panY) / zoomFactor;
                    
                    // Adjust zoom factor
                    if (wheelRotation < 0) {
                        // Zoom in
                        zoomFactor = Math.min(MAX_ZOOM, zoomFactor + ZOOM_STEP);
                    } else {
                        // Zoom out
                        zoomFactor = Math.max(MIN_ZOOM, zoomFactor - ZOOM_STEP);
                    }
                    
                    // Calculate new screen position for the same canvas point
                    double newScreenX = oldCanvasX * zoomFactor + panX;
                    double newScreenY = oldCanvasY * zoomFactor + panY;
                    
                    // Adjust pan to keep mouse position stable
                    panX += (mouseX - newScreenX);
                    panY += (mouseY - newScreenY);
                    
                    repaint();
                }
            }
        });
        
        // Key listener for keyboard shortcuts
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Delete selected shape
                if ((e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE) 
                    && selectedShape != null && currentLayer != null) {
                    deleteSelectedShape();
                }
                
                // Reset zoom and pan with Ctrl+0
                if (e.getKeyCode() == KeyEvent.VK_0 && e.isControlDown()) {
                    resetView();
                }
                
                // Zoom in with Ctrl++ (plus)
                if (e.getKeyCode() == KeyEvent.VK_EQUALS && e.isControlDown()) {
                    zoomIn();
                }
                
                // Zoom out with Ctrl+- (minus)
                if (e.getKeyCode() == KeyEvent.VK_MINUS && e.isControlDown()) {
                    zoomOut();
                }
                
                // Toggle space bar for temporary panning
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                // Reset cursor when space bar is released
                if (e.getKeyCode() == KeyEvent.VK_SPACE && !isPanning) {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        });
    }
    
    // Convert screen X coordinate to canvas coordinate
    private int screenToCanvasX(int screenX) {
        return (int)((screenX - panX) / zoomFactor);
    }
    
    // Convert screen Y coordinate to canvas coordinate
    private int screenToCanvasY(int screenY) {
        return (int)((screenY - panY) / zoomFactor);
    }
    
    // Convert canvas X coordinate to screen coordinate
    private int canvasToScreenX(int canvasX) {
        return (int)(canvasX * zoomFactor + panX);
    }
    
    // Convert canvas Y coordinate to screen coordinate
    private int canvasToScreenY(int canvasY) {
        return (int)(canvasY * zoomFactor + panY);
    }
    
    // Reset zoom and pan to default values
    public void resetView() {
        zoomFactor = 1.0;
        panX = 0;
        panY = 0;
        repaint();
    }
    
    // Zoom in by one step
    public void zoomIn() {
        zoomFactor = Math.min(MAX_ZOOM, zoomFactor + ZOOM_STEP);
        repaint();
    }
    
    // Zoom out by one step
    public void zoomOut() {
        zoomFactor = Math.max(MIN_ZOOM, zoomFactor - ZOOM_STEP);
        repaint();
    }
    
    // Get current zoom factor
    public double getZoomFactor() {
        return zoomFactor;
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
        
        // Store original transform
        AffineTransform originalTransform = g2d.getTransform();
        
        // Enable antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Apply zoom and pan transformations
        g2d.translate(panX, panY);
        g2d.scale(zoomFactor, zoomFactor);
        
        // Draw checkerboard pattern for transparent background
        drawCheckerboardBackground(g2d);
        
        // Draw all layers
        for (Layer layer : layers) {
            layer.draw(g2d);
        }
        
        // Draw current drawing in progress
        if (currentDrawing != null) {
            currentDrawing.draw(g2d);
        }
        
        // Restore original transform
        g2d.setTransform(originalTransform);
    }
    
    // Draw a checkerboard pattern to indicate transparent areas
    private void drawCheckerboardBackground(Graphics2D g2d) {
        int tileSize = 10;
        int width = getWidth();
        int height = getHeight();
        
        // Calculate visible area in canvas coordinates
        int startX = screenToCanvasX(0);
        int startY = screenToCanvasY(0);
        int endX = screenToCanvasX(width);
        int endY = screenToCanvasY(height);
        
        // Adjust to tile boundaries
        startX = (startX / tileSize) * tileSize;
        startY = (startY / tileSize) * tileSize;
        
        // Draw tiles
        for (int y = startY; y <= endY; y += tileSize) {
            for (int x = startX; x <= endX; x += tileSize) {
                boolean isLightTile = ((x / tileSize) + (y / tileSize)) % 2 == 0;
                g2d.setColor(isLightTile ? new Color(240, 240, 240) : new Color(220, 220, 220));
                g2d.fillRect(x, y, tileSize, tileSize);
            }
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
    
    public Shape getSelectedShape() {
        return selectedShape;
    }
} 