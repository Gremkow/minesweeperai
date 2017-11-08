package de.unima.info.ki.minesweeper.api;

import java.util.ArrayList;
import java.util.HashMap;

public class IntelligentMSAgent extends MSAgent {

  private boolean displayActivated = false;
  private boolean firstDecision = true;
  private ArrayList<ArrayList<Integer>> KB;
  private boolean[][] uncovered;
  private HashMap<Tuple<Integer, Integer>, Integer> locVarMap;
  private SatSolver sSolver;

  public IntelligentMSAgent() {
    KB = new ArrayList<>();
    sSolver = new SatSolver();
    locVarMap = new HashMap<>();
  }

  @Override
  public void setField(MSField field) {
    super.setField(field);
    uncovered = new boolean[field.getNumOfCols()][field.getNumOfRows()];
    int count = 1;
    for(int i = 0; i < field.getNumOfCols(); i++) {
      for(int j = 0; j < field.getNumOfRows(); j++) {
        locVarMap.put(new Tuple<Integer, Integer>(j, i), count);        
        count++;
      }
    }
  }

  @Override
  public boolean solve() {
    int x, y, feedback;

    do {
      if (displayActivated) {
        System.out.println(field);
      }

      if (firstDecision) {
        x = 0;
        y = 0;
        firstDecision = false;
      } else {
        Tuple<Integer, Integer> pos = getNextPosition();
        x = pos.getKey();
        y = pos.getValue();
      }

      if (displayActivated) {
        System.out.println("Uncovering (" + x + "," + y + ")");
      }
      feedback = field.uncover(x, y);
      uncovered[x][y] = true;
      if (feedback >= 0) {
        updateKB(x, y, feedback);
      }

    } while (feedback >= 0 && !field.solved());

    if (field.solved()) {
      if (displayActivated) {
        System.out.println("Solved the field");
      }
      return true;
    } else {
      if (displayActivated) {
        System.out.println("BOOM!");
      }
      return false;
    }
  }

  private void updateKB(int x, int y, int feedback) {
    ArrayList<Integer> clause;
    ArrayList<Tuple<Integer, Integer>> neighbours = getCoveredNeighbours(x, y);
    int ncount = neighbours.size();

    //Currently opened is definately no mine
    clause = new ArrayList<>();
    clause.add(-1 * locVarMap.get(new Tuple<Integer, Integer>(x, y)));
    KB.add(clause);
    
    //Set surroundings to false if feedback = 0
    if(feedback == 0) {
      for (Tuple<Integer, Integer> pos : neighbours) {
        clause = new ArrayList<>();
        clause.add(-1 * locVarMap.get(pos));
        KB.add(clause);
      }
      return;
    }
    
    //Check upper bound
    for (int n = 0; n < ncount && (n + feedback) < ncount; n++) {
      Tuple<Integer, Integer> pos = neighbours.get(n);
      int i = pos.getKey(), j = pos.getValue();

      // Check, that there a not more than feedback mines
      int count = 0, vorlaufplan = 0, vorlauf = 0;
      outer: while (true) {
        vorlauf = 0;
        clause = new ArrayList<>();
        clause.add(-1 * locVarMap.get(pos)); // add current starter
        for (int a = x + 1; a >= x - 1; a--) {
          if (a < 0 || a > field.getNumOfCols()) {
            continue;
          }
          for (int b = y + 1; b >= y - 1; b--) {
            if (b < 0 || b > field.getNumOfRows() || (a == x && b == y)) {
              continue;
            }
            if (vorlauf < vorlaufplan) {
              vorlauf++;
              continue;
            }
            if (a == i && b == j) {
              // Break, if the current field is reached
              KB.add(clause);
              break outer;
            }
            if (count == feedback) {
              // If the number has reached feedback, we start with the next
              // round
              count = 0;
              KB.add(clause);
              vorlaufplan++;
              continue outer;
            }
            clause.add(-1 * locVarMap.get(new Tuple<Integer, Integer>(a, b)));
            count++;
          }
        }
      }      
    }
    
    //Lower bound
    // (!a || b) && (a || !b) && (a || b) && !a --> Widerspruch
    for(int i = 0; i < feedback; i++) { //anzahl negationen
      for(int j = 0; j < binom(ncount, i); j++) {
        clause = new ArrayList<>();
        for (int k = 0; k < ncount; k++) {
          Tuple<Integer, Integer> pos = neighbours.get(k);
          int factor = (k >= j && k < j+i) ? -1: 1;
          clause.add(factor * locVarMap.get(pos));
        }
        KB.add(clause);
      }
    }
    System.out.println();

  }

  private Tuple<Integer, Integer> getNextPosition() {
    for (Tuple<Integer, Integer> pos : getCoveredFields()) {
      if (sSolver.isNoMine(KB, locVarMap.get(pos))) {
        return pos;
      } else if (sSolver.isMine(KB, locVarMap.get(pos))) {
        // If we've found a mine, ignore this field in further calculation
        uncovered[pos.getKey()][pos.getValue()] = true;
        ArrayList<Integer> clause = new ArrayList<>();
        clause.add(locVarMap.get(pos));
        KB.add(clause);
      }
    }
    return getCoveredFields().get(0);
  }

  /**
   * Return all indices of possible positions
   */
  private ArrayList<Tuple<Integer, Integer>> getCoveredFields() {
    ArrayList<Tuple<Integer, Integer>> positions = new ArrayList<>();
    for (int i = 0; i < uncovered.length; i++) {
      for (int j = 0; j < uncovered[i].length; j++) {
        if (!uncovered[i][j]) {
          positions.add(new Tuple<Integer, Integer>(i, j));
        }
      }
    }
    return positions;
  }

  /**
   * Return all indices of covered neighbours
   */
  private ArrayList<Tuple<Integer, Integer>> getCoveredNeighbours(int x, int y) {
    ArrayList<Tuple<Integer, Integer>> positions = new ArrayList<>();
    for (int i = x - 1; i <= x + 1; i++) {
      if (i < 0 || i >= field.getNumOfCols()) {
        continue;
      }
      for (int j = y - 1; j <= y + 1; j++) {
        if (j < 0 || j >= field.getNumOfRows()) {
          continue;
        }

        if (!uncovered[i][j]) {
          positions.add(new Tuple<Integer, Integer>(i, j));
        }
      }
    }
    return positions;
  }
  
  private int binom(int n, int k) {
    return faculty(n)/(faculty(n-k) * faculty(k));
  }
  
  private int faculty(int n) {
    if(n <= 1) return 1;
    return n*faculty(n-1);
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
