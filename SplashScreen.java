import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/**
 * A splash screen that displays when the application starts up
 * This creates a professional-looking loading screen similar to Adobe Photoshop
 * It shows a logo, title, and progress bar with fade-in and fade-out animations
 */
public class SplashScreen extends JWindow {
    // Constants for splash screen size and timing
    private static final int SPLASH_WIDTH = 500;
    private static final int SPLASH_HEIGHT = 300;
    
    // UI components and animation properties
    private JProgressBar progressBar;    // Shows loading progress
    private Timer timer;                 // Controls the progress updates
    private int progress = 0;            // Current progress (0-100)
    private float opacity = 0.0f;        // Current opacity for fade effects
    private Timer fadeInTimer;           // Controls fade-in animation
    private Timer fadeOutTimer;          // Controls fade-out animation
    
    /**
     * Constructor - creates the splash screen with all its visual elements
     */
    public SplashScreen() {
        setSize(SPLASH_WIDTH, SPLASH_HEIGHT);
        setLocationRelativeTo(null);  // Center on screen
        
        // Create the main panel with a gradient background
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create a dark gradient background (dark gray to medium gray)
                GradientPaint gradient = new GradientPaint(0, 0, new Color(40, 40, 40), 
                                                          0, getHeight(), new Color(70, 70, 70));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw a border with rounded corners for a polished look
                g2d.setColor(new Color(100, 100, 100));
                g2d.setStroke(new BasicStroke(2));
                g2d.draw(new RoundRectangle2D.Float(1, 1, getWidth()-3, getHeight()-3, 15, 15));
            }
        };
        panel.setLayout(new BorderLayout());
        
        // Create the title panel (top part of splash screen)
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);  // Make transparent to show gradient background
        titlePanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 0, 20));
        
        // App title - large white text
        JLabel titleLabel = new JLabel("Interactive Drawing Tool");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        
        // App logo/image - centered in the splash screen
        JLabel logoLabel = new JLabel(new ImageIcon(createLogoImage(200, 120)));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(logoLabel, BorderLayout.CENTER);
        
        panel.add(titlePanel, BorderLayout.CENTER);
        
        // Bottom panel for progress bar and version info
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);  // Make transparent to show gradient background
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        // Version label - shown in bottom left
        JLabel versionLabel = new JLabel("Version 1.0");
        versionLabel.setForeground(new Color(200, 200, 200));
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        bottomPanel.add(versionLabel, BorderLayout.WEST);
        
        // Progress bar - shows loading progress at bottom
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(false);  // Don't show percentage text
        progressBar.setForeground(new Color(64, 136, 230));  // Blue progress color
        progressBar.setBackground(new Color(50, 50, 50));    // Dark background
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(SPLASH_WIDTH - 40, 5));
        bottomPanel.add(progressBar, BorderLayout.SOUTH);
        
        // Copyright label - shown in bottom right
        JLabel copyrightLabel = new JLabel("© " + java.time.Year.now().getValue() + " All Rights Reserved");
        copyrightLabel.setForeground(new Color(200, 200, 200));
        copyrightLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        copyrightLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        bottomPanel.add(copyrightLabel, BorderLayout.EAST);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        setContentPane(panel);
        
        // Set window to be translucent if the system supports it
        // This enables the fade-in/fade-out effects
        if (isTranslucencySupported()) {
            setOpacity(0.0f);
        }
    }
    
    /**
     * Checks if the system supports translucent windows
     * This is needed for the fade effects to work
     */
    private boolean isTranslucencySupported() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        return gd.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.TRANSLUCENT);
    }
    
    /**
     * Creates a simple logo image for the splash screen
     * This draws colorful circles representing a paint palette and a paintbrush
     * 
     * @param width Width of the logo image
     * @param height Height of the logo image
     * @return The generated logo image
     */
    private Image createLogoImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw a stylized paint palette with three overlapping colored circles
        g2d.setColor(new Color(64, 136, 230));  // Blue circle
        g2d.fillOval(30, 20, 80, 80);
        
        g2d.setColor(new Color(230, 64, 64));   // Red circle
        g2d.fillOval(90, 20, 80, 80);
        
        g2d.setColor(new Color(64, 230, 64));   // Green circle
        g2d.fillOval(60, 60, 80, 80);
        
        // Draw a paintbrush handle
        g2d.setColor(new Color(200, 150, 100));  // Wooden handle color
        g2d.fillRoundRect(120, 30, 60, 10, 5, 5);
        
        g2d.setColor(Color.DARK_GRAY);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(120, 35, 180, 35);
        
        // Draw the paintbrush bristles
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine(180, 35, 200, 55);
        
        g2d.dispose();
        return image;
    }
    
    /**
     * Shows the splash screen with animations
     * First fades in, then shows progress, then fades out and launches the main app
     * 
     * @param mainFrame The main application frame to show after splash screen
     */
    public void showSplash(final JFrame mainFrame) {
        // Setup fade-in timer - gradually increases opacity from 0 to 1
        fadeInTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (opacity < 1.0f) {
                    opacity += 0.1f;  // Increase opacity by 10% each step
                    if (opacity > 1.0f) opacity = 1.0f;
                    if (isTranslucencySupported()) {
                        setOpacity(opacity);
                    }
                } else {
                    fadeInTimer.stop();
                    startProgressTimer();  // Start showing progress after fade-in completes
                }
            }
        });
        
        // Start fade-in animation
        setVisible(true);
        fadeInTimer.start();
    }
    
    /**
     * Starts the progress timer to simulate loading
     * Updates the progress bar from 0% to 100%
     */
    private void startProgressTimer() {
        // Setup progress timer - updates progress bar every 30ms
        timer = new Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progress += 1;  // Increment progress by 1%
                progressBar.setValue(progress);
                
                if (progress >= 100) {
                    timer.stop();
                    startFadeOutTimer();  // Start fade-out when progress reaches 100%
                }
            }
        });
        timer.start();
    }
    
    /**
     * Starts the fade-out timer after loading completes
     * Gradually decreases opacity from 1 to 0, then launches the main app
     */
    private void startFadeOutTimer() {
        // Setup fade-out timer - gradually decreases opacity from 1 to 0
        fadeOutTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (opacity > 0.0f) {
                    opacity -= 0.1f;  // Decrease opacity by 10% each step
                    if (opacity < 0.0f) opacity = 0.0f;
                    if (isTranslucencySupported()) {
                        setOpacity(opacity);
                    }
                } else {
                    fadeOutTimer.stop();
                    dispose();  // Remove splash screen from memory
                    
                    // Show the main application frame
                    SwingUtilities.invokeLater(() -> {
                        DrawingApp app = new DrawingApp();
                        app.setVisible(true);
                    });
                }
            }
        });
        fadeOutTimer.start();
    }
} 