import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class LayerPanel extends JPanel {
    private ArrayList<Layer> layers;
    private JList<Layer> layerList;
    private DefaultListModel<Layer> listModel;
    private JButton addLayerButton;
    private JButton deleteLayerButton;
    private JButton moveUpButton;
    private JButton moveDownButton;
    private JCheckBox fillCheckBox;
    private DrawingPanel drawingPanel;
    
    public LayerPanel(DrawingPanel drawingPanel) {
        this.drawingPanel = drawingPanel;
        this.layers = new ArrayList<>();
        this.listModel = new DefaultListModel<>();
        
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(200, 0));
        
        // Create layer list
        layerList = new JList<>(listModel);
        layerList.setCellRenderer(new LayerListCellRenderer());
        layerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        layerList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Layer selectedLayer = layerList.getSelectedValue();
                if (selectedLayer != null) {
                    drawingPanel.setCurrentLayer(selectedLayer);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(layerList);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        
        addLayerButton = new JButton("Add Layer");
        deleteLayerButton = new JButton("Delete Layer");
        moveUpButton = new JButton("Move Up");
        moveDownButton = new JButton("Move Down");
        fillCheckBox = new JCheckBox("Fill Shapes");
        
        addLayerButton.addActionListener(e -> addLayer());
        deleteLayerButton.addActionListener(e -> deleteLayer());
        moveUpButton.addActionListener(e -> moveLayerUp());
        moveDownButton.addActionListener(e -> moveLayerDown());
        fillCheckBox.addActionListener(e -> drawingPanel.setFilled(fillCheckBox.isSelected()));
        
        buttonPanel.add(addLayerButton);
        buttonPanel.add(deleteLayerButton);
        buttonPanel.add(moveUpButton);
        buttonPanel.add(moveDownButton);
        buttonPanel.add(fillCheckBox);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Add initial layer
        Layer initialLayer = new Layer("Layer 1");
        layers.add(initialLayer);
        listModel.addElement(initialLayer);
        layerList.setSelectedValue(initialLayer, true);
        
        // Share layers with DrawingPanel
        drawingPanel.setLayers(layers);
        drawingPanel.setCurrentLayer(initialLayer);
    }
    
    private void addLayer() {
        String name = "Layer " + (layers.size() + 1);
        Layer layer = new Layer(name);
        layers.add(layer);
        listModel.addElement(layer);
        layerList.setSelectedValue(layer, true);
        drawingPanel.setCurrentLayer(layer);
    }
    
    private void deleteLayer() {
        Layer selectedLayer = layerList.getSelectedValue();
        if (selectedLayer != null && layers.size() > 1) {
            int index = layers.indexOf(selectedLayer);
            layers.remove(selectedLayer);
            listModel.removeElement(selectedLayer);
            
            // Select the next available layer
            if (index >= layers.size()) {
                index = layers.size() - 1;
            }
            Layer newSelectedLayer = layers.get(index);
            layerList.setSelectedValue(newSelectedLayer, true);
            drawingPanel.setCurrentLayer(newSelectedLayer);
        }
    }
    
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
    
    public ArrayList<Layer> getLayers() {
        return layers;
    }
    
    private class LayerListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            JCheckBox checkBox = new JCheckBox();
            Layer layer = (Layer) value;
            checkBox.setText(layer.getName());
            checkBox.setSelected(layer.isVisible());
            checkBox.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            checkBox.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            
            checkBox.addActionListener(e -> {
                layer.setVisible(checkBox.isSelected());
                drawingPanel.repaint();
            });
            
            return checkBox;
        }
    }
} 