import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Main application class that creates the drawing tool interface
 * This class sets up the window, menus, and all UI components
 */
public class DrawingApp extends JFrame {
    private DrawingPanel drawingPanel;
    private JTextField textField;
    private JMenuBar menuBar;
    private JToolBar toolBar;
    private JToolBar propertiesBar;
    private JLabel statusLabel;
    // Constants for UI sizes and colors
    private static final Color DARK_BG_COLOR = new Color(220, 220, 220);
    private static final Color MID_BG_COLOR = new Color(240, 240, 240);
    private static final Color LIGHT_BG_COLOR = new Color(250, 250, 250);
    private static final Color TEXT_COLOR = new Color(40, 40, 40);
    private static final Color ACCENT_COLOR = new Color(64, 136, 230);
    
    /**
     * Constructor - creates the application window and all UI components
     */
    public DrawingApp() {
        setTitle("Interactive Drawing Tool");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        
        // Set modern look and feel for the application
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // Set color theme for a consistent look
            UIManager.put("Panel.background", DARK_BG_COLOR);
            UIManager.put("ToolBar.background", DARK_BG_COLOR);
            UIManager.put("ToolBar.foreground", TEXT_COLOR);
            UIManager.put("Label.foreground", TEXT_COLOR);
            UIManager.put("Button.background", MID_BG_COLOR);
            UIManager.put("Button.foreground", TEXT_COLOR);
            UIManager.put("ToggleButton.background", MID_BG_COLOR);
            UIManager.put("ToggleButton.foreground", TEXT_COLOR);
            UIManager.put("ToggleButton.select", ACCENT_COLOR);
            UIManager.put("Slider.background", DARK_BG_COLOR);
            UIManager.put("Slider.foreground", TEXT_COLOR);
            UIManager.put("TabbedPane.background", DARK_BG_COLOR);
            UIManager.put("TabbedPane.foreground", TEXT_COLOR);
            
            // Set Mac-specific properties for menu bar
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Interactive Drawing Tool");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Set the container panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(DARK_BG_COLOR);
        
        // Create the drawing panel - this is where users will draw
        drawingPanel = new DrawingPanel();
        drawingPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//        drawingPanel.setBackground(Color.WHITE);
        
        // Create the layer panel - shows all layers in the drawing
        LayerPanel layerPanel = new LayerPanel(drawingPanel);
        drawingPanel.setLayerPanel(layerPanel);
        layerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 1, 0, 0, Color.GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        layerPanel.setBackground(MID_BG_COLOR);
        
        // Create the vertical toolbar (left side) with drawing tools
        createToolBar();
        
        // Create the properties panel (right side) with settings
        createPropertiesPanel();
        
        // Create center panel for drawing area
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(DARK_BG_COLOR);
        centerPanel.add(drawingPanel, BorderLayout.CENTER);
        
        // Create right panel for properties and layers
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(MID_BG_COLOR);
        rightPanel.add(propertiesBar, BorderLayout.NORTH);
        
        // Add layer panel to the bottom of the right panel
        JPanel bottomRightPanel = new JPanel(new BorderLayout());
        bottomRightPanel.setBackground(MID_BG_COLOR);
        JLabel layersLabel = new JLabel("LAYERS");
        layersLabel.setForeground(TEXT_COLOR);
        layersLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        bottomRightPanel.add(layersLabel, BorderLayout.NORTH);
        bottomRightPanel.add(layerPanel, BorderLayout.CENTER);
        rightPanel.add(bottomRightPanel, BorderLayout.CENTER);
        
        // Create a split pane for the main content and right panel
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, centerPanel, rightPanel);
        mainSplitPane.setDividerLocation(900);
        mainSplitPane.setDividerSize(4);
        mainSplitPane.setBorder(null);
        
        // Create the left and main content split pane
        JSplitPane contentSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, toolBar, mainSplitPane);
        contentSplitPane.setDividerLocation(60);
        contentSplitPane.setDividerSize(1);
        contentSplitPane.setBorder(null);
        
        mainPanel.add(contentSplitPane, BorderLayout.CENTER);
        
        // Create the menu bar at the top of the window
        createMenuBar();
        
        // Add status bar at the bottom of the window
        JPanel statusBar = createStatusBar();
        mainPanel.add(statusBar, BorderLayout.SOUTH);
        
        // Add the main panel to the frame
        add(mainPanel);
        
        // Center the window on screen
        setLocationRelativeTo(null);
    }
    
    /**
     * Creates the menu bar with File, Edit, View, Insert and Format menus
     */
    private void createMenuBar() {
        menuBar = new JMenuBar();
        menuBar.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        menuBar.setBackground(DARK_BG_COLOR);
        menuBar.setForeground(TEXT_COLOR);
        
        // File Menu - for file operations like New, Open, Save, Exit
        JMenu fileMenu = createMenu("File", KeyEvent.VK_F);
        addMenuItem(fileMenu, "New", KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), e -> {
            int response = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to clear the entire drawing?",
                "New Drawing",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (response == JOptionPane.YES_OPTION) {
                drawingPanel.reset();
            }
        });
        
        addMenuItem(fileMenu, "Open", KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), e -> openFile());
        addMenuItem(fileMenu, "Save", KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), e -> saveFile());
        fileMenu.addSeparator();
        addMenuItem(fileMenu, "Exit", KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), e -> System.exit(0));
        
        // Edit Menu - for editing operations like Undo, Redo, Delete
        JMenu editMenu = createMenu("Edit", KeyEvent.VK_E);
        addMenuItem(editMenu, "Undo", KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), e -> drawingPanel.undo());
        addMenuItem(editMenu, "Redo", KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), e -> drawingPanel.redo());
        addMenuItem(editMenu, "Delete", KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), e -> drawingPanel.deleteSelectedShape());
        
        // View Menu - for view operations like Zoom In, Zoom Out, Reset View
        JMenu viewMenu = createMenu("View", KeyEvent.VK_V);
        addMenuItem(viewMenu, "Zoom In", KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), e -> drawingPanel.zoomIn());
        addMenuItem(viewMenu, "Zoom Out", KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), e -> drawingPanel.zoomOut());
        addMenuItem(viewMenu, "Reset View", KeyStroke.getKeyStroke(KeyEvent.VK_0, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), e -> drawingPanel.resetView());
        
        // Insert Menu - for inserting images
        JMenu insertMenu = createMenu("Insert", KeyEvent.VK_I);
        addMenuItem(insertMenu, "Image", KeyStroke.getKeyStroke(KeyEvent.VK_I, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), e -> insertImage());
        
        // Format Menu - for formatting options like Color and Font
        JMenu formatMenu = createMenu("Format", KeyEvent.VK_O);
        addMenuItem(formatMenu, "Color...", KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), e -> chooseColor());
        addMenuItem(formatMenu, "Font...", KeyStroke.getKeyStroke(KeyEvent.VK_T, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), e -> chooseFont());
        
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(insertMenu);
        menuBar.add(formatMenu);
        
        setJMenuBar(menuBar);
    }
    
    /**
     * Helper method to create a menu with title and mnemonic key
     */
    private JMenu createMenu(String title, int mnemonic) {
        JMenu menu = new JMenu(title);
        menu.setMnemonic(mnemonic);
        menu.setForeground(TEXT_COLOR);
        return menu;
    }
    
    /**
     * Helper method to add a menu item with title, keyboard shortcut and action
     */
    private void addMenuItem(JMenu menu, String title, KeyStroke accelerator, ActionListener action) {
        JMenuItem item = new JMenuItem(title);
        item.setForeground(TEXT_COLOR);
        item.setBackground(DARK_BG_COLOR);
        if (accelerator != null) {
            item.setAccelerator(accelerator);
        }
        item.addActionListener(action);
        menu.add(item);
    }
    
    /**
     * Creates the toolbar with drawing tools like Select, Line, Rectangle, etc.
     */
    private void createToolBar() {
        toolBar = new JToolBar(JToolBar.VERTICAL);
        toolBar.setFloatable(false);
        toolBar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY));
        toolBar.setBackground(DARK_BG_COLOR);
        
        // Shape buttons with modern styling
        String[] shapes = {"Select", "Line", "Rectangle", "Circle", "Text", "Free"};
        ButtonGroup shapeGroup = new ButtonGroup();
        
        for (String shape : shapes) {
            JToggleButton button = createStyledToggleButton(shape);
            button.addActionListener(e -> {
                drawingPanel.setShape(shape);
                textField.setEnabled(shape.equals("Text"));
                
                // When Select tool is chosen
                if (shape.equals("Select")) {
                    drawingPanel.setSelectMode(true);
                    updateStatusMessage("Select Mode: Click to select shapes. Use Delete key to remove selected shapes.");
                } else {
                    drawingPanel.setSelectMode(false);
                    updateStatusMessage("Ready");
                }
            });
            
            // Default select Select tool
            if (shape.equals("Select")) {
                button.setSelected(true);
                drawingPanel.setSelectMode(true);
                updateStatusMessage("Select Mode: Click to select shapes. Use Delete key to remove selected shapes.");
            }
            
            shapeGroup.add(button);
            toolBar.add(button);
            toolBar.addSeparator(new Dimension(0, 5));
        }
    }
    
    /**
     * Creates the properties panel with controls for stroke width, color, text, etc.
     */
    private void createPropertiesPanel() {
        propertiesBar = new JToolBar();
        propertiesBar.setFloatable(false);
        propertiesBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        propertiesBar.setBackground(MID_BG_COLOR);
        propertiesBar.setLayout(new BoxLayout(propertiesBar, BoxLayout.Y_AXIS));
        
        // Create a title for properties panel
        JLabel propertiesTitle = new JLabel("PROPERTIES");
        propertiesTitle.setForeground(TEXT_COLOR);
        propertiesTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        propertiesTitle.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        propertiesBar.add(propertiesTitle);
        
        // Add stroke width control - slider to adjust line thickness
        JPanel strokePanel = new JPanel();
        strokePanel.setBackground(MID_BG_COLOR);
        strokePanel.setLayout(new BoxLayout(strokePanel, BoxLayout.Y_AXIS));
        strokePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        strokePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel strokeLabel = new JLabel("Stroke Width:");
        strokeLabel.setForeground(TEXT_COLOR);
        strokeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        strokePanel.add(strokeLabel);
        
        JSlider strokeSlider = new JSlider(JSlider.HORIZONTAL, 1, 20, 1);
        strokeSlider.setBackground(MID_BG_COLOR);
        strokeSlider.setForeground(TEXT_COLOR);
        strokeSlider.setMajorTickSpacing(5);
        strokeSlider.setMinorTickSpacing(1);
        strokeSlider.setPaintTicks(true);
        strokeSlider.setPaintLabels(true);
        strokeSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        strokeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                float strokeWidth = (float) strokeSlider.getValue();
                drawingPanel.setStrokeWidth(strokeWidth);
            }
        });
        strokePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        strokePanel.add(strokeSlider);
        
        propertiesBar.add(strokePanel);
        
        // Add color selector - button to choose drawing color
        JPanel colorPanel = new JPanel();
        colorPanel.setBackground(MID_BG_COLOR);
        colorPanel.setLayout(new BoxLayout(colorPanel, BoxLayout.Y_AXIS));
        colorPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        colorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel colorLabel = new JLabel("Color:");
        colorLabel.setForeground(TEXT_COLOR);
        colorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        colorPanel.add(colorLabel);
        
        JButton colorButton = new JButton("Choose Color");
        colorButton.setBackground(LIGHT_BG_COLOR);
        colorButton.setForeground(TEXT_COLOR);
        colorButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        colorButton.addActionListener(e -> chooseColor());
        colorPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        colorPanel.add(colorButton);
        
        propertiesBar.add(colorPanel);
        
        // Add text field for text input - used when adding text to drawing
        JPanel textPanel = new JPanel();
        textPanel.setBackground(MID_BG_COLOR);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        textPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel textLabel = new JLabel("Text:");
        textLabel.setForeground(TEXT_COLOR);
        textLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        textPanel.add(textLabel);
        
        textField = new JTextField(15);
        textField.setEnabled(false);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);
        textField.addActionListener(e -> {
            String text = textField.getText();
            if (!text.isEmpty()) {
                drawingPanel.setCurrentText(text);
                textField.setText("");
            }
        });
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(textField);
        
        JButton fontButton = new JButton("Choose Font");
        fontButton.setBackground(LIGHT_BG_COLOR);
        fontButton.setForeground(TEXT_COLOR);
        fontButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        fontButton.addActionListener(e -> chooseFont());
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(fontButton);
        
        propertiesBar.add(textPanel);
        
        // Add fill option - checkbox to toggle filled shapes
        JPanel fillPanel = new JPanel();
        fillPanel.setBackground(MID_BG_COLOR);
        fillPanel.setLayout(new BoxLayout(fillPanel, BoxLayout.Y_AXIS));
        fillPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        fillPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JCheckBox fillCheckBox = new JCheckBox("Fill Shape");
        fillCheckBox.setBackground(MID_BG_COLOR);
        fillCheckBox.setForeground(TEXT_COLOR);
        fillCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        fillCheckBox.addActionListener(e -> drawingPanel.setFilled(fillCheckBox.isSelected()));
        fillPanel.add(fillCheckBox);
        
        propertiesBar.add(fillPanel);
        
        // Add filler to push everything to the top
        propertiesBar.add(Box.createVerticalGlue());
    }
    
    /**
     * Helper method to create styled toggle buttons for the toolbar
     */
    private JToggleButton createStyledToggleButton(String text) {
        JToggleButton button = new JToggleButton(text);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(50, 50));
        button.setMaximumSize(new Dimension(50, 50));
        button.setBackground(LIGHT_BG_COLOR);
        button.setForeground(TEXT_COLOR);
        button.setFont(button.getFont().deriveFont(11f));
        return button;
    }
    
    /**
     * Creates the status bar at the bottom of the window
     * Contains status messages, help button and zoom controls
     */
    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(DARK_BG_COLOR);
        statusBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Status message on the left side
        statusLabel = new JLabel("Ready");
        statusLabel.setForeground(TEXT_COLOR);
        statusBar.add(statusLabel, BorderLayout.WEST);
        
        // Add center panel with help button
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setBackground(DARK_BG_COLOR);
        
        JButton helpButton = new JButton("Navigation Help");
        helpButton.setToolTipText("Show canvas navigation instructions");
        helpButton.addActionListener(e -> showNavigationHelp());
        centerPanel.add(helpButton);
        
        statusBar.add(centerPanel, BorderLayout.CENTER);
        
        // Add zoom controls on the right side
        JPanel zoomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        zoomPanel.setBackground(DARK_BG_COLOR);
        
        JButton zoomOutButton = new JButton("-");
        zoomOutButton.setToolTipText("Zoom Out (Ctrl+-)");
        zoomOutButton.addActionListener(e -> drawingPanel.zoomOut());
        
        JLabel zoomLabel = new JLabel("100%");
        zoomLabel.setForeground(TEXT_COLOR);
        
        JButton zoomInButton = new JButton("+");
        zoomInButton.setToolTipText("Zoom In (Ctrl++)");
        zoomInButton.addActionListener(e -> drawingPanel.zoomIn());
        
        JButton zoomResetButton = new JButton("Reset View");
        zoomResetButton.setToolTipText("Reset Zoom and Pan (Ctrl+0)");
        zoomResetButton.addActionListener(e -> drawingPanel.resetView());
        
        zoomPanel.add(zoomOutButton);
        zoomPanel.add(zoomLabel);
        zoomPanel.add(zoomInButton);
        zoomPanel.add(zoomResetButton);
        
        statusBar.add(zoomPanel, BorderLayout.EAST);
        
        // Update zoom percentage display every 100ms
        Timer zoomUpdateTimer = new Timer(100, e -> {
            int zoomPercentage = (int)(drawingPanel.getZoomFactor() * 100);
            zoomLabel.setText(zoomPercentage + "%");
        });
        zoomUpdateTimer.start();
        
        return statusBar;
    }
    
    /**
     * Updates the status message shown at the bottom of the window
     */
    private void updateStatusMessage(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }
    
    /**
     * Opens a PNG image file and displays it in the drawing panel
     */
    private void openFile() {
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
                showError("Error loading image", ex.getMessage());
            }
        }
    }
    
    /**
     * Saves the current drawing as a PNG image file
     */
    private void saveFile() {
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
    }
    
    /**
     * Inserts an image into the drawing
     */
    private void insertImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Image Files", "jpg", "jpeg", "png", "gif"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                BufferedImage image = ImageIO.read(file);
                drawingPanel.setCurrentImage(image);
            } catch (Exception ex) {
                showError("Error loading image", ex.getMessage());
            }
        }
    }
    
    /**
     * Opens a color chooser dialog to select a new drawing color
     */
    private void chooseColor() {
        Color newColor = JColorChooser.showDialog(this, "Choose Color", drawingPanel.getCurrentColor());
        if (newColor != null) {
            drawingPanel.setColor(newColor);
        }
    }
    
    /**
     * Opens a font chooser dialog to select a new text font
     */
    private void chooseFont() {
        Font currentFont = drawingPanel.getCurrentFont();
        Font newFont = JFontChooser.showDialog(this, "Choose Font", currentFont);
        if (newFont != null) {
            drawingPanel.setCurrentFont(newFont);
        }
    }
    
    /**
     * Shows an error message dialog
     */
    private void showError(String title, String message) {
        JOptionPane.showMessageDialog(this, 
            message,
            title,
            JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Shows a help dialog with canvas navigation instructions
     */
    private void showNavigationHelp() {
        JOptionPane.showMessageDialog(this,
            "Canvas Navigation Controls:\n\n" +
            "• Zoom In: Ctrl + Mouse Wheel Up or Ctrl + '+'\n" +
            "• Zoom Out: Ctrl + Mouse Wheel Down or Ctrl + '-'\n" +
            "• Reset View: Ctrl + 0\n" +
            "• Pan Canvas: Middle Mouse Button Drag or Ctrl + Shift + Drag\n" +
            "• Temporary Pan Mode: Hold Space Bar\n\n" +
            "When zoomed in, you can navigate around the canvas to work on details.",
            "Canvas Navigation Help",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Main method - entry point of the application
     * Shows a splash screen and then launches the main window
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Show splash screen first
            SplashScreen splash = new SplashScreen();
            splash.showSplash(null);
            
            // Main application will be launched after splash screen fades out
        });
    }
}