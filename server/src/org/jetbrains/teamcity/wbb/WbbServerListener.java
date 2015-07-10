package org.jetbrains.teamcity.wbb;

import jetbrains.buildServer.serverSide.BuildHistory;
import jetbrains.buildServer.serverSide.BuildServerAdapter;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SRunningBuild;
import org.jetbrains.annotations.NotNull;

/**
 * @author Leonid Bushuev from JetBrains
 **/
public class WbbServerListener extends BuildServerAdapter {

  @NotNull
  private final Situations mySituations;

  @NotNull
  private final WbbBuildStarter myBuildStarter;


  @NotNull
  private final BuildHistory myBuildHistory;



  public WbbServerListener(@NotNull final Situations situations,
                           @NotNull final WbbBuildStarter buildStarter,
                           @NotNull final BuildHistory buildHistory) {
    mySituations = situations;
    myBuildStarter = buildStarter;
    myBuildHistory = buildHistory;
  }


  @Override
  public void buildFinished(@NotNull SRunningBuild build) {
    if (build.isPersonal()) return;

    final SBuildType bt = build.getBuildType();
    if (bt == null) return;

    final Situation situation = mySituations.getOrCreateFor(bt);
    situation.setValid(false);
    Logic.refreshSituation(situation, bt, myBuildHistory);

    if (situation.settings.isAutoBuild()) {
      myBuildStarter.startIteration(situation, bt);
    }
  }



}
