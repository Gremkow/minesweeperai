package de.unima.info.ki.minesweeper.api;

import java.util.Random;

/**
* This agent uncovering positions randomly.
* It obviously does not use a SAT solver.
* 
*/
public class RandomMSAgent extends MSAgent {

	private Random rand = null;
	private boolean displayActivated = false;
	private boolean firstDecision = true;
	
	@Override
	public boolean solve() {
		this.rand = new Random();
		int numOfRows = this.field.getNumOfRows();
		int numOfCols = this.field.getNumOfCols();
		int x, y, feedback;
		
		do {
			if (displayActivated) System.out.println(field);
			if (firstDecision) {
				x = 0;
				y = 0;
				firstDecision = false;
			}
			else {
				x = rand.nextInt(numOfCols);
				y = rand.nextInt(numOfRows);
			}

			if (displayActivated) System.out.println("Uncovering (" + x + "," + y + ")");
			feedback = field.uncover(x,y);
			
		} while(feedback >= 0 && !field.solved());
		
		if (field.solved()) {
			if (displayActivated) System.out.println("Solved the field");
			return true;
		}
		else {
			if (displayActivated) System.out.println("BOOM!");
			return false;
		}
	}

	@Override
	public void activateDisplay() {
		this.displayActivated = true;
		
	}

	@Override
	public void deactivateDisplay() {
		this.displayActivated = false;
	}

}
