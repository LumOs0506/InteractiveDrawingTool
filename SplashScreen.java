import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/**
 * A splash screen that displays when the application starts up
 * Similar to Adobe Photoshop's splash screen
 */
public class SplashScreen extends JWindow {
    private static final int SPLASH_WIDTH = 500;
    private static final int SPLASH_HEIGHT = 300;
    private static final int DISPLAY_TIME = 3000; // 3 seconds
    
    private JProgressBar progressBar;
    private Timer timer;
    private int progress = 0;
    private float opacity = 0.0f;
    private Timer fadeInTimer;
    private Timer fadeOutTimer;
    
    public SplashScreen() {
        setSize(SPLASH_WIDTH, SPLASH_HEIGHT);
        setLocationRelativeTo(null);
        
        // Create the main panel with a gradient background
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create a gradient background
                GradientPaint gradient = new GradientPaint(0, 0, new Color(40, 40, 40), 
                                                          0, getHeight(), new Color(70, 70, 70));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw a border with rounded corners
                g2d.setColor(new Color(100, 100, 100));
                g2d.setStroke(new BasicStroke(2));
                g2d.draw(new RoundRectangle2D.Float(1, 1, getWidth()-3, getHeight()-3, 15, 15));
            }
        };
        panel.setLayout(new BorderLayout());
        
        // Create the title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 0, 20));
        
        // App title
        JLabel titleLabel = new JLabel("Interactive Drawing Tool");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        
        // App logo/image
        JLabel logoLabel = new JLabel(new ImageIcon(createLogoImage(200, 120)));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(logoLabel, BorderLayout.CENTER);
        
        panel.add(titlePanel, BorderLayout.CENTER);
        
        // Bottom panel for progress bar and version
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        // Version label
        JLabel versionLabel = new JLabel("Version 1.0");
        versionLabel.setForeground(new Color(200, 200, 200));
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        bottomPanel.add(versionLabel, BorderLayout.WEST);
        
        // Progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(false);
        progressBar.setForeground(new Color(64, 136, 230));
        progressBar.setBackground(new Color(50, 50, 50));
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(SPLASH_WIDTH - 40, 5));
        bottomPanel.add(progressBar, BorderLayout.SOUTH);
        
        // Copyright label
        JLabel copyrightLabel = new JLabel("© " + java.time.Year.now().getValue() + " All Rights Reserved");
        copyrightLabel.setForeground(new Color(200, 200, 200));
        copyrightLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        copyrightLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        bottomPanel.add(copyrightLabel, BorderLayout.EAST);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        setContentPane(panel);
        
        // Set window to be translucent if supported
        if (isTranslucencySupported()) {
            setOpacity(0.0f);
        }
    }
    
    private boolean isTranslucencySupported() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        return gd.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.TRANSLUCENT);
    }
    
    /**
     * Create a simple logo image
     */
    private Image createLogoImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw a stylized paint palette
        g2d.setColor(new Color(64, 136, 230));
        g2d.fillOval(30, 20, 80, 80);
        
        g2d.setColor(new Color(230, 64, 64));
        g2d.fillOval(90, 20, 80, 80);
        
        g2d.setColor(new Color(64, 230, 64));
        g2d.fillOval(60, 60, 80, 80);
        
        // Draw a paintbrush
        g2d.setColor(new Color(200, 150, 100));
        g2d.fillRoundRect(120, 30, 60, 10, 5, 5);
        
        g2d.setColor(Color.DARK_GRAY);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(120, 35, 180, 35);
        
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine(180, 35, 200, 55);
        
        g2d.dispose();
        return image;
    }
    
    /**
     * Show the splash screen, update progress, and then fade out
     */
    public void showSplash(final JFrame mainFrame) {
        // Setup fade-in timer
        fadeInTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (opacity < 1.0f) {
                    opacity += 0.1f;
                    if (opacity > 1.0f) opacity = 1.0f;
                    if (isTranslucencySupported()) {
                        setOpacity(opacity);
                    }
                } else {
                    fadeInTimer.stop();
                    startProgressTimer();
                }
            }
        });
        
        // Start fade-in
        setVisible(true);
        fadeInTimer.start();
    }
    
    private void startProgressTimer() {
        // Setup progress timer
        timer = new Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progress += 1;
                progressBar.setValue(progress);
                
                if (progress >= 100) {
                    timer.stop();
                    startFadeOutTimer();
                }
            }
        });
        timer.start();
    }
    
    private void startFadeOutTimer() {
        // Setup fade-out timer
        fadeOutTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (opacity > 0.0f) {
                    opacity -= 0.1f;
                    if (opacity < 0.0f) opacity = 0.0f;
                    if (isTranslucencySupported()) {
                        setOpacity(opacity);
                    }
                } else {
                    fadeOutTimer.stop();
                    dispose();
                    
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