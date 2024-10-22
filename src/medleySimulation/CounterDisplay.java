//Author: Nkosi Zachariah
//Date: 10 September 2024

package medleySimulation;

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

// Thread class to update the display of text fields
public class CounterDisplay implements Runnable {

    private final FinishCounter results;
    private final JLabel winnerLabel;
    private volatile boolean running = true; // Flag to control the thread loop

    // Constructor to initialize labels and finish counter
    public CounterDisplay(JLabel winnerLabel, FinishCounter results) {
        this.winnerLabel = winnerLabel;
        this.results = results;
    }

    @Override
    public void run() {
        while (running) {
            try {
                // Check if the race is won and update the labels
                SwingUtilities.invokeLater(() -> {
                    if (results.isRaceWon()) {
                        winnerLabel.setForeground(Color.RED);
                        winnerLabel.setText("1st Winning Team: " + results.getWinningTeam());
                    } else {
                        winnerLabel.setForeground(Color.BLACK);
                        winnerLabel.setText("Winner: ");
                    }
                });

                // Sleep for a short period to reduce CPU usage
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore the interrupted status
                running = false; // Stop the loop if interrupted
            }
        }
    }

    // Method to stop the thread gracefully
    public void stop() {
        running = false;
    }
}
