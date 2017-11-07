package de.unima.info.ki.minesweeper.api;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class MSField {

	private boolean[][] f;
	
	private int[][] uf;
	
	private int numOfCols = 0;
	private int numOfRows = 0;
	
	/**
	* Reads a minesweeper field from a text file and constructs an representation of that field.
	* 
	* @param path the path to the file that represents a mine sweeper field.
	*/
	public MSField(String path) {
		try {
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(new FileInputStream(path));
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = br.readLine()) != null)   {
				numOfRows++;
				if (numOfCols == 0) { numOfCols = line.length() / 2; }
			}
			in.close();
			f = new boolean[numOfCols][numOfRows];
			uf = new int[numOfCols][numOfRows];
			
			in = new DataInputStream(new FileInputStream(path));
			br = new BufferedReader(new InputStreamReader(in));
			// not nice to do it twice ...
			int y = 0;
			while ((line = br.readLine()) != null)   {
				for (int x = 0; x < numOfCols; x++) {
					char mine = line.toCharArray()[x*2];
					uf[x][y] = -1; // -1 means uncovered (not bomb)
					if (mine == 'X') f[x][y] = true;
					else f[x][y] = false;
				}
				y++;
			}
		}
		catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer("");
		for (int y = 0; y < numOfRows; y++) {
			for (int x = 0; x < numOfCols; x++) {
				if (f[x][y]) sb.append('X');
				else {
					if (uf[x][y] >= 0) {
						sb.append(uf[x][y]);
					}
					else {
						sb.append('-');
					}
				}
				sb.append(' ');
			}
			sb.append("\n");
		}
		return sb.toString();	
	}
	
	/**
	* Uncovers a position x,y and returns the number of mines in the neighborhood.
	* 
	* @param x 
	* @param y
	* @return Number of mines in the neighborhood or -1 if the current position contains a mine.
	*/
	public int uncover(int x, int y) {
		if (f[x][y]) {
			return -1;
		}
		else {
			int num = countNeighbors(x, y);
			if (validPosition(x,y)) uf[x][y] = num;
			return num;
		}	
	}
	
	/**
	* Returns the number of rows of this field.
	* 
	* @return Number of rows.
	*/
	public int getNumOfRows() {
		return numOfRows; 
	}
	
	/**
	* Returns the number of columns of this field.
	* 
	* @return Number of columns.
	*/
	public int getNumOfCols() {
		return numOfCols; 
	}
	
	private int countNeighbors(int x, int y) {
		int count = 0;
		if (validPosition(x+1, y+1) && f[x+1][y+1])  count++;
		if (validPosition(x+1, y)   && f[x+1][y])    count++;
		if (validPosition(x+1, y-1) && f[x+1][y-1])  count++;
		if (validPosition(x, y+1) && f[x][y+1])  count++;
		if (validPosition(x, y-1) && f[x][y-1])  count++;
		if (validPosition(x-1, y+1) && f[x-1][y+1])  count++;
		if (validPosition(x-1, y)   && f[x-1][y])  count++;
		if (validPosition(x-1, y-1) && f[x-1][y-1])  count++;
		return count;	
	}

	private boolean validPosition(int x, int y) {
		if (x >= 0 && x < numOfCols && y >= 0 && y < numOfRows) return true;
		return false;
	}
	
	public boolean solved() {
		for (int x = 0; x < numOfCols; x++) {
			for (int y = 0; y < numOfRows; y++) {
				if (f[x][y] == false && uf[x][y] < 0) {
					return false;
					
				}
			}
		}
		return true;
		
	}
}
