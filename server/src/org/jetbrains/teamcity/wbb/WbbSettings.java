package org.jetbrains.teamcity.wbb;

/**
 * Settings per build configuration.
 *
 * @author Leonid Bushuev from JetBrains
 **/
public class WbbSettings {

  private boolean myAutoActivate = false;

  private boolean myAutoAssign = true;

  private boolean myDoubleCheck = false;

  private boolean myAlsoFindChange = false;

  private int myParallelLimit = 3;


  public boolean isAutoActivate() {
    return myAutoActivate;
  }

  public void setAutoActivate(boolean autoActivate) {
    myAutoActivate = autoActivate;
  }

  public boolean isAutoAssign() {
    return myAutoAssign;
  }

  public void setAutoAssign(boolean autoAssign) {
    myAutoAssign = autoAssign;
  }

  public boolean isDoubleCheck() {
    return myDoubleCheck;
  }

  public void setDoubleCheck(boolean doubleCheck) {
    myDoubleCheck = doubleCheck;
  }

  public boolean isAlsoFindChange() {
    return myAlsoFindChange;
  }

  public void setAlsoFindChange(boolean alsoFindChange) {
    myAlsoFindChange = alsoFindChange;
  }

  public int getParallelLimit() {
    return myParallelLimit;
  }

  public void setParallelLimit(int parallelLimit) {
    myParallelLimit = parallelLimit;
  }

}
