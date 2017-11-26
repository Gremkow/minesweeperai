package de.unima.info.ki.minesweeper.api;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

public class Test {
  
  public Test() {
    // TODO Auto-generated constructor stub
  }

  public static void main(String[] args) {
    MSField f = new MSField("fields/profi1-30x16-99.txt");
    IntelligentMSAgent agent = new IntelligentMSAgent();
    agent.setField(f);
    //agent.activateDisplay();
    Instant start = Instant.now();
    System.out.println(agent.solve());
    Instant end = Instant.now();
	Duration dur = Duration.between(start, end);
	System.out.println("Zeit: " + (dur.toMillis() / 1000f) + "s");
  }

}
