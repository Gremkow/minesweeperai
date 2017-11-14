package de.unima.info.ki.minesweeper.api;

import java.util.ArrayList;
import java.util.HashMap;

public class Test {

  private static HashMap<Tuple<Integer, Integer>, Integer> locVarMap = new HashMap<>();
  private static ArrayList<ArrayList<Integer>> KB = new ArrayList<>();
  
  public Test() {
    // TODO Auto-generated constructor stub
  }

  public static void main(String[] args) {
    MSField f = new MSField("fields/profi1-30x16-99.txt");
    long startTime = System.currentTimeMillis();
    IntelligentMSAgent agent = new IntelligentMSAgent();
    agent.setField(f);
    agent.activateDisplay();
    agent.solve();
    long endTime = System.currentTimeMillis();
    System.out.println("Runtime in ms: " + (startTime - endTime));
  }

}
