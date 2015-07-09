package org.jetbrains.teamcity.wbb;

import jetbrains.buildServer.serverSide.SFinishedBuild;
import jetbrains.buildServer.util.Couple;
import org.jetbrains.annotations.NotNull;

/**
 * @author Leonid Bushuev from JetBrains
 **/
public final class Incident {

  private final long myRedBuildId;

  private final long myGreenBuildId;

  @NotNull
  private final String myRedBuildNr;

  @NotNull
  private final String myGreenBuildNr;



  public Incident(@NotNull SFinishedBuild redBuild, @NotNull SFinishedBuild greenBuild) {
    myGreenBuildId = greenBuild.getBuildId();
    myGreenBuildNr = greenBuild.getBuildNumber();
    myRedBuildId = redBuild.getBuildId();
    myRedBuildNr = redBuild.getBuildNumber();
  }

  public Incident(@NotNull final Couple<SFinishedBuild> couple) {
    this(couple.a, couple.b);
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
