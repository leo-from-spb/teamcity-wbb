package org.jetbrains.teamcity.wbb;

/**
 * Settings per build configuration.
 *
 * @author Leonid Bushuev from JetBrains
 **/
public class WbbSettings {

  private boolean myAutoBuild = false;

  private boolean myAutoAssign = true;

  private boolean myDoubleCheck = false;

  private int myParallelLimit = 3;


  public boolean isAutoBuild() {
    return myAutoBuild;
  }

  public void setAutoBuild(boolean autoBuild) {
    myAutoBuild = autoBuild;
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

  public int getParallelLimit() {
    return myParallelLimit;
  }

  public void setParallelLimit(int parallelLimit) {
    myParallelLimit = parallelLimit;
  }

}
