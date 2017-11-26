package de.unima.info.ki.minesweeper.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class IntelligentMSAgent extends MSAgent {

  private boolean displayActivated = false;
  private boolean firstDecision = true;
  //private boolean[][] uncovered;
  private ArrayList<Tuple<Integer, Integer>> coveredFields, mineFields;
  //private boolean[][] mines;
  private HashMap<Tuple<Integer, Integer>, Integer> locVarMap;
  private SatSolver sSolver;

  public IntelligentMSAgent() {
    
  }

  @Override
  public void setField(MSField field) {
	super.setField(field);
	locVarMap = new HashMap<>();  
	coveredFields = new ArrayList<>();
	mineFields = new ArrayList<>();
    
    int varNumber = 1;
    for(int i = 0; i < field.getNumOfCols(); i++) {
      for(int j = 0; j < field.getNumOfRows(); j++) {
    	  	Tuple<Integer, Integer> tpl = new Tuple<Integer, Integer>(i, j);
        locVarMap.put(tpl, varNumber);
        coveredFields.add(tpl);
        varNumber++;
      }
    }
  }

  @Override
  public boolean solve() {
	//init solver
	sSolver = new SatSolver();  
	  
    int feedback;
    Tuple<Integer, Integer> pos;

    do {
      if (displayActivated) {
        System.out.println(field);
      }

      if (firstDecision) {
        pos = new Tuple<Integer, Integer>(0, 0);
        firstDecision = false;
      } else {
        pos = getNextPosition();
      }
      
      /*
      if (displayActivated) {
        System.out.println("Uncovering (" + x + "," + y + ")");
      }
      */
      
      //Uncover the field
      feedback = field.uncover(pos.getKey(), pos.getValue());
      coveredFields.remove(pos);
      
      if (feedback >= 0) {
        update(pos, feedback, true);
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

  private void update(Tuple<Integer, Integer> pos, int feedback, boolean first) {
    //Current field
    //int l = t;
    sSolver.addClause(new int[]{-locVarMap.get(pos)});
    
    //Set surroundings to false if feedback = 0
    if(feedback == 0) {
      for (Tuple<Integer, Integer> nP : getCoveredNeighbours(pos)) {
        sSolver.addClause(new int[]{-locVarMap.get(nP)});
      }
      return;
    }
    
    //Get the clauses from the bounds
    bounds(pos, feedback);
  }
  
  private void bounds(Tuple<Integer, Integer> pos, int feedback) {
	ArrayList<Tuple<Integer, Integer>> neighbours = getCoveredNeighbours(pos);
	int n = neighbours.size();
	int k = feedback - getNeighboursMineCount(pos);
    
	int[] s, vars = getCoveredNeighbours(pos).stream().map(ele -> locVarMap.get(ele)).mapToInt(Integer::intValue).toArray();
    int l;
    
    //upper
    l=k+1;
    s = new int[l];
    if (l <= vars.length) {
      // first index sequence: 0, 1, 2, ...
      for (int i = 0; (s[i] = i) < l - 1; i++);  
      sSolver.addClause(getSubset(vars, s, -1));
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
          sSolver.addClause(getSubset(vars, s, -1));
      }
    }
    
    //lower
    l=n-k+1;
    s = new int[l];
    if (l <= vars.length) {
      // first index sequence: 0, 1, 2, ...
      for (int i = 0; (s[i] = i) < l - 1; i++);  
      sSolver.addClause(getSubset(vars, s, 1));
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
          sSolver.addClause(getSubset(vars, s, 1));
      }
    }
  }
  
  //generate actual subset by index sequence
  int[] getSubset(int[] input, int[] subset, int fak) {
	  int[] result = new int[subset.length];
      for (int i = 0; i < subset.length; i++) 
    	  result[i] = (fak * input[subset[i]]);
      return result;
  }
  
  private Tuple<Integer, Integer> getNextPosition() {
	ArrayList<Tuple<Integer, Integer>> cF = new ArrayList<>(coveredFields);
    for (Tuple<Integer, Integer> pos : cF) {
      if (sSolver.isNoMine(locVarMap.get(pos))) {
        return pos;
      }
      if (sSolver.isMine(locVarMap.get(pos))) {
        // If we've found a mine, ignore this field in further calculation
        coveredFields.remove(pos);
        mineFields.add(pos);
        sSolver.addClause(new int[]{locVarMap.get(pos)});
      }
    }
    //get random field next if nothing is concludeable
    Random r = new Random();
    int index = r.nextInt(coveredFields.size());
    return coveredFields.get(index);
  }
  
  /**
   * Return all indices of covered neighbours
   */
  private ArrayList<Tuple<Integer, Integer>> getCoveredNeighbours(Tuple<Integer, Integer> pos) {
    ArrayList<Tuple<Integer, Integer>> positions = new ArrayList<>();
    for (int i = pos.getKey() - 1; i <= pos.getKey() + 1; i++) {
      for (int j = pos.getValue() - 1; j <= pos.getValue() + 1; j++) {
        Tuple<Integer, Integer> nP = new Tuple<Integer, Integer>(i, j);
        if(coveredFields.contains(nP)) {
        	positions.add(nP);        	
        }
      }
    }
    positions.remove(pos);
    return positions;
  }
  
  /**
   * Return all indices of neighbor mines
   */
  private int getNeighboursMineCount(Tuple<Integer, Integer> pos) {
	ArrayList<Tuple<Integer, Integer>> positions = new ArrayList<>();
	for (int i = pos.getKey() - 1; i <= pos.getKey() + 1; i++) {
		for (int j = pos.getValue() - 1; j <= pos.getValue() + 1; j++) {
		  Tuple<Integer, Integer> nP = new Tuple<Integer, Integer>(i, j);
		  if(mineFields.contains(nP)) {
		  	positions.add(nP);        	
		  }
		}
	}
	positions.remove(pos);
	return positions.size();
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
