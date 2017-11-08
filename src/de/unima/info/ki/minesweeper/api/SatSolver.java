package de.unima.info.ki.minesweeper.api;

import java.util.ArrayList;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

public class SatSolver {
  ISolver solver;
  
  public SatSolver() {
     
  }
  
  /**
   * 
   * @param KB
   * @param variable
   * @return True, if the variable is a mine. False, if no reasoning can be done
   */
  public boolean isMine(ArrayList<ArrayList<Integer>> KB, int variable) {
    solver = SolverFactory.newDefault();
    
    //Add all clauses from the KB to the solver
    for (ArrayList<Integer> clause : KB) {
      int[] cl = clause.stream().mapToInt(Integer::intValue).toArray();
      try {
        solver.addClause(new VecInt(cl));
      } catch (ContradictionException e) {
        // An error here is bad, because this means the KB is not good
        e.printStackTrace();
      }
    }
    
    //Add search variable to the solver
    try {
      solver.addClause(new VecInt(new int[] {-variable}));
    } catch (ContradictionException e) {
      // An error here is good, because this means that the variable leads to a contradiction
      return true;
    }
    
    try {
      return !solver.isSatisfiable();
    } catch (TimeoutException e) {
      //If it takes too long, simply say, that no knowledge was created
      e.printStackTrace();
      return false;
    }
  }
  
  /**
   * 
   * @param KB
   * @param variable
   * @return True, if the variable is not a mine. False, if no reasoning can be done
   */
  public boolean isNoMine(ArrayList<ArrayList<Integer>> KB, int variable) {
    solver = SolverFactory.newDefault();
    
    //Add all clauses from the KB to the solver
    for (ArrayList<Integer> clause : KB) {
      int[] cl = clause.stream().mapToInt(Integer::intValue).toArray();
      try {
        solver.addClause(new VecInt(cl));
      } catch (ContradictionException e) {
        // An error here is bad, because this means the KB is not good
        e.printStackTrace();
      }
    }
    
    //Add search variable to the solver
    try {
      solver.addClause(new VecInt(new int[] {variable}));
    } catch (ContradictionException e) {
      // An error here is good, because this means that the variable leads to a contradiction
      return true;
    }
    
    try {
      return !solver.isSatisfiable();
    } catch (TimeoutException e) {
      //If it takes too long, simply say, that no knowledge was created
      e.printStackTrace();
      return false;
    }
  }

}
