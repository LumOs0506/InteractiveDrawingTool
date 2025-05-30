# Interactive Drawing Tool - Architecture Explanation

## Core Components

### `DrawingApp.java`
**Purpose**: Main application class that creates the user interface.

- Creates the application window with menus, toolbars, and panels
- Manages the user interface interactions
- Handles file operations (open, save)
- Connects all components together
- Entry point with the `main()` method that launches the application
- Shows the splash screen before loading the main interface

**Key Connections**:
- Instantiates `DrawingPanel` which is the central canvas
- Instantiates `LayerPanel` to manage drawing layers
- Uses `JFontChooser` for font selection

### `DrawingPanel.java`
**Purpose**: The main canvas where all drawing happens.

- Manages all drawing operations
- Handles mouse and keyboard interactions
- Maintains the list of layers and shapes
- Implements undo/redo functionality
- Manages selection, moving, and resizing of shapes
- Provides zoom and pan functionality

**Key Connections**:
- Contains instances of `Layer` objects
- Creates different `Shape` objects based on user selection
- Central component that connects shapes with user interactions

### `Shape.java`
**Purpose**: Abstract base class for all drawable shapes.

- Defines common properties (color, coordinates, filled state)
- Defines selection and manipulation functionality
- Provides abstract methods that specific shapes must implement
- Handles selection visualization with handles for resizing

**Key Connections**:
- Extended by specific shape classes (`Rectangle`, `Circle`, `Line`, etc.)
- Used by `DrawingPanel` to track what's being drawn
- Used by `Layer` to store collections of shapes

### `Layer.java`
**Purpose**: Represents a single layer in the drawing.

- Contains a collection of shapes
- Provides visibility control (show/hide)
- Manages the z-order of shapes within the layer
- Handles selection of shapes based on coordinates

**Key Connections**:
- Contains multiple `Shape` objects
- Used by `DrawingPanel` to organize shapes
- Displayed in the `LayerPanel` interface

### `LayerPanel.java`
**Purpose**: UI component that displays and manages layers.

- Shows a list of all layers
- Provides controls to add/remove layers
- Allows changing layer visibility
- Enables reordering of layers

**Key Connections**:
- Manipulates `Layer` objects in the `DrawingPanel`
- Updates the UI when layers change

## Shape Classes

### `Rectangle.java`
**Purpose**: Implements a rectangular shape.

- Draws rectangles with optional fill
- Handles selection and resizing

**Key Connections**:
- Extends `Shape`
- Created by `DrawingPanel` when the user selects the rectangle tool

### `Circle.java`
**Purpose**: Implements a circular shape.

- Draws circles with optional fill
- Handles selection and resizing

**Key Connections**:
- Extends `Shape`
- Created by `DrawingPanel` when the user selects the circle tool

### `Line.java`
**Purpose**: Implements a line shape.

- Draws straight lines
- Handles selection and resizing

**Key Connections**:
- Extends `Shape`
- Created by `DrawingPanel` when the user selects the line tool

### `TextShape.java`
**Purpose**: Implements text as a drawable shape.

- Renders text with specified font and color
- Handles selection and moving

**Key Connections**:
- Extends `Shape`
- Created by `DrawingPanel` when the user uses the text tool

### `FreeDrawing.java`
**Purpose**: Implements freehand drawing.

- Captures mouse movement for sketching
- Stores path points for rendering

**Key Connections**:
- Extends `Shape`
- Created by `DrawingPanel` when the user selects the free drawing tool

### `ImageShape.java`
**Purpose**: Implements an image as a drawable shape.

- Displays imported images on the canvas
- Handles selection, moving, and resizing

**Key Connections**:
- Extends `Shape`
- Created by `DrawingPanel` when the user imports an image

## Utility Classes

### `SplashScreen.java`
**Purpose**: Creates a professional splash screen at application startup.

- Shows logo, title, and progress bar
- Provides fade-in and fade-out animations
- Simulates loading process before launching main application

**Key Connections**:
- Called by `DrawingApp.main()` before creating the main window

### `JFontChooser.java`
**Purpose**: Custom font selection dialog.

- Provides UI for selecting font family, style, and size
- Shows preview of the selected font
- Mimics standard JColorChooser API for consistency

**Key Connections**:
- Used by `DrawingApp` when user wants to change text font

## Program Flow

1. User starts the application via `DrawingApp.main()`
2. `SplashScreen` shows with animation and progress bar
3. Main `DrawingApp` window initializes with:
   - `DrawingPanel` in the center (canvas)
   - Toolbar on the left with drawing tools
   - Properties panel on the right
   - `LayerPanel` on the bottom right
   - Menu bar with file/edit/view options

4. User interactions:
   - Selecting tools changes the `currentShape` in `DrawingPanel`
   - Drawing creates specific shape objects added to current layer
   - Layer operations affect visibility and ordering
   - Selection allows manipulation of existing shapes

5. Changes are tracked in undo/redo stacks for history management
6. Drawings can be saved as PNG images or loaded from files

## Data Flow

1. User input (mouse/keyboard) → `DrawingApp` → `DrawingPanel`
2. `DrawingPanel` creates appropriate `Shape` objects
3. Shapes are stored in `Layer` objects
4. `Layer` objects are managed by both `DrawingPanel` and `LayerPanel`
5. When drawing, `DrawingPanel` loops through all visible layers
6. Each layer draws its shapes in the proper z-order
7. Selection checks go from top layer to bottom to find shapes under the cursor

This architecture follows the Model-View-Controller pattern:
- **Model**: `Shape` and `Layer` classes
- **View**: `DrawingApp` UI components and `LayerPanel`
- **Controller**: `DrawingPanel` handling interactions and state

The design allows for easy extension by adding new shape types while keeping the core drawing and layer management logic separate.
