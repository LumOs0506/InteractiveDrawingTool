import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class DrawingApp extends JFrame {
    private DrawingPanel drawingPanel;
    private JTextField textField;
    private JMenuBar menuBar;
    private JToolBar toolBar;
    
    public DrawingApp() {
        setTitle("Drawing Tool");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);  // Increased width to accommodate layer panel
        
        drawingPanel = new DrawingPanel();
        LayerPanel layerPanel = new LayerPanel(drawingPanel);
        
        // Create a split pane to hold the drawing panel and layer panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, drawingPanel, layerPanel);
        splitPane.setDividerLocation(800);  // Set initial divider location
        
        add(splitPane, BorderLayout.CENTER);
        
        createMenuBar();
        createToolBar();
        
        setLocationRelativeTo(null);
    }
    
    private void createMenuBar() {
        menuBar = new JMenuBar();
        
        // File Menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem newItem = new JMenuItem("New");
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem exitItem = new JMenuItem("Exit");
        
        newItem.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to clear the entire drawing?",
                "New Drawing",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (response == JOptionPane.YES_OPTION) {
                drawingPanel.reset();
            }
        });
        
        openItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "PNG Images", "png"));
            
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    BufferedImage image = ImageIO.read(file);
                    drawingPanel.reset();
                    drawingPanel.setCurrentImage(image);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, 
                        "Error loading image: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        saveItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "PNG Images", "png"));
            
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".png")) {
                    file = new File(file.getAbsolutePath() + ".png");
                }
                drawingPanel.saveDrawing(file);
            }
        });
        
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // Edit Menu
        JMenu editMenu = new JMenu("Edit");
        JMenuItem undoItem = new JMenuItem("Undo");
        JMenuItem redoItem = new JMenuItem("Redo");
        
        undoItem.addActionListener(e -> drawingPanel.undo());
        redoItem.addActionListener(e -> drawingPanel.redo());
        
        editMenu.add(undoItem);
        editMenu.add(redoItem);
        
        // Insert Menu
        JMenu insertMenu = new JMenu("Insert");
        JMenuItem imageItem = new JMenuItem("Image");
        
        imageItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Image Files", "jpg", "jpeg", "png", "gif"));
            
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = fileChooser.getSelectedFile();
                    BufferedImage image = ImageIO.read(file);
                    drawingPanel.setCurrentImage(image);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, 
                        "Error loading image: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        insertMenu.add(imageItem);
        
        // Format Menu
        JMenu formatMenu = new JMenu("Format");
        JMenuItem colorMenuItem = new JMenuItem("Color...");
        JMenuItem fontMenuItem = new JMenuItem("Font...");
        
        colorMenuItem.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Choose Color", drawingPanel.getCurrentColor());
            if (newColor != null) {
                drawingPanel.setColor(newColor);
            }
        });
        
        fontMenuItem.addActionListener(e -> {
            Font currentFont = drawingPanel.getCurrentFont();
            Font newFont = JFontChooser.showDialog(this, "Choose Font", currentFont);
            if (newFont != null) {
                drawingPanel.setCurrentFont(newFont);
            }
        });
        
        formatMenu.add(colorMenuItem);
        formatMenu.add(fontMenuItem);
        
        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(insertMenu);
        menuBar.add(formatMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void createToolBar() {
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        // Shape buttons
        String[] shapes = {"Line", "Rectangle", "Circle", "Text"};
        ButtonGroup shapeGroup = new ButtonGroup();
        
        for (String shape : shapes) {
            JToggleButton button = new JToggleButton(shape);
            button.addActionListener(e -> {
                drawingPanel.setShape(shape);
                textField.setEnabled(shape.equals("Text"));
            });
            shapeGroup.add(button);
            toolBar.add(button);
        }
        
        // Text field for text input
        textField = new JTextField(15);
        textField.setEnabled(false);
        textField.addActionListener(e -> {
            String text = textField.getText();
            if (!text.isEmpty()) {
                drawingPanel.setCurrentText(text);
                textField.setText(""); // Clear the field after setting the text
            }
        });
        toolBar.add(textField);
        
        add(toolBar, BorderLayout.NORTH);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DrawingApp().setVisible(true);
        });
    }
} 