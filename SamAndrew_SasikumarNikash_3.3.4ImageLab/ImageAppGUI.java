import java.awt.*;
import java.io.File;
import javax.swing.*;

/**
 * ImageAppGUI - Interactive image manipulation with GUI controls
 * Features: Dropdown menus for image selection, rotation buttons, color effects
 */
public class ImageAppGUI extends JFrame {
    
    private Picture currentPicture;
    private String currentBackgroundImage;
    private String currentOverlayImage;
    private PictureFrame pictureFrame;
    private int bgRotationAngle = 0;
    private int overlayRotationAngle = 0;
    
    private String libFolderPath = "lib";  // Store the actual path where lib folder was found
    private String lib2FolderPath = "lib2"; // Store the actual path where lib2 folder was found
    
    private JComboBox<String> backgroundDropdown;
    private JComboBox<String> overlayDropdown;
    private JComboBox<String> bgColorEffectDropdown;
    private JComboBox<String> overlayColorEffectDropdown;
    private JComboBox<String> bgRotateDropdown;
    private JComboBox<String> overlayRotateDropdown;
    private JButton applyButton;
    private JLabel imageLabel;
    private JLabel rowLabel;
    private JLabel colLabel;
    private JLabel rgbLabel;
    private JLabel colorSwatchLabel;
    
