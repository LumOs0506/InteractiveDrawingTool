import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

/**
 * JFontChooser provides a dialog for selecting fonts
 * Similar to the font chooser dialog in word processors
 * Allows users to select font name, style, and size
 */
public class JFontChooser extends JDialog {
    private Font selectedFont;       // The font that will be returned when OK is clicked
    private JList<String> fontNameList;  // List of available font names
    private JList<String> fontStyleList; // List of font styles (Plain, Bold, etc.)
    private JList<String> fontSizeList;  // List of font sizes
    private JTextField previewField;     // Shows a preview of the selected font
    
    /**
     * Constructor - creates the font chooser dialog
     * 
     * @param parent      The parent window
     * @param title       The title for the dialog
     * @param initialFont The font to show initially
     */
    public JFontChooser(Frame parent, String title, Font initialFont) {
        super(parent, title, true);  // true makes it modal (blocks input to other windows)
        
        selectedFont = initialFont;
        
        // Get available fonts from the system
        String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        String[] fontStyles = {"Plain", "Bold", "Italic", "Bold Italic"};
        String[] fontSizes = {"8", "10", "12", "14", "16", "18", "20", "24", "28", "32", "36", "40", "48", "56", "64", "72"};
        
        // Create lists for each property
        fontNameList = new JList<>(fontNames);
        fontStyleList = new JList<>(fontStyles);
        fontSizeList = new JList<>(fontSizes);
        
        // Set initial selections based on the provided font
        fontNameList.setSelectedValue(initialFont.getFamily(), true);
        fontStyleList.setSelectedIndex(initialFont.getStyle());
        fontSizeList.setSelectedValue(String.valueOf(initialFont.getSize()), true);
        
        // Create preview field to show how the selected font looks
        previewField = new JTextField("Preview Text");
        previewField.setEditable(false);
        updatePreview();
        
        // Add listeners to update preview when selections change
        ListSelectionListener listListener = e -> updatePreview();
        fontNameList.addListSelectionListener(listListener);
        fontStyleList.addListSelectionListener(listListener);
        fontSizeList.addListSelectionListener(listListener);
        
        // Layout the components in the dialog
        JPanel listPanel = new JPanel(new GridLayout(1, 3, 5, 0));
        listPanel.add(new JScrollPane(fontNameList));
        listPanel.add(new JScrollPane(fontStyleList));
        listPanel.add(new JScrollPane(fontSizeList));
        
        // Create buttons for OK and Cancel
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        
        // OK button saves the selected font and closes the dialog
        okButton.addActionListener(e -> {
            selectedFont = getSelectedFont();
            dispose();
        });
        
        // Cancel button sets the result to null and closes the dialog
        cancelButton.addActionListener(e -> {
            selectedFont = null;
            dispose();
        });
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        // Add all components to the dialog
        setLayout(new BorderLayout(5, 5));
        add(listPanel, BorderLayout.CENTER);
        add(previewField, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Size the dialog and center it on the parent window
        pack();
        setLocationRelativeTo(parent);
    }
    
    /**
     * Updates the preview text field to show the currently selected font
     */
    private void updatePreview() {
        Font font = getSelectedFont();
        if (font != null) {
            previewField.setFont(font);
        }
    }
    
    /**
     * Creates a Font object based on the current selections
     * 
     * @return A new Font with the selected properties
     */
    private Font getSelectedFont() {
        String fontName = fontNameList.getSelectedValue();
        int fontStyle = fontStyleList.getSelectedIndex();
        int fontSize = Integer.parseInt(fontSizeList.getSelectedValue());
        
        return new Font(fontName, fontStyle, fontSize);
    }
    
    /**
     * Static method to show the font chooser dialog
     * This is the main method that other classes should call
     * 
     * @param parent      The parent component
     * @param title       The title for the dialog
     * @param initialFont The font to show initially
     * @return The selected font, or null if canceled
     */
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