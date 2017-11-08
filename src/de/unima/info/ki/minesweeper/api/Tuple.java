package de.unima.info.ki.minesweeper.api;

public class Tuple<X,Y> {
  private final X key;
  private final Y value;
  
  public Tuple(X key, Y value) {
    this.key = key;
    this.value = value;
  }
  
  public X getKey() {
    return this.key;
  }
  public Y getValue() {
    return this.value;
  }
  
  @Override
  public String toString() {
    return "[" + key + " -> " + value + "]";
  }
  
  @Override
  public boolean equals(Object obj) {
    if(obj instanceof Tuple<?, ?>) {
      Tuple<?,?> other = (Tuple<?, ?>)(obj);
      if(this.key.equals(other.key) && this.value.equals(other.value)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public int hashCode() {
    return key.hashCode() + value.hashCode();
  }
}
