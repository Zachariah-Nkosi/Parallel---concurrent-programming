package medleySimulation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.AlphaComposite;
import javax.swing.JPanel;

public class StadiumView extends JPanel implements Runnable {

    private static final long serialVersionUID = 1L;
    private PeopleLocation[] pplLocations; // Array of the locations of the patrons
    private int numPeople; // Total number in the simulation

    private int wIncr; // Width of each block
    private int hIncr; // Height of each block
    private int maxY; // Maximum Y for the grid
    private int maxX; // Maximum X for the grid
    private int endPool; // Where pool ends, starting block position.

    private final int xBorder = 5;
    private final int yBorder = 5;

    private static Color[] laneColours = { Color.green,
        Color.blue, Color.blue, Color.blue,
        Color.yellow, Color.yellow, Color.yellow,
        Color.blue, Color.blue, Color.blue,
        Color.green };
    private final Color water = new Color(200, 255, 255, 100); // More translucent water color
    private final Color concrete = new Color(255, 255, 255, 100); // More translucent concrete color
    private final Image backgroundImage; // Background image
    private final float overlayAlpha = 0.5f; // Alpha value for the dark overlay
    StadiumGrid grid; // Shared grid

    StadiumView(PeopleLocation[] people, StadiumGrid grid) { // Constructor
        this.pplLocations = people;
        numPeople = people.length;
        this.grid = grid;
        this.maxY = grid.getMaxY();
        this.maxX = grid.getMaxX();
        this.endPool = StadiumGrid.start_y;

        // Load the background image
        backgroundImage = Toolkit.getDefaultToolkit().getImage("src/medleySimulation/cover3.jpg");

        // Ensure the panel is transparent
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Always call superclass method first
        Graphics2D g2 = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();
        wIncr = width / (maxX + xBorder * 2);
        hIncr = height / (maxY + yBorder * 2);

        // Draw the background image
        g2.drawImage(backgroundImage, 0, 0, width, height, this);

        // Create a dark overlay
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, overlayAlpha));
        g2.setColor(new Color(0, 0, 0)); // Black color for the overlay
        g2.fillRect(0, 0, width, height); // Fill the entire panel with the dark overlay

        // Reset the composite to default
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        // Draw pool and concrete with transparency
        g2.setColor(water);
        g2.fillRect(xBorder * wIncr, yBorder * hIncr, (maxX) * wIncr, (endPool) * hIncr); // Water
        g2.setColor(concrete);
        g2.fillRect(xBorder * wIncr, (yBorder + endPool) * hIncr, (maxX) * wIncr, (maxY - endPool) * hIncr); // Concrete

        // Draw top and bottom edges
        g2.setColor(Color.lightGray);
        g2.fillRect(wIncr * xBorder, (yBorder - 1) * hIncr, wIncr * (maxX), hIncr);
        g2.fillRect(wIncr * xBorder, (endPool + yBorder) * hIncr, wIncr * (maxX), hIncr);
        g2.setColor(Color.black);

        // Draw lane lines
        g2.setStroke(new BasicStroke(3));
        int lane = 0, i = 0;
        for (i = 0; i < maxX; i += 5) { // Columns
            g2.setColor(laneColours[lane]);
            lane++;
            g2.drawLine((i + xBorder) * wIncr, hIncr * yBorder, (i + xBorder) * wIncr, (endPool + yBorder) * hIncr); // Draw lane lines
            g2.setColor(Color.white);
            g2.fillRect((i + 2 + xBorder) * wIncr, (endPool + yBorder) * hIncr, wIncr, hIncr); // Draw starting blocks outside pool
        }
        g2.setColor(laneColours[lane]);
        g2.drawLine((i + xBorder) * wIncr, hIncr * yBorder, (i + xBorder) * wIncr, (endPool + yBorder) * hIncr); // Draw last lane line

        // Draw the ovals representing people in middle of grid block
        int x, y;
        g2.setFont(new Font("Helvetica", Font.BOLD, hIncr / 2));

        // Patrons
        for (i = 0; i < numPeople; i++) {
            if (pplLocations[i].inPool()) {
                g2.setColor(pplLocations[i].getColor());
                x = (pplLocations[i].getX() + xBorder) * wIncr;
                y = (pplLocations[i].getY() + yBorder) * hIncr;
                g2.fillOval(x + wIncr, y, wIncr, hIncr);
            }
        }
    }

    public int getEndPool() {
        return endPool;
    }

    public void setEndPool(int endPool) {
        this.endPool = endPool;
    }

    @Override
    public void run() {
        while (true) {
            repaint();
            try {
                Thread.sleep(100); // Adjust sleep time as necessary
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
