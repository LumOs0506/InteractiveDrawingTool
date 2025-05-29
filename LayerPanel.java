import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class LayerPanel extends JPanel {
    private ArrayList<Layer> layers;
    private JList<Layer> layerList;
    private DefaultListModel<Layer> listModel;
    private JButton addLayerButton;
    private JButton deleteLayerButton;
    private JButton moveUpButton;
    private JButton moveDownButton;
    private JCheckBox visibilityCheckBox;
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
                    
                    // 更新可见性复选框状态
                    visibilityCheckBox.setSelected(selectedLayer.isVisible());
                }
            }
        });
        
        layerList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 处理层列表中的点击，检查是否点击在可见性图标上
                if (e.getX() < 20) { // 假设可见性图标在左边20像素内
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
        
        JScrollPane scrollPane = new JScrollPane(layerList);
        add(scrollPane, BorderLayout.CENTER);
        
        // 创建图层属性面板
        JPanel layerPropertiesPanel = new JPanel(new BorderLayout());
        
        // 创建可见性切换
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
        
        // Create button panel
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
    
    private void addLayer() {
        String name = "Layer " + (layers.size() + 1);
        Layer layer = new Layer(name);
        layers.add(0, layer); // 添加到顶部
        listModel.add(0, layer);
        layerList.setSelectedValue(layer, true);
        drawingPanel.setCurrentLayer(layer);
    }
    
    private void deleteLayer() {
        Layer selectedLayer = layerList.getSelectedValue();
        if (selectedLayer != null && layers.size() > 1) {
            int index = layers.indexOf(selectedLayer);
            
            // 确认对话框
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
    
    // 自定义渲染器，显示图层名称和可见性图标
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
                
                // 设置可见性图标
                setIcon(layer.isVisible() ? visibleIcon : hiddenIcon);
                
                // 为当前选中图层添加特殊标记
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
        
        // 创建可见性图标
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
        
        // 创建隐藏图标
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