import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.border.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class DrawingApp extends JFrame {
    private DrawingPanel drawingPanel;
    private JTextField textField;
    private JMenuBar menuBar;
    private JToolBar toolBar;
    private JToolBar propertiesBar;
    private static final int TOOLBAR_ICON_SIZE = 24;
    private static final Color DARK_BG_COLOR = new Color(220, 220, 220);
    private static final Color MID_BG_COLOR = new Color(240, 240, 240);
    private static final Color LIGHT_BG_COLOR = new Color(250, 250, 250);
    private static final Color TEXT_COLOR = new Color(40, 40, 40);
    private static final Color ACCENT_COLOR = new Color(64, 136, 230);
    
    public DrawingApp() {
        setTitle("Interactive Drawing Tool");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        
        // Set modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // Set color theme
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
        
        // Create the drawing panel
        drawingPanel = new DrawingPanel();
        drawingPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        drawingPanel.setBackground(Color.WHITE);
        
        // Create the layer panel
        LayerPanel layerPanel = new LayerPanel(drawingPanel);
        layerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 1, 0, 0, Color.GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        layerPanel.setBackground(MID_BG_COLOR);
        
        // Create the vertical toolbar (left side)
        createToolBar();
        
        // Create the properties panel (right side)
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
        
        // Create the menu bar
        createMenuBar();
        
        // Add status bar at the bottom
        JPanel statusBar = createStatusBar();
        mainPanel.add(statusBar, BorderLayout.SOUTH);
        
        // Add the main panel to the frame
        add(mainPanel);
        
        setLocationRelativeTo(null);
    }
    
    private void createMenuBar() {
        menuBar = new JMenuBar();
        menuBar.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        menuBar.setBackground(DARK_BG_COLOR);
        menuBar.setForeground(TEXT_COLOR);
        
        // File Menu
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
        
        // Edit Menu
        JMenu editMenu = createMenu("Edit", KeyEvent.VK_E);
        addMenuItem(editMenu, "Undo", KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), e -> drawingPanel.undo());
        addMenuItem(editMenu, "Redo", KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), e -> drawingPanel.redo());
        
        // Insert Menu
        JMenu insertMenu = createMenu("Insert", KeyEvent.VK_I);
        addMenuItem(insertMenu, "Image", KeyStroke.getKeyStroke(KeyEvent.VK_I, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), e -> insertImage());
        
        // Format Menu
        JMenu formatMenu = createMenu("Format", KeyEvent.VK_O);
        addMenuItem(formatMenu, "Color...", KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), e -> chooseColor());
        addMenuItem(formatMenu, "Font...", KeyStroke.getKeyStroke(KeyEvent.VK_T, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), e -> chooseFont());
        
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(insertMenu);
        menuBar.add(formatMenu);
        
        setJMenuBar(menuBar);
    }
    
    private JMenu createMenu(String title, int mnemonic) {
        JMenu menu = new JMenu(title);
        menu.setMnemonic(mnemonic);
        menu.setForeground(TEXT_COLOR);
        return menu;
    }
    
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
    
    private void createToolBar() {
        toolBar = new JToolBar(JToolBar.VERTICAL);
        toolBar.setFloatable(false);
        toolBar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY));
        toolBar.setBackground(DARK_BG_COLOR);
        
        // Shape buttons with modern styling
        String[] shapes = {"Line", "Rectangle", "Circle", "Text", "Free"};
        ButtonGroup shapeGroup = new ButtonGroup();
        
        for (String shape : shapes) {
            JToggleButton button = createStyledToggleButton(shape);
            button.addActionListener(e -> {
                drawingPanel.setShape(shape);
                textField.setEnabled(shape.equals("Text"));
            });
            shapeGroup.add(button);
            toolBar.add(button);
            toolBar.addSeparator(new Dimension(0, 5));
        }
    }
    
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
        
        // Add stroke width control
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
        
        // Add color selector
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
        
        // Add text field for text input
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
        
        // Add fill option
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
    
    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(DARK_BG_COLOR);
        statusBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        JLabel statusLabel = new JLabel("Ready");
        statusLabel.setForeground(TEXT_COLOR);
        statusBar.add(statusLabel, BorderLayout.WEST);
        
        return statusBar;
    }
    
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
    
    private void chooseColor() {
        Color newColor = JColorChooser.showDialog(this, "Choose Color", drawingPanel.getCurrentColor());
        if (newColor != null) {
            drawingPanel.setColor(newColor);
        }
    }
    
    private void chooseFont() {
        Font currentFont = drawingPanel.getCurrentFont();
        Font newFont = JFontChooser.showDialog(this, "Choose Font", currentFont);
        if (newFont != null) {
            drawingPanel.setCurrentFont(newFont);
        }
    }
    
    private void showError(String title, String message) {
        JOptionPane.showMessageDialog(this, 
            message,
            title,
            JOptionPane.ERROR_MESSAGE);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 显示启动画面
            SplashScreen splash = new SplashScreen();
            splash.showSplash(null);
            
            // 主应用程序将由启动画面在淡出后启动
        });
    }
}