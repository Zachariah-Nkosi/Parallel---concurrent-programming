//Author: Nkosi Zachariah
//Date: 10 September 2024

package medleySimulation;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class MedleySimulation {

    static final int numTeams = 10; // Total number of teams participating in the simulation

    // Synchronization mechanisms for controlling the start and coordination of threads
    private static CountDownLatch startLatch = new CountDownLatch(1); // Latch to wait for the "Start" button press
    private static final CyclicBarrier teamBarrier = new CyclicBarrier(numTeams); // Barrier to synchronize team actions

    static int frameX = 600; // Width of the application frame
    static int frameY = 800; // Height of the application frame
    static int yLimit = 400; // Y-axis boundary limit for the simulation
    static int max = 5; // Maximum number of swimmers visible on screen at once

    static int gridX = 50; // Number of grid points along the X-axis
    static int gridY = 120; // Number of grid points along the Y-axis

    static SwimTeam[] teams; // Array to hold threads for each team
    static PeopleLocation[] peopleLocations; // Array to keep track of locations of people in the simulation
    static StadiumView stadiumView; // Panel to display the stadium view
    static StadiumGrid stadiumGrid; // Object representing the stadium grid

    static FinishCounter finishLine; // Object to keep track of the race completion
    static CounterDisplay counterDisplay; // Object to display race results

    private static final String COVER_IMAGE_PATH = "C:/Users/Zachariah/Downloads/NKSZAK005(3)/src/medleySimulation/ready.jpg"; // Path to the cover image
    private static final String COVER_SOUND_PATH = "C:/Users/Zachariah/Downloads/NKSZAK005(3)/src/medleySimulation/final.wav"; // Path to the cover page sound
    private static Clip backgroundClip; // Clip object to manage background audio

    // Method to set up the GUI for the simulation
    public static void setupGUI(int frameX, int frameY) {
        JFrame frame = new JFrame("Swim Medley Relay Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(frameX, frameY);
        frame.setLocationRelativeTo(null); // Center the window on the screen

        // Create a card layout to switch between cover page and game panel
        CardLayout cardLayout = new CardLayout();
        JPanel cardPanel = new JPanel(cardLayout);

        JPanel coverPage = createCoverPage(cardPanel, cardLayout); // Create the cover page panel
        JPanel gamePanel = createGamePanel(); // Create the game panel

        // Add panels to the card layout
        cardPanel.add(coverPage, "coverPage");
        cardPanel.add(gamePanel, "gamePanel");

        frame.add(cardPanel); // Add the card panel to the frame
        frame.setVisible(true); // Make the frame visible
    }

    // Method to create the cover page panel
    private static JPanel createCoverPage(JPanel cardPanel, CardLayout cardLayout) {
        // Create a background panel with the cover image
        JPanel coverPage = new BackgroundPanel(COVER_IMAGE_PATH);
        coverPage.setLayout(new BorderLayout());

        // Play the cover page sound
        playSound(COVER_SOUND_PATH);

        // Create and customize the title label
        JLabel titleLabel = new JLabel("", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 24));
        titleLabel.setForeground(new Color(255, 215, 0)); // Gold color
        coverPage.add(titleLabel, BorderLayout.CENTER);

        // Create a panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setOpaque(false); // Make the button panel transparent

        // Create and customize the start button
        JButton startButton = new JButton("Proceed!");
        startButton.setBackground(new Color(34, 139, 34)); // Green color
        startButton.setForeground(Color.WHITE);
        startButton.setFont(new Font("Arial", Font.BOLD, 18));
        startButton.setFocusPainted(false);

        // Create and customize the quit button
        JButton quitButton = new JButton("Quit");
        quitButton.setBackground(new Color(178, 34, 34)); // Red color
        quitButton.setForeground(Color.WHITE);
        quitButton.setFont(new Font("Arial", Font.BOLD, 18));
        quitButton.setFocusPainted(false);

        buttonPanel.add(startButton); // Add start button to the button panel
        buttonPanel.add(quitButton); // Add quit button to the button panel

        coverPage.add(buttonPanel, BorderLayout.SOUTH); // Add button panel to cover page

        // Add action listener for the start button
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "gamePanel"); // Switch to the game panel
            }
        });

        // Add action listener for the quit button
        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // Exit the application
            }
        });

        return coverPage; // Return the cover page panel
    }

    // Method to create the game panel
    private static JPanel createGamePanel() {
        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new BorderLayout(10, 10));
        gamePanel.setBackground(new Color(45, 45, 48)); // Dark background color

        // Create and set up the top panel for race information
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(60, 63, 65));
        infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        infoPanel.setLayout(new GridBagLayout()); // Use GridBagLayout for centering

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        // Create and customize the winner label
        JLabel winnerLabel = new JLabel("Winner: ");
        winnerLabel.setFont(new Font("Verdana", Font.BOLD, 18));
        winnerLabel.setForeground(new Color(255, 215, 0)); // Gold color

        infoPanel.add(winnerLabel, gbc); // Add the winner label to the info panel

        // Create and set up the center panel for stadium view
        stadiumView = new StadiumView(peopleLocations, stadiumGrid);
        stadiumView.setBackground(new Color(30, 30, 30)); // Darker background
        stadiumView.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        // Create and set up the bottom panel for buttons and actions
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(45, 45, 48));
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        buttonPanel.setLayout(new FlowLayout());

        // Create and customize the start button
        JButton startB = new JButton("Start");
        startB.setBackground(new Color(34, 139, 34)); // Green color
        startB.setForeground(Color.WHITE);
        startB.setFocusPainted(false);
        startB.setFont(new Font("Arial", Font.BOLD, 14));

        // Create and customize the quit button
        JButton endB = new JButton("Quit");
        endB.setBackground(new Color(178, 34, 34)); // Red color
        endB.setForeground(Color.WHITE);
        endB.setFocusPainted(false);
        endB.setFont(new Font("Arial", Font.BOLD, 14));

        // Add action listener for the start button
        startB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stopSound(); // Stop any currently playing sound
                // Play the start sound
                startSound("C:/Users/Zachariah/Downloads/NKSZAK005(3)/src/medleySimulation/start.wav");
                startLatch.countDown(); // Release latch to start the race
            }
        });

        // Add action listener for the quit button
        endB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // Exit the application
            }
        });

        buttonPanel.add(startB); // Add start button to the button panel
        buttonPanel.add(endB); // Add quit button to the button panel

        // Create and customize volume and mute buttons
        JButton volumeButton = new JButton(new ImageIcon("src/medleySimulation/volume.png"));
        JButton muteButton = new JButton(new ImageIcon("src/medleySimulation/mute.png"));

        volumeButton.setFocusPainted(false);
        muteButton.setFocusPainted(false);

        // Add action listener for the volume button
        volumeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Implement functionality to increase volume
                System.out.println("Volume Up");
            }
        });

        // Add action listener for the mute button
        muteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Implement functionality to mute or unmute
                System.out.println("Muted");
                stopSound(); // Stop the sound
            }
        });

        // Add volume and mute buttons to the button panel
        buttonPanel.add(volumeButton);
        buttonPanel.add(muteButton);

        // Add panels to the game panel
        gamePanel.add(infoPanel, BorderLayout.NORTH);
        gamePanel.add(stadiumView, BorderLayout.CENTER);
        gamePanel.add(buttonPanel, BorderLayout.SOUTH);

        // Initialize the CounterDisplay and start its thread
        counterDisplay = new CounterDisplay(winnerLabel, finishLine);
        Thread resultsThread = new Thread(counterDisplay);
        resultsThread.start();

        return gamePanel; // Return the game panel
    }

    // Method to play a sound from a file
    private static void playSound(String filePath) {
        try {
            File soundFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(audioStream);
            backgroundClip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    // Method to start a sound from a file
    private static void startSound(String filePath) {
        try {
            File soundFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(audioStream);
            backgroundClip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    // Method to stop any currently playing sound
    private static void stopSound() {
        if (backgroundClip != null && backgroundClip.isRunning()) {
            backgroundClip.stop();
            backgroundClip.close();
        }
    }

    // Main method - initializes and starts the simulation
    public static void main(String[] args) throws InterruptedException {
        finishLine = new FinishCounter(); // Initialize finish line counter

        // Initialize the stadium grid with the given dimensions and number of teams
        try {
            stadiumGrid = new StadiumGrid(gridX, gridY, numTeams, finishLine);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        SwimTeam.stadium = stadiumGrid; // Share the stadium grid with SwimTeam class
        Swimmer.stadium = stadiumGrid; // Share the stadium grid with Swimmer class

        // Initialize array to keep track of all people's locations
        peopleLocations = new PeopleLocation[numTeams * SwimTeam.sizeOfTeam];
        Color[] teamColors = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN, 
                              Color.MAGENTA, Color.ORANGE, Color.PINK, Color.GRAY, Color.DARK_GRAY};

        // Create PeopleLocation instances for each person
        for (int i = 0; i < peopleLocations.length; i++) {
            int teamID = i / SwimTeam.sizeOfTeam;
            peopleLocations[i] = new PeopleLocation(i, teamColors[teamID % teamColors.length]); // Initialize with ID and Color
        }

        teams = new SwimTeam[numTeams]; // Initialize the array for team threads

        // Create and initialize team threads
        for (int i = 0; i < numTeams; i++) {
            teams[i] = new SwimTeam(i, finishLine, peopleLocations, teamBarrier);
        }

        setupGUI(frameX, frameY); // Setup GUI for the simulation

        // Start the stadium view thread to handle rendering
        Thread view = new Thread(stadiumView);
        view.start();

        // Wait until the "Start" button is pressed
        startLatch.await();

        // Start all team threads
        for (int i = 0; i < numTeams; i++) {
            teams[i].start();
        }
    }
}
