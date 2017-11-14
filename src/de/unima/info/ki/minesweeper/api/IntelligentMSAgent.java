package de.unima.info.ki.minesweeper.api;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class IntelligentMSAgent extends MSAgent {

  private boolean displayActivated = false;
  private boolean firstDecision = true;
  private ArrayList<ArrayList<Integer>> KB;
  private boolean[][] uncovered;
  private boolean[][] mines;
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
    mines = new boolean[field.getNumOfCols()][field.getNumOfRows()];
    int count = 1;
    for(int i = 0; i < field.getNumOfCols(); i++) {
      for(int j = 0; j < field.getNumOfRows(); j++) {
        locVarMap.put(new Tuple<Integer, Integer>(i, j), count);
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
        update(x, y, feedback, true);
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

  private void update(int x, int y, int feedback, boolean first) {
    ArrayList<Integer> clause;
    ArrayList<Tuple<Integer, Integer>> neighbours = getCoveredNeighbours(x, y), mines = getMinesNeighbours(x, y);
    int n = neighbours.size();
    int k = feedback - mines.size();
    
    //Current field
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
    
    bounds(x, y, k, n);
    
    if(first) {
      for (Tuple<Integer, Integer> p : getUncoveredNeighbours(x, y)) {
        update(p.getKey(), p.getValue(), field.uncover(p.getKey(), p.getValue()), false);
      }
    }
  }
  
  private void bounds(int x, int y, int k, int n) {
    int[] s, vars = getCoveredNeighbours(x, y).stream().map(ele -> locVarMap.get(ele)).mapToInt(Integer::intValue).toArray();
    int l;
    
    //upper
    l=k+1;
    s = new int[l];
    if (l <= vars.length) {
      // first index sequence: 0, 1, 2, ...
      for (int i = 0; (s[i] = i) < l - 1; i++);  
      KB.add(getSubset(vars, s, -1));
      while(true) {
          int i;
          // find position of item that can be incremented
          for (i = l - 1; i >= 0 && s[i] == vars.length - l + i; i--); 
          if (i < 0) {
              break;
          }
          s[i]++;                    // increment this item
          for (++i; i < l; i++) {    // fill up remaining items
              s[i] = s[i - 1] + 1; 
          }
          KB.add(getSubset(vars, s, -1));
      }
    }
    
    //lower
    l=n-k+1;
    s = new int[l];
    if (l <= vars.length) {
      // first index sequence: 0, 1, 2, ...
      for (int i = 0; (s[i] = i) < l - 1; i++);  
      KB.add(getSubset(vars, s, 1));
      while(true) {
          int i;
          // find position of item that can be incremented
          for (i = l - 1; i >= 0 && s[i] == vars.length - l + i; i--); 
          if (i < 0) {
              break;
          }
          s[i]++;                    // increment this item
          for (++i; i < l; i++) {    // fill up remaining items
              s[i] = s[i - 1] + 1; 
          }
          KB.add(getSubset(vars, s, 1));
      }
    }
  }
  
  /**
   * 
   * @param k number of mines
   * @param n number of fields to check
   */
  private void upperBound(int x, int y, int k, int n) {
    ArrayList<Integer> clause;
    int[] vars;
    k+=1;
    vars = getCoveredNeighbours(x, y).stream().map(ele -> locVarMap.get(ele)).mapToInt(Integer::intValue).toArray();

    int[] s = new int[k]; // here we'll keep indices pointing to elements in input array

    if (k <= vars.length) {
        // first index sequence: 0, 1, 2, ...
        for (int i = 0; (s[i] = i) < k - 1; i++);  
        KB.add(getSubset(vars, s, -1));
        while(true) {
            int i;
            // find position of item that can be incremented
            for (i = k - 1; i >= 0 && s[i] == vars.length - k + i; i--); 
            if (i < 0) {
                break;
            }
            s[i]++;                    // increment this item
            for (++i; i < k; i++) {    // fill up remaining items
                s[i] = s[i - 1] + 1; 
            }
            KB.add(getSubset(vars, s, -1));
        }
    }
  }
  
  //generate actual subset by index sequence
  ArrayList<Integer> getSubset(int[] input, int[] subset, int fak) {
      ArrayList<Integer> result = new ArrayList<>(); 
      for (int i = 0; i < subset.length; i++) 
          result.add(fak * input[subset[i]]);
      return result;
  }
  
  /**
   * 
   * @param k number of mines
   * @param n number of fields to check
   */
  private void lowerBound(int x, int y, int k, int n) {
    ArrayList<Integer> clause;
    int[] vars;
    k = n - k + 1;
    vars = getCoveredNeighbours(x, y).stream().map(ele -> locVarMap.get(ele)).mapToInt(Integer::intValue).toArray();

    int[] s = new int[k]; // here we'll keep indices pointing to elements in input array

    if (k <= vars.length) {
        // first index sequence: 0, 1, 2, ...
        for (int i = 0; (s[i] = i) < k - 1; i++);  
        KB.add(getSubset(vars, s, 1));
        while(true) {
            int i;
            // find position of item that can be incremented
            for (i = k - 1; i >= 0 && s[i] == vars.length - k + i; i--); 
            if (i < 0) {
                break;
            }
            s[i]++;                    // increment this item
            for (++i; i < k; i++) {    // fill up remaining items
                s[i] = s[i - 1] + 1; 
            }
            KB.add(getSubset(vars, s, 1));
        }
    }
  }
  
  private Tuple<Integer, Integer> getNextPosition() {
    for (Tuple<Integer, Integer> pos : getCoveredFields()) {
      if (sSolver.isNoMine(KB, locVarMap.get(pos))) {
        return pos;
      }
      if (sSolver.isMine(KB, locVarMap.get(pos))) {
        // If we've found a mine, ignore this field in further calculation
        uncovered[pos.getKey()][pos.getValue()] = true;
        mines[pos.getKey()][pos.getValue()] = true;
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
  
  private ArrayList<Tuple<Integer, Integer>> getUncoveredNeighbours(int x, int y) {
    ArrayList<Tuple<Integer, Integer>> positions = new ArrayList<>();
    for (int i = x - 1; i <= x + 1; i++) {
      if (i < 0 || i >= field.getNumOfCols()) {
        continue;
      }
      for (int j = y - 1; j <= y + 1; j++) {
        if (j < 0 || j >= field.getNumOfRows()) {
          continue;
        }

        if (uncovered[i][j] && !mines[i][j]) {
          positions.add(new Tuple<Integer, Integer>(i, j));
        }
      }
    }
    return positions;
  }
  
  /**
   * Return all indices of neighbor mines
   */
  private ArrayList<Tuple<Integer, Integer>> getMinesNeighbours(int x, int y) {
    ArrayList<Tuple<Integer, Integer>> positions = new ArrayList<>();
    for (int i = x - 1; i <= x + 1; i++) {
      if (i < 0 || i >= field.getNumOfCols()) {
        continue;
      }
      for (int j = y - 1; j <= y + 1; j++) {
        if (j < 0 || j >= field.getNumOfRows()) {
          continue;
        }

        if (mines[i][j]) {
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