    public ImageAppGUI() {
        setTitle("ImageApp - Interactive Image Manipulation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLayout(new BorderLayout());
        
        // setup control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(8, 2, 10, 10));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Background image dropdown
        controlPanel.add(new JLabel("Background Image (lib):"));
        backgroundDropdown = new JComboBox<>();
        backgroundDropdown.addItem("None");
        libFolderPath = loadImagesFromFolder("lib", backgroundDropdown);
        backgroundDropdown.setSelectedItem("beach.jpg");
        controlPanel.add(backgroundDropdown);
        
        // Background color effect dropdown
        controlPanel.add(new JLabel("Background Color Effect:"));
        bgColorEffectDropdown = new JComboBox<>();
        bgColorEffectDropdown.addItem("None");
        bgColorEffectDropdown.addItem("Recolor (BRG)");
        bgColorEffectDropdown.addItem("Negative");
        bgColorEffectDropdown.addItem("Grayscale");
        controlPanel.add(bgColorEffectDropdown);
        
        // Background rotation dropdown
        controlPanel.add(new JLabel("Background Rotation:"));
        bgRotateDropdown = new JComboBox<>();
        bgRotateDropdown.addItem("None (0°)");
        bgRotateDropdown.addItem("90° Clockwise");
        bgRotateDropdown.addItem("90° Counter-Clockwise");
        bgRotateDropdown.addItem("180° Flip");
        controlPanel.add(bgRotateDropdown);
        
        // Overlay image dropdown
        controlPanel.add(new JLabel("Overlay Image (lib2):"));
        overlayDropdown = new JComboBox<>();
        overlayDropdown.addItem("None");
        lib2FolderPath = loadImagesFromFolder("lib2", overlayDropdown);
        controlPanel.add(overlayDropdown);
        
        // Overlay color effect dropdown
        controlPanel.add(new JLabel("Overlay Color Effect:"));
        overlayColorEffectDropdown = new JComboBox<>();
        overlayColorEffectDropdown.addItem("None");
        overlayColorEffectDropdown.addItem("Recolor (BRG)");
        overlayColorEffectDropdown.addItem("Negative");
        overlayColorEffectDropdown.addItem("Grayscale");
        controlPanel.add(overlayColorEffectDropdown);
        
        // Overlay rotation dropdown
        controlPanel.add(new JLabel("Overlay Rotation:"));
        overlayRotateDropdown = new JComboBox<>();
        overlayRotateDropdown.addItem("None (0°)");
        overlayRotateDropdown.addItem("90° Clockwise");
        overlayRotateDropdown.addItem("90° Counter-Clockwise");
        overlayRotateDropdown.addItem("180° Flip");
        controlPanel.add(overlayRotateDropdown);
        
        // Apply button
        controlPanel.add(new JLabel(""));
        applyButton = new JButton("Apply Changes");
        applyButton.addActionListener(e -> applyChanges());
        controlPanel.add(applyButton);
        
        // Image display area
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        imageLabel.setBorder(BorderFactory.createTitledBorder("Modified Image"));
        
        // Add mouse motion listener for color tracking
        imageLabel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                updateColorInfo(evt.getX(), evt.getY());
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(imageLabel);
        scrollPane.setPreferredSize(new Dimension(800, 600));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // Create zoom/color info panel
        JPanel zoomPanel = createZoomPanel();
        
        // Create a panel for controls (west side)
        JPanel westPanel = new JPanel(new BorderLayout());
        westPanel.add(controlPanel, BorderLayout.NORTH);
        westPanel.add(zoomPanel, BorderLayout.CENTER);
        
        // Add components to frame
        add(westPanel, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);
        
        setVisible(true);
        
        // Load default image
        applyChanges();
    }
    
    private String loadImagesFromFolder(String folderPath, JComboBox<String> dropdown) {
        File folder = null;
        String foundPath = folderPath;  // Default to original path
        
        // Try multiple possible paths
        String[] pathsToTry = {
            folderPath,                          // Direct path
            "./" + folderPath,                   // Current directory
            "../" + folderPath,                  // Parent directory
            "SamAndrew_SasikumarNikash_3.3.4ImageLab/" + folderPath  // With parent folder
        };
        
        for (String path : pathsToTry) {
            File testFolder = new File(path);
            if (testFolder.exists() && testFolder.isDirectory()) {
                folder = testFolder;
                foundPath = path;  // Store the path that worked
                break;
            }
        }
        
        if (folder != null && folder.exists() && folder.isDirectory()) {
            // get jpg and png files
            File[] files = folder.listFiles((dir, name) -> 
                name.toLowerCase().endsWith(".jpg") || 
                name.toLowerCase().endsWith(".png") ||
                name.toLowerCase().endsWith(".jpeg"));
            
            if (files != null && files.length > 0) {
                // Sort files alphabetically for better UX
                java.util.Arrays.sort(files, (f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));
                for (File file : files) {
                    dropdown.addItem(file.getName());
                }
                System.out.println("Loaded " + files.length + " images from " + folder.getAbsolutePath());
            } else {
                System.err.println("Warning: No image files found in " + folder.getAbsolutePath());
            }
        } else {
            System.err.println("Warning: Folder not found: " + folderPath);
            System.err.println("Current working directory: " + System.getProperty("user.dir"));
            // Try to list what's in current directory for debugging
            File currentDir = new File(".");
            if (currentDir.exists()) {
                System.err.println("Files in current directory: ");
                File[] dirFiles = currentDir.listFiles();
                if (dirFiles != null) {
                    for (File f : dirFiles) {
                        if (f.isDirectory()) {
                            System.err.println("  DIR: " + f.getName());
                        }
                    }
                }
            }
        }
        
        return foundPath;  // Return the path that was found (or original if not found)
    }
    
    
    private void applyChanges() {
        String background = (String) backgroundDropdown.getSelectedItem();
        String overlay = (String) overlayDropdown.getSelectedItem();
        String bgColorEffect = (String) bgColorEffectDropdown.getSelectedItem();
        String overlayColorEffect = (String) overlayColorEffectDropdown.getSelectedItem();
        String bgRotation = (String) bgRotateDropdown.getSelectedItem();
        String overlayRotation = (String) overlayRotateDropdown.getSelectedItem();
        
        if (background == null || background.equals("None")) {
            return;
        }
        
        try {
            // Load background image using the actual folder path that was found
            String backgroundPath = libFolderPath + "/" + background;
            // Normalize path separators for the current OS
            backgroundPath = backgroundPath.replace("\\", "/");
            currentBackgroundImage = backgroundPath;
            Picture workingPicture = new Picture(currentBackgroundImage);
            Pixel[][] pixels = workingPicture.getPixels2D();
            
            // Apply background color effect
            if (bgColorEffect != null && !bgColorEffect.equals("None")) {
                applyColorEffect(pixels, bgColorEffect);
            }
            
            // Apply background rotation
            int bgRotationAngle = getRotationAngle(bgRotation);
            if (bgRotationAngle != 0) {
                pixels = applyRotation(pixels, bgRotationAngle);
                workingPicture = createPictureFromPixels(pixels);
            }
            
            // Apply overlay using the actual folder path that was found
            if (overlay != null && !overlay.equals("None")) {
                String overlayPath = lib2FolderPath + "/" + overlay;
                // Normalize path separators for the current OS
                overlayPath = overlayPath.replace("\\", "/");
                currentOverlayImage = overlayPath;
                int overlayRotationAngle = getRotationAngle(overlayRotation);
                applyOverlayWithEffects(workingPicture, currentOverlayImage, overlayColorEffect, overlayRotationAngle);
            }
            
            // Display the result in the UI
            currentPicture = workingPicture;
            updateImageDisplay(workingPicture);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void updateImageDisplay(Picture picture) {
        if (picture != null) {
            Image img = picture.getImage();
            if (img != null) {
                ImageIcon icon = new ImageIcon(img);
                imageLabel.setIcon(icon);
                imageLabel.revalidate();
                imageLabel.repaint();
            }
        }
    }
    
    private JPanel createZoomPanel() {
        JPanel zoomPanel = new JPanel();
        zoomPanel.setBorder(BorderFactory.createTitledBorder("Zoom"));
        zoomPanel.setLayout(new BoxLayout(zoomPanel, BoxLayout.Y_AXIS));
        zoomPanel.setPreferredSize(new Dimension(250, 200));
        
        // Row input
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rowPanel.add(new JLabel("Row:"));
        rowLabel = new JLabel("0");
        rowLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        rowLabel.setPreferredSize(new Dimension(80, 20));
        rowPanel.add(rowLabel);
        zoomPanel.add(rowPanel);
        
        // Column input
        JPanel colPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        colPanel.add(new JLabel("Column:"));
        colLabel = new JLabel("0");
        colLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        colLabel.setPreferredSize(new Dimension(80, 20));
        colPanel.add(colLabel);
        zoomPanel.add(colPanel);
        
        // RGB values
        rgbLabel = new JLabel("R: 0 G: 0 B: 0");
        rgbLabel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
        zoomPanel.add(rgbLabel);
        
        // Color swatch
        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        colorPanel.add(new JLabel("Color at location:"));
        colorSwatchLabel = new JLabel();
        colorSwatchLabel.setOpaque(true);
        colorSwatchLabel.setBackground(Color.BLACK);
        colorSwatchLabel.setPreferredSize(new Dimension(30, 30));
        colorSwatchLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        colorPanel.add(colorSwatchLabel);
        zoomPanel.add(colorPanel);
        
        return zoomPanel;
    }
    
    private void updateColorInfo(int mouseX, int mouseY) {
        if (currentPicture == null) {
            return;
        }
        
        try {
            // Get the icon bounds to calculate actual image position
            ImageIcon icon = (ImageIcon) imageLabel.getIcon();
            if (icon == null) {
                return;
            }
            
            // Get label size and icon size
            int labelWidth = imageLabel.getWidth();
            int labelHeight = imageLabel.getHeight();
            int iconWidth = icon.getIconWidth();
            int iconHeight = icon.getIconHeight();
            
            // Calculate scale factors (image may be scaled to fit label)
            double scaleX = (double) iconWidth / Math.max(labelWidth, 1);
            double scaleY = (double) iconHeight / Math.max(labelHeight, 1);
            
            // Account for centered/scaled image - calculate actual image position
            // ImageIcon scales the image to fit the label while maintaining aspect ratio
            int imageX = (int) (mouseX * scaleX);
            int imageY = (int) (mouseY * scaleY);
            
            // Get picture dimensions
            int pictureWidth = currentPicture.getWidth();
            int pictureHeight = currentPicture.getHeight();
            
            // Clamp coordinates to valid range
            if (imageX >= 0 && imageX < pictureWidth && imageY >= 0 && imageY < pictureHeight) {
                // Get pixel color from the picture (x=column, y=row)
                Pixel pixel = currentPicture.getPixel(imageX, imageY);
                Color color = pixel.getColor();
                
                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();
                
                // Update labels (row=y, column=x in image coordinates)
                rowLabel.setText(String.valueOf(imageY));
                colLabel.setText(String.valueOf(imageX));
                rgbLabel.setText("R: " + r + " G: " + g + " B: " + b);
                colorSwatchLabel.setBackground(color);
            } else {
                // Out of bounds
                rowLabel.setText("-");
                colLabel.setText("-");
                rgbLabel.setText("R: - G: - B: -");
                colorSwatchLabel.setBackground(Color.GRAY);
            }
        } catch (Exception e) {
            // Ignore errors, just don't update
        }
    }
    
    private int getRotationAngle(String rotationChoice) {
        if (rotationChoice == null || rotationChoice.equals("None (0°)")) {
            return 0;
        } else if (rotationChoice.equals("90° Clockwise")) {
            return 270; // 90 clockwise = 270 counter-clockwise
        } else if (rotationChoice.equals("90° Counter-Clockwise")) {
            return 90;
        } else if (rotationChoice.equals("180° Flip")) {
            return 180;
        }
        return 0;
    }
    
    private void applyColorEffect(Pixel[][] pixels, String effect) {
        // loop through all pixels
        for (int row = 0; row < pixels.length; row++) {
            for (int col = 0; col < pixels[0].length; col++) {
                Color originalColor = pixels[row][col].getColor();
                int r = originalColor.getRed();
                int g = originalColor.getGreen();
                int b = originalColor.getBlue();
                
                Color newColor;
                if (effect.equals("Recolor (BRG)")) {
                    newColor = new Color(b, r, g);
                } else if (effect.equals("Negative")) {
                    newColor = new Color(255 - r, 255 - g, 255 - b);
                } else if (effect.equals("Grayscale")) {
                    int avg = (r + g + b) / 3;
                    newColor = new Color(avg, avg, avg);
                } else {
                    newColor = originalColor;
                }
                
                pixels[row][col].setColor(newColor);
            }
        }
    }
    
    private Pixel[][] applyRotation(Pixel[][] originalPixels, int angle) {
        int originalHeight = originalPixels.length;
        int originalWidth = originalPixels[0].length;
        
        // For 90 and 270 degree rotations, swap dimensions to prevent white edges
        int destHeight, destWidth;
        if (angle == 90 || angle == 270) {
            destHeight = originalWidth;  // Swapped
            destWidth = originalHeight;  // Swapped
        } else {
            destHeight = originalHeight;
            destWidth = originalWidth;
        }
        
        // Validate angle
        if (angle != 90 && angle != 180 && angle != 270) {
            return originalPixels;
        }
        
        Picture rotatedPicture = new Picture(destHeight, destWidth);
        Pixel[][] rotatedPixels = rotatedPicture.getPixels2D();
        
        // Rotate around center of original image
        double centerRow = originalHeight / 2.0;
        double centerCol = originalWidth / 2.0;
        
        // For 90/270 rotations, use destination center for mapping
        double destCenterRow = destHeight / 2.0;
        double destCenterCol = destWidth / 2.0;
        
        for (int row = 0; row < destHeight; row++) {
            for (int col = 0; col < destWidth; col++) {
                // Get coordinates relative to destination center
                double relRow = row - destCenterRow;
                double relCol = col - destCenterCol;
                
                // Apply inverse rotation to find source pixel
                // For rotation, we need inverse matrix to map destination -> source
                Vector1by2 destPos = new Vector1by2((int)Math.round(relCol), (int)Math.round(relRow));
                
                // For 90°: inverse is 270°, for 270°: inverse is 90°, for 180°: inverse is 180°
                Matrix2by2 invMatrix;
                if (angle == 90) {
                    invMatrix = Matrix2by2.rotation270(); // Inverse of 90° is 270°
                } else if (angle == 270) {
                    invMatrix = Matrix2by2.rotation90();  // Inverse of 270° is 90°
                } else {
                    invMatrix = Matrix2by2.rotation180(); // 180° is its own inverse
                }
                
                Vector1by2 sourcePos = Vector1by2.multiply(destPos, invMatrix);
                
                int sourceCol = (int)Math.round(sourcePos.getElement1() + centerCol);
                int sourceRow = (int)Math.round(sourcePos.getElement2() + centerRow);
                
                // Check bounds and copy pixel - use clamping to prevent white edges
                int clampedRow = Math.max(0, Math.min(originalHeight - 1, sourceRow));
                int clampedCol = Math.max(0, Math.min(originalWidth - 1, sourceCol));
                rotatedPixels[row][col].setColor(originalPixels[clampedRow][clampedCol].getColor());
            }
        }
        
        return rotatedPixels;
    }
    
    private void applyOverlayWithEffects(Picture basePicture, String overlayPath, String colorEffect, int rotation) {
        Picture overlayPicture = new Picture(overlayPath);
        Pixel[][] overlayPixels = overlayPicture.getPixels2D();
        
        // apply effects to overlay
        if (colorEffect != null && !colorEffect.equals("None")) {
            applyColorEffect(overlayPixels, colorEffect);
        }
        
        if (rotation != 0) {
            overlayPixels = applyRotation(overlayPixels, rotation);
        }
        
        Pixel[][] basePixels = basePicture.getPixels2D();
        
        int startRow = 50;
        int startCol = 50;
        
        for (int row = 0; row < overlayPixels.length && (startRow + row) < basePixels.length; row++) {
            for (int col = 0; col < overlayPixels[0].length && (startCol + col) < basePixels[0].length; col++) {
                Color overlayColor = overlayPixels[row][col].getColor();
                
                // skip white pixels
                if (overlayColor.getRed() < 250 || 
                    overlayColor.getGreen() < 250 || 
                    overlayColor.getBlue() < 250) {
                    basePixels[startRow + row][startCol + col].setColor(overlayColor);
                }
            }
        }
    }
    
    private Picture createPictureFromPixels(Pixel[][] pixels) {
        int height = pixels.length;
        int width = pixels[0].length;
        Picture picture = new Picture(height, width);
        Pixel[][] newPixels = picture.getPixels2D();
        
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                newPixels[row][col].setColor(pixels[row][col].getColor());
            }
        }
        
        return picture;
    }
    
    public static void main(String[] args) {
        // Run unit tests first
        System.out.println("Running unit tests...\n");
        Vector1by2.runUnitTests();
        Matrix2by2.runUnitTests();
        System.out.println("\nStarting GUI...\n");
        
        SwingUtilities.invokeLater(() -> new ImageAppGUI());
    }
}
