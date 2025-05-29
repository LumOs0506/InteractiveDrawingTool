import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * LayerPanel class creates the UI panel for managing layers
 * This panel shows the list of layers and provides controls to add, delete, and reorder layers
 * Similar to the layers panel in Photoshop or other graphics programs
 */
public class LayerPanel extends JPanel {
    private ArrayList<Layer> layers;           // List of all layers in the drawing
    private JList<Layer> layerList;            // UI component showing the layers
    private DefaultListModel<Layer> listModel; // Data model for the layer list
    private JButton addLayerButton;            // Button to add a new layer
    private JButton deleteLayerButton;         // Button to delete the selected layer
    private JButton moveUpButton;              // Button to move layer up in the stack
    private JButton moveDownButton;            // Button to move layer down in the stack
    private JCheckBox visibilityCheckBox;      // Checkbox to toggle layer visibility
    private DrawingPanel drawingPanel;         // Reference to the main drawing panel
    
    /**
     * Constructor - creates the layer panel with all its controls
     * 
     * @param drawingPanel Reference to the main drawing panel
     */
    public LayerPanel(DrawingPanel drawingPanel) {
        this.drawingPanel = drawingPanel;
        this.layers = new ArrayList<>();
        this.listModel = new DefaultListModel<>();
        
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(200, 0));
        
