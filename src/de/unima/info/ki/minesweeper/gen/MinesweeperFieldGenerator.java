package de.unima.info.ki.minesweeper.gen;

import java.io.File;
import java.io.FileWriter;
import java.util.Random;

/**
 * Can be used for test purpose, however, for the programming project the
 * delivered files should be used for the report. 
 * 
 */
public class MinesweeperFieldGenerator {

	private static final String path = "fields/";
	private static final Random rnd = new Random(776);

	public static void generateField(int n, int m, int mineCount, String namePrefix) throws Exception {
		boolean[][] field = new boolean[n][m];
		String fileName = path + namePrefix + n + "x" + m + "-" + mineCount + ".txt";

		while (mineCount > 0) {
			int rndN = rnd.nextInt(n);
			int rndM = rnd.nextInt(m);

			if (!field[rndN][rndM]) {
				field[rndN][rndM] = true;
				mineCount--;
			}
		}

		File file = new File(fileName);
		file.createNewFile();

		try {
			FileWriter writer = new FileWriter(file);
			for (boolean[] row : field) {
				for (boolean cell : row) {
					if (cell) {
						writer.append('X');
					} else {
						writer.append('-');
					}
					writer.append(' ');
				}
				writer.append('\n');
			}
			writer.close();
		} catch (Exception e) {
			System.err.println("Something bad happended " + e);

		}
	}

	public static void main(String[] args) throws Exception {
		generateField(9, 9, 10, "anfaenger1-");
		generateField(9, 9, 10, "anfaenger2-");
		generateField(9, 9, 10, "anfaenger3-");
		generateField(9, 9, 10, "anfaenger4-");
		generateField(9, 9, 10, "anfaenger5-");
		generateField(16, 16, 40, "fortgeschrittene1-");
		generateField(16, 16, 40, "fortgeschrittene2-");
		generateField(16, 16, 40, "fortgeschrittene3-");
		generateField(16, 16, 40, "fortgeschrittene4-");
		generateField(16, 16, 40, "fortgeschrittene5-");
		generateField(30, 16, 99, "profi1-");
		generateField(30, 16, 99, "profi2-");
		generateField(30, 16, 99, "profi3-");
		generateField(30, 16, 99, "profi4-");
		generateField(30, 16, 99, "profi5-");
	}

}
