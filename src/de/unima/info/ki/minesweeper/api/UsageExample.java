package de.unima.info.ki.minesweeper.api;

import java.time.Duration;
import java.time.Instant;

public class UsageExample {

	public static void main(String[] args) {
		//testOne();
		testAll();
	}

	private static void testAll() {
		// use smaller numbers for larger fields
		final int iterations = 1000;
		System.out.println("Trying all fields " + iterations + " times");
		
		// if you want to iterate over all of them, this might help
		String[] fields = { "anfaenger1-9x9-10.txt", "anfaenger2-9x9-10.txt", "anfaenger3-9x9-10.txt", "anfaenger4-9x9-10.txt", "anfaenger5-9x9-10.txt", "baby1-3x3-0.txt", "baby2-3x3-1.txt", "baby3-5x5-1.txt", "baby4-5x5-3.txt", "baby5-5x5-5.txt", "baby6-7x7-1.txt", "baby7-7x7-3.txt", "baby8-7x7-5.txt", "baby9-7x7-10.txt", "fortgeschrittene1-16x16-40.txt", "fortgeschrittene2-16x16-40.txt", "fortgeschrittene3-16x16-40.txt", "fortgeschrittene4-16x16-40.txt", "fortgeschrittene5-16x16-40.txt", "profi1-30x16-99.txt", "profi2-30x16-99.txt", "profi3-30x16-99.txt", "profi4-30x16-99.txt", "profi5-30x16-99.txt" };

		for (String fieldName : fields) {
			long totalDur = 0;
			int success = 0;
			for (int i = 0; i < iterations; i++) {
				MSField f = new MSField("fields/" + fieldName);
				MSAgent agent = new IntelligentMSAgent();
				agent.setField(f);
				// to see what happens in the first iteration
				/*
				 * if (i == 1) agent.activateDisplay(); else
				 * agent.deactivateDisplay();
				 */
				Instant start = Instant.now();
				if (agent.solve()) {
					Instant end = Instant.now();
					totalDur += Duration.between(start, end).toMillis();
					success++;
				}
			}
			double rate = (double) success / (double) iterations;
			System.out.println("Erfolgsquote (" + fieldName + "): " + rate + " - Zeit: " + (totalDur / 1000f) + "s/" + (1f * totalDur / success) + "ms");
		}
	}

	private static void testOne() {
		// use smaller numbers for larger fields
		final int iterations = 100;

		// if you want to iterate over all of them, this might help
		String field = "fortgeschrittene2-16x16-40.txt";//"profi1-30x16-99.txt";

		int success = 0;
		for (int i = 0; i < iterations; i++) {
			MSField f = new MSField("fields/" + field);
			MSAgent agent = new IntelligentMSAgent();
			agent.setField(f);
			// to see what happens in the first iteration
			/*
			 * if (i == 1) agent.activateDisplay(); else
			 * agent.deactivateDisplay();
			 */
			Instant start = Instant.now();
			boolean solved = agent.solve();
			if (solved) {
				success++;
			}
			Instant end = Instant.now();
			Duration dur = Duration.between(start, end);
			System.out.println("Field solved: " + solved + " - Time: " + (dur.toMillis() / 1000f));
		}
		double rate = (double) success / (double) iterations;
		System.out.println("Erfolgsquote (" + field + "): " + rate);
	}
}
