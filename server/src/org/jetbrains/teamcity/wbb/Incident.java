package org.jetbrains.teamcity.wbb;

import jetbrains.buildServer.serverSide.SFinishedBuild;
import org.jetbrains.annotations.NotNull;

/**
 * @author Leonid Bushuev from JetBrains
 **/
public final class Incident {

  @NotNull
  private final long myGreenBuildId;

  @NotNull
  private final long myRedBuildId;

  @NotNull
  private final String myGreenBuildNr;

  @NotNull
  private final String myRedBuildNr;



  public Incident(@NotNull SFinishedBuild greenBuild, @NotNull SFinishedBuild redBuild) {
    myGreenBuildId = greenBuild.getBuildId();
    myGreenBuildNr = greenBuild.getBuildNumber();
    myRedBuildId = redBuild.getBuildId();
    myRedBuildNr = redBuild.getBuildNumber();
  }


  @Override
  public String toString() {
    return myGreenBuildNr + " .. " + myRedBuildNr;
  }


  @NotNull
  public long getGreenBuildId() {
    return myGreenBuildId;
  }

  @NotNull
  public long getRedBuildId() {
    return myRedBuildId;
  }

  @NotNull
  public String getGreenBuildNr() {
    return myGreenBuildNr;
  }

  @NotNull
  public String getRedBuildNr() {
    return myRedBuildNr;
  }
}
