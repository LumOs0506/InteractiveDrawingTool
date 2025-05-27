import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;

public class JFontChooser extends JDialog {
    private Font selectedFont;
    private JList<String> fontNameList;
    private JList<String> fontStyleList;
    private JList<String> fontSizeList;
    private JTextField previewField;
    
    public JFontChooser(Frame parent, String title, Font initialFont) {
        super(parent, title, true);
        
        selectedFont = initialFont;
        
        // Get available fonts
        String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        String[] fontStyles = {"Plain", "Bold", "Italic", "Bold Italic"};
        String[] fontSizes = {"8", "10", "12", "14", "16", "18", "20", "24", "28", "32", "36", "40", "48", "56", "64", "72"};
        
        // Create lists
        fontNameList = new JList<>(fontNames);
        fontStyleList = new JList<>(fontStyles);
        fontSizeList = new JList<>(fontSizes);
        
        // Set initial selections
        fontNameList.setSelectedValue(initialFont.getFamily(), true);
        fontStyleList.setSelectedIndex(initialFont.getStyle());
        fontSizeList.setSelectedValue(String.valueOf(initialFont.getSize()), true);
        
        // Create preview field
        previewField = new JTextField("Preview Text");
        previewField.setEditable(false);
        updatePreview();
        
        // Add listeners
        ListSelectionListener listListener = e -> updatePreview();
        fontNameList.addListSelectionListener(listListener);
        fontStyleList.addListSelectionListener(listListener);
        fontSizeList.addListSelectionListener(listListener);
        
        // Layout
        JPanel listPanel = new JPanel(new GridLayout(1, 3, 5, 0));
        listPanel.add(new JScrollPane(fontNameList));
        listPanel.add(new JScrollPane(fontStyleList));
        listPanel.add(new JScrollPane(fontSizeList));
        
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        
        okButton.addActionListener(e -> {
            selectedFont = getSelectedFont();
            dispose();
        });
        
        cancelButton.addActionListener(e -> {
            selectedFont = null;
            dispose();
        });
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        setLayout(new BorderLayout(5, 5));
        add(listPanel, BorderLayout.CENTER);
        add(previewField, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void updatePreview() {
        Font font = getSelectedFont();
        if (font != null) {
            previewField.setFont(font);
        }
    }
    
    private Font getSelectedFont() {
        String fontName = fontNameList.getSelectedValue();
        int fontStyle = fontStyleList.getSelectedIndex();
        int fontSize = Integer.parseInt(fontSizeList.getSelectedValue());
        
        return new Font(fontName, fontStyle, fontSize);
    }
    
    public static Font showDialog(Component parent, String title, Font initialFont) {
        JFontChooser chooser = new JFontChooser(
            parent instanceof Frame ? (Frame)parent : null,
            title,
            initialFont
        );
        chooser.setVisible(true);
        return chooser.selectedFont;
    }
} 