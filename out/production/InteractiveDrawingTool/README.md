# Interactive Drawing Tool

A Java-based drawing application with layer support, similar to basic image editing software. This application allows users to create and edit drawings with multiple layers, various shapes, and text.

## Features

### Drawing Tools
- **Line**: Draw straight lines
- **Rectangle**: Draw rectangles (filled or outlined)
- **Circle**: Draw circles (filled or outlined)
- **Text**: Add text with customizable font
- **Image**: Import and place images

### Layer Management
- Create multiple layers
- Show/hide layers using checkboxes
- Reorder layers using Up/Down buttons
- Delete layers (maintains at least one layer)
- Layer visibility toggle

### File Operations
- **New**: Clear the canvas and start fresh
- **Open**: Open PNG images
- **Save**: Save drawings as PNG files
- **Exit**: Close the application

### Edit Operations
- **Undo**: Revert the last action
- **Redo**: Restore previously undone actions
- **Delete**: Remove selected shapes (using Backspace or Delete key)

### Format Options
- **Color**: Choose colors for shapes and text
- **Font**: Select font style, size, and type for text
- **Fill**: Toggle fill mode for shapes (checkbox in layer panel)

### Shape Manipulation
- Select shapes by clicking
- Move shapes by dragging
- Resize shapes using the selection handles
- Delete selected shapes

## How to Use

### Basic Drawing
1. Select a shape tool from the toolbar (Line, Rectangle, Circle)
2. Click and drag on the canvas to draw
3. Use the fill checkbox in the layer panel to toggle filled/outlined shapes

### Adding Text
1. Select the Text tool from the toolbar
2. Either:
   - Type text in the text field and press Enter, then click where you want the text
   - Or click where you want the text and enter it in the dialog that appears

### Working with Layers
1. Use the layer panel on the right side of the window
2. Click the "Add Layer" button to create new layers
3. Use checkboxes to show/hide layers
4. Use Up/Down buttons to reorder layers
5. Select a layer to make it active for drawing

### Managing Shapes
1. Click a shape to select it
2. Drag the shape to move it
3. Use the corner handles to resize
4. Press Backspace or Delete to remove selected shapes

### Saving and Loading
1. Use File > Save to save your drawing as a PNG file
2. Use File > Open to load a PNG image
3. Use File > New to start a fresh canvas

## Requirements
- Java Runtime Environment (JRE) 8 or higher
- Swing and AWT libraries (included in standard Java installation)

## Building and Running
1. Compile the Java files:
   ```bash
   javac *.java
   ```
2. Run the application:
   ```bash
   java DrawingApp
   ```

## Keyboard Shortcuts
- **Backspace/Delete**: Delete selected shape
- **Enter** (in text field): Set text for text tool

## Tips
- Use layers to organize different elements of your drawing
- The most recently added layer appears on top
- You can always undo/redo your actions
- Save your work regularly
- Use the fill checkbox to create solid shapes
- Select a layer before drawing to ensure shapes appear on the correct layer 
