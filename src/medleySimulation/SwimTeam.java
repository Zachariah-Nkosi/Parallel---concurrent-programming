//Author: Nkosi Zachariah
//Date: 10 September 2024
//Class to represent a swim team - which has four swimmers
package medleySimulation;

import medleySimulation.Swimmer.SwimStroke;
//import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class SwimTeam extends Thread {

	public static StadiumGrid stadium; // shared
	private Swimmer[] swimmers;
	private int teamNo; // team number
	private int entered = 1;
	private CyclicBarrier barrier;

	// private CountDownLatch latch;
	public static final int sizeOfTeam = 4;

	SwimTeam(int ID, FinishCounter finish, PeopleLocation[] locArr,
			CyclicBarrier Barrier) {
		this.teamNo = ID;
		// this.latch = lt;
		this.barrier = Barrier;
		swimmers = new Swimmer[sizeOfTeam];
		SwimStroke[] strokes = SwimStroke.values(); // Get all enum constants
		stadium.returnStartingBlock(ID);

		for (int i = teamNo * sizeOfTeam, s = 0; i < ((teamNo + 1) * sizeOfTeam); i++, s++) { // initialise swimmers in
																								// team
			locArr[i] = new PeopleLocation(i, strokes[s].getColour());
			int speed = (int) (Math.random() * (3) + 30); // range of speeds
			swimmers[s] = new Swimmer(i, teamNo, locArr[i], finish, speed, strokes[s], barrier); // hardcoded
			// speed for now
		}
	}

	public void run() {
		try {
			for (int s = 0; s < sizeOfTeam; s++) { // start swimmer threads
				swimmers[s].start();

			}

			for (int s = 0; s < sizeOfTeam; s++)
				swimmers[s].join(); // don't really need to do this;

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
