//Author: Nkosi Zachariah
//Date: 10 September 2024
// Class representing a swimmer participating in a race
// Swimmers can use one of four possible strokes: Backstroke, Breaststroke, Butterfly, or Freestyle
package medleySimulation;

import java.awt.Color;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Swimmer extends Thread {
	// Shared resources for all swimmers
	public static StadiumGrid stadium; // The stadium grid shared among all swimmers
	private FinishCounter finish; // Object to keep track of race results

	// Variables for each swimmer
	private GridBlock currentBlock; // Current position of the swimmer on the grid
	private Random rand; // Random number generator for simulating delays
	private int movingSpeed; // Swimmer's speed
	private CyclicBarrier barrier; // Barrier to synchronize swimmers at the start of the race
	private PeopleLocation myLocation; // Object representing the swimmer's location
	private int ID; // Swimmer's unique ID
	private int team; // Team ID that the swimmer belongs to
	private GridBlock start; // Starting position of the swimmer

	// Enum defining different swim strokes and their properties
	public enum SwimStroke {
		Backstroke(1, 2.5, Color.black),
		Breaststroke(2, 2.1, new Color(255, 102, 0)),
		Butterfly(3, 2.55, Color.magenta),
		Freestyle(4, 2.8, Color.red);

		private final double strokeTime; // Time taken for the stroke
		private final int order; // Order of the stroke in the race
		private final Color colour; // Color representing the stroke

		SwimStroke(int order, double sT, Color c) {
			this.strokeTime = sT; // Time taken for one lap in this stroke
			this.order = order; // Order of the stroke in the race
			this.colour = c; // Color representing the stroke
		}

		// Get the order of the stroke
		public int getOrder() {
			return order;
		}

		// Get the color representing the stroke
		public Color getColour() {
			return colour;
		}
	}

	private final SwimStroke swimStroke; // The stroke style for this swimmer

	// Constructor to initialize a swimmer
	Swimmer(int ID, int t, PeopleLocation loc, FinishCounter f, int speed,
			SwimStroke s, CyclicBarrier br) {
		this.swimStroke = s; // Assign stroke type to the swimmer
		this.ID = ID; // Set swimmer ID
		this.movingSpeed = speed; // Assign swimmer's speed
		this.myLocation = loc; // Set location object for swimmer
		this.team = t; // Assign swimmer's team ID
		this.barrier = br; // Set barrier for synchronization
		this.start = stadium.returnStartingBlock(team); // Get starting block position for the swimmer's team
		this.finish = f; // Assign the finish counter object

		rand = new Random(); // Initialize random number generator
		stadium.inside[ID] = false; // Initially, swimmer is not inside the stadium
	}

	// Getter for swimmer's current X coordinate
	public int getX() {
		return currentBlock.getX();
	}

	// Getter for swimmer's current Y coordinate
	public int getY() {
		return currentBlock.getY();
	}

	// Getter for swimmer's speed
	public int getSpeed() {
		return movingSpeed;
	}

	// Getter for swimmer's stroke type
	public SwimStroke getSwimStroke() {
		return swimStroke;
	}

	// Swimmer enters the stadium
	public void enterStadium() throws InterruptedException {
		currentBlock = stadium.enterStadium(myLocation); // Get the entry point in the stadium grid
		sleep(200); // Pause to simulate looking around after entering
	}

	// Swimmer moves to the starting blocks
	public void goToStartingBlocks() throws InterruptedException {
		int x_st = start.getX(); // X-coordinate of the starting block
		int y_st = start.getY(); // Y-coordinate of the starting block

		// Move towards the starting block until swimmer reaches it
		while (currentBlock != start) {
			sleep(movingSpeed * 3); // Move slowly towards the starting block
			currentBlock = stadium.moveTowards(currentBlock, x_st, y_st, myLocation); // Update current position
		}
		System.out.println(
				"-----------Thread " + this.ID + " at start " + currentBlock.getX() + " " + currentBlock.getY());
	}

	// Swimmer dives into the pool
	private void dive() throws InterruptedException {
		int x = currentBlock.getX();
		int y = currentBlock.getY();
		currentBlock = stadium.jumpTo(currentBlock, x, y - 2, myLocation); // Move swimmer into the pool
	}

	// Swimmer swims to the end and back
	private void swimRace() throws InterruptedException {
		int x = currentBlock.getX(); // X-coordinate remains constant while swimming

		// Swim forward until reaching the end of the pool
		while (currentBlock.getY() != 0) {
			currentBlock = stadium.moveTowards(currentBlock, x, 0, myLocation); // Move towards the end
			sleep((int) (movingSpeed * swimStroke.strokeTime)); // Simulate time taken for the stroke
			System.out.println("Thread " + this.ID + " swimming at speed " + movingSpeed);
		}

		// Swim back to the starting point
		while (currentBlock.getY() != (StadiumGrid.start_y - 1)) {
			currentBlock = stadium.moveTowards(currentBlock, x, StadiumGrid.start_y, myLocation); // Move back to start
			sleep((int) (movingSpeed * swimStroke.strokeTime)); // Simulate time taken for the stroke
		}
	}

	// Swimmer exits the pool after finishing the race
	public void exitPool() throws InterruptedException {
		int bench = stadium.getMaxY() - swimStroke.getOrder(); // Determine exit bench based on stroke order
		int lane = currentBlock.getX() + 1; // Determine exit lane
		currentBlock = stadium.moveTowards(currentBlock, lane, currentBlock.getY(), myLocation); // Move to exit lane

		// Move towards the bench
		while (currentBlock.getY() != bench) {
			currentBlock = stadium.moveTowards(currentBlock, lane, bench, myLocation);
			sleep(movingSpeed * 3); // Move slowly towards the bench
		}
	}

	// Run method executed when the thread starts
	public void run() {
		try {
			// Swimmer arrives at the stadium
			sleep(movingSpeed + (rand.nextInt(10))); // Simulate varying arrival times
			myLocation.setArrived(); // Mark swimmer as arrived

			// Wait for the previous swimmer to enter the stadium
			if (ID != 0) {
				synchronized (stadium) {
					while (!stadium.inside[ID - 1]) {
						stadium.wait();
					}
				}
			}

			// Enter the stadium
			enterStadium();

			// Notify that this swimmer has entered the stadium
			synchronized (stadium) {
				stadium.inside[ID] = true;
				stadium.notifyAll();
			}

			// Go to starting blocks
			goToStartingBlocks();

			// Wait for all swimmers to reach the starting blocks
			barrier.await();

			// Wait for all team members to be ready for the dive
			synchronized (stadium) {
				while (stadium.retrieveDive(team)) {
					stadium.wait();
				}
				stadium.setDive(team, true); // Set team ready to dive
			}

			// Swimmer dives into the pool
			dive();

			// Swim the race
			swimRace();

			// Handle the finish of the race
			if (swimStroke.order == 4) { // Last swimmer handles race finish
				finish.finishRace(ID, team); // Record the first-place finish
			} else {
				// Notify and prepare for next swimmer
				synchronized (stadium) {
					stadium.setDive(team, false); // Swimmer finished, set flag to false
					stadium.notifyAll(); // Notify other swimmers
				}
				exitPool(); // Exit the pool if not the last swimmer
			}

		} catch (InterruptedException e1) {
			// Handle InterruptedException
		} catch (BrokenBarrierException e2) {
			// Handle BrokenBarrierException
		}
	}
}
