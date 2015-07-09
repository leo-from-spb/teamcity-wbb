package org.jetbrains.teamcity.wbb;

import jetbrains.buildServer.serverSide.BuildHistoryEx;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SFinishedBuild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Leonid Bushuev from JetBrains
 **/
public class SituationBean {

  @NotNull
  private final Situation mySituation;

  @Nullable
  private final SFinishedBuild myGreenBuild;

  @Nullable
  private final SFinishedBuild myRedBuild;

  @NotNull
  private final SBuildType myBt;

  @NotNull
  private final BuildHistoryEx myBuildHistory;


  public SituationBean(@NotNull Situation situation,
                       @NotNull SBuildType bt,
                       @NotNull BuildHistoryEx buildHistory) {
    mySituation = situation;
    myBt = bt;
    myBuildHistory = buildHistory;

    final Incident incident = mySituation.getIncident();
    if (incident != null) {
      myGreenBuild = buildHistory.findEntry(incident.getGreenBuildId());
      myRedBuild = buildHistory.findEntry(incident.getRedBuildId());
    }
    else {
      myGreenBuild = myRedBuild = null;
    }
  }


  public Situation getSituation() {
    return mySituation;
  }

  public SBuildType getBt() {
    return myBt;
  }


  @Nullable
  public SFinishedBuild getGreenBuild() {
    return myGreenBuild;
  }

  @Nullable
  public SFinishedBuild getRedBuild() {
    return myRedBuild;
  }
}
