package de.unima.info.ki.minesweeper.api;

public abstract class MSAgent {
	
	MSField field = null;
	
	public void setField(MSField field) {
		this.field = field;
	}	
	
	public abstract boolean solve();
	
	public abstract void activateDisplay();
	
	public abstract void deactivateDisplay();



}
