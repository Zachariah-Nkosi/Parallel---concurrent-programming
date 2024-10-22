//Author: Nkosi Zachariah
//Date: 10 September 2024

package medleySimulation;

public class FinishCounter {
    private boolean firstAcrossLine; // Flag to track if 1st place has been taken
    private int winner; // ID of the swimmer who finished in 1st place
    private int winningTeam; // ID of the team of the swimmer who finished in 1st place

    // Constructor to initialize race status and results
    public FinishCounter() {
        firstAcrossLine = true; // Initial status: no swimmer has finished 1st yet
        winner = -1; // No swimmer has won yet
        winningTeam = -1; // No team has won yet
    }

    // This method is called by a swimmer when they finish the race in 1st place
    public synchronized void finishRace(int swimmer, int team) {
        if (firstAcrossLine) {
            firstAcrossLine = false;
            winner = swimmer;
            winningTeam = team;
        }
    }

    // Method to check if the race has been won (i.e., if 1st place is taken)
    public boolean isRaceWon() {
        return !firstAcrossLine;
    }

    // Getter for the ID of the swimmer who finished 1st
    public int getWinner() {
        return winner;
    }

    // Getter for the ID of the winning team for 1st place
    public int getWinningTeam() {
        return winningTeam;
    }
}
