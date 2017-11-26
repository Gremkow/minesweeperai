package de.unima.info.ki.minesweeper.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.sat4j.core.Vec;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IConstr;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.IVec;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.TimeoutException;

public class SatSolver {
  ISolver solver;
  IVec<IVecInt> clauses;
  
  public SatSolver() {
	  clauses = new Vec<>();
	  solver = SolverFactory.newDefault();
  }
  
  public void addClause(int[] clause) {
	//int[] cl = clause.stream().mapToInt(Integer::intValue).toArray();
	try {
		solver.addClause(new VecInt(clause));
	} catch (ContradictionException e) {
		//Bad
		e.printStackTrace();
	}
  }
  
  /**
   * 
   * @param KB
   * @param variable
   * @return True, if the variable is a mine. False, if no reasoning can be done
   */
  public boolean isMine(int variable) {
	IConstr constraint = null;
	  
	//Add search variable to the solver
	try {
	  constraint = solver.addClause(new VecInt(new int[] {-variable}));
	  if(!solver.isSatisfiable()){
		if(constraint != null) {
		  solver.removeConstr(constraint);
		}
		return true;
	  } else {
		if(constraint != null) {
	      solver.removeConstr(constraint);
		}
		return false;
	  }
	} catch (ContradictionException e) {
	  // An error here is good, because this means that the variable leads to a contradiction
	  return true;
	} catch (TimeoutException e) {
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
  public boolean isNoMine(int variable) {
	IConstr constraint = null;
	  
    //Add search variable to the solver
    try {
      constraint = solver.addClause(new VecInt(new int[] {variable}));
      if(!solver.isSatisfiable()){
    	if(constraint != null) {
    	  solver.removeConstr(constraint);
    	}
    	return true;
      } else {
    	if(constraint != null) {
          solver.removeConstr(constraint);
    	}
    	return false;
      }
    } catch (ContradictionException e) {
      // An error here is good, because this means that the variable leads to a contradiction
      return true;
    } catch (TimeoutException e) {
	  e.printStackTrace();
	  return false;
	}
  }

}