        // Create layer list with custom cell renderer
        layerList = new JList<>(listModel);
        layerList.setCellRenderer(new LayerListCellRenderer());
        layerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        layerList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Layer selectedLayer = layerList.getSelectedValue();
                if (selectedLayer != null) {
                    drawingPanel.setCurrentLayer(selectedLayer);
                    
                    // Update visibility checkbox state
                    visibilityCheckBox.setSelected(selectedLayer.isVisible());
                }
            }
        });
        
        // Add mouse listener to handle clicks on visibility icons
        layerList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Handle clicks in the layer list, check if clicking on visibility icon
                if (e.getX() < 20) { // Assume visibility icon is within 20 pixels from left
                    int index = layerList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        Layer layer = listModel.getElementAt(index);
                        layer.setVisible(!layer.isVisible());
                        drawingPanel.repaint();
                        layerList.repaint();
                    }
                }
            }
        });
        
        // Add scrollbar to layer list
        JScrollPane scrollPane = new JScrollPane(layerList);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create layer properties panel
        JPanel layerPropertiesPanel = new JPanel(new BorderLayout());
        
        // Create visibility toggle checkbox
        JPanel visibilityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        visibilityCheckBox = new JCheckBox("Visible");
        visibilityCheckBox.addActionListener(e -> {
            Layer selectedLayer = layerList.getSelectedValue();
            if (selectedLayer != null) {
                selectedLayer.setVisible(visibilityCheckBox.isSelected());
                drawingPanel.repaint();
                layerList.repaint();
            }
        });
        visibilityPanel.add(visibilityCheckBox);
        layerPropertiesPanel.add(visibilityPanel, BorderLayout.NORTH);
        
        // Create button panel with layer operations
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        
        addLayerButton = new JButton("Add Layer");
        deleteLayerButton = new JButton("Delete Layer");
        moveUpButton = new JButton("Move Up");
        moveDownButton = new JButton("Move Down");
        
        addLayerButton.addActionListener(e -> addLayer());
        deleteLayerButton.addActionListener(e -> deleteLayer());
        moveUpButton.addActionListener(e -> moveLayerUp());
        moveDownButton.addActionListener(e -> moveLayerDown());
        
        buttonPanel.add(addLayerButton);
        buttonPanel.add(deleteLayerButton);
        buttonPanel.add(moveUpButton);
        buttonPanel.add(moveDownButton);
        
        layerPropertiesPanel.add(buttonPanel, BorderLayout.CENTER);
        add(layerPropertiesPanel, BorderLayout.SOUTH);
        
        // Add initial layer
        Layer initialLayer = new Layer("Layer 1");
        layers.add(initialLayer);
        listModel.addElement(initialLayer);
        layerList.setSelectedValue(initialLayer, true);
        
        // Share layers with DrawingPanel
        drawingPanel.setLayers(layers);
        drawingPanel.setCurrentLayer(initialLayer);
    }
    
    /**
     * Adds a new layer to the drawing
     * The new layer is added at the top of the stack
     */
    private void addLayer() {
        String name = "Layer " + (layers.size() + 1);
        Layer layer = new Layer(name);
        layers.add(0, layer); // Add to the top
        listModel.add(0, layer);
        layerList.setSelectedValue(layer, true);
        drawingPanel.setCurrentLayer(layer);
    }
    
    /**
     * Deletes the currently selected layer
     * Shows a confirmation dialog before deleting
     * Prevents deleting the last remaining layer
     */
    private void deleteLayer() {
        Layer selectedLayer = layerList.getSelectedValue();
        if (selectedLayer != null && layers.size() > 1) {
            int index = layers.indexOf(selectedLayer);
            
            // Show confirmation dialog
            int response = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete the layer '" + selectedLayer.getName() + "'?",
                "Delete Layer",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (response == JOptionPane.YES_OPTION) {
                layers.remove(selectedLayer);
                listModel.removeElement(selectedLayer);
                
                // Select the next available layer
                if (index >= layers.size()) {
                    index = layers.size() - 1;
                }
                Layer newSelectedLayer = layers.get(index);
                layerList.setSelectedValue(newSelectedLayer, true);
                drawingPanel.setCurrentLayer(newSelectedLayer);
                drawingPanel.repaint();
            }
        } else if (layers.size() <= 1) {
            JOptionPane.showMessageDialog(
                this,
                "Cannot delete the last layer.",
                "Delete Layer",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    /**
     * Moves the selected layer up in the stack (higher z-index)
     * This makes the layer appear on top of the layer that was previously above it
     */
    private void moveLayerUp() {
        int selectedIndex = layerList.getSelectedIndex();
        if (selectedIndex > 0) {
            Layer layer = layers.remove(selectedIndex);
            layers.add(selectedIndex - 1, layer);
            listModel.remove(selectedIndex);
            listModel.add(selectedIndex - 1, layer);
            layerList.setSelectedIndex(selectedIndex - 1);
            drawingPanel.repaint();
        }
    }
    
    /**
     * Moves the selected layer down in the stack (lower z-index)
     * This makes the layer appear below the layer that was previously under it
     */
    private void moveLayerDown() {
        int selectedIndex = layerList.getSelectedIndex();
        if (selectedIndex < layers.size() - 1) {
            Layer layer = layers.remove(selectedIndex);
            layers.add(selectedIndex + 1, layer);
            listModel.remove(selectedIndex);
            listModel.add(selectedIndex + 1, layer);
            layerList.setSelectedIndex(selectedIndex + 1);
            drawingPanel.repaint();
        }
    }
    
    /**
     * Gets the list of all layers
     * 
     * @return ArrayList of all layers
     */
    public ArrayList<Layer> getLayers() {
        return layers;
    }
    
    /**
     * Custom renderer for the layer list items
     * Shows layer name and visibility icon for each layer
     */
    private class LayerListCellRenderer extends DefaultListCellRenderer {
        private final ImageIcon visibleIcon = createVisibleIcon();
        private final ImageIcon hiddenIcon = createHiddenIcon();
        
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof Layer) {
                Layer layer = (Layer) value;
                setText(layer.getName());
                
                // Set visibility icon
                setIcon(layer.isVisible() ? visibleIcon : hiddenIcon);
                
                // Add special marking for currently selected layer
                if (layerList.getSelectedValue() == layer) {
                    setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLUE),
                        getBorder()
                    ));
                    setFont(getFont().deriveFont(Font.BOLD));
                }
            }
            
            return this;
        }
        
        /**
         * Creates an icon showing an eye for visible layers
         */
        private ImageIcon createVisibleIcon() {
            BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = (Graphics2D) image.getGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(Color.BLACK);
            g2d.fillOval(3, 5, 10, 6);
            g2d.setColor(Color.WHITE);
            g2d.fillOval(5, 7, 6, 2);
            g2d.dispose();
            return new ImageIcon(image);
        }
        
        /**
         * Creates an icon showing a crossed-out eye for hidden layers
         */
        private ImageIcon createHiddenIcon() {
            BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = (Graphics2D) image.getGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(Color.GRAY);
            g2d.drawLine(3, 3, 13, 13);
            g2d.drawOval(3, 5, 10, 6);
            g2d.dispose();
            return new ImageIcon(image);
        }
    }
} 