package org.jetbrains.teamcity.wbb;

import jetbrains.buildServer.serverSide.BuildServerAdapter;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SRunningBuild;
import org.jetbrains.annotations.NotNull;

import java.util.SortedSet;

/**
 * @author Leonid Bushuev from JetBrains
 **/
public class WbbServerListener extends BuildServerAdapter {

  @NotNull
  private final Situations mySituations;

  @NotNull
  private final WbbBuildStarter myBuildStarter;


  public WbbServerListener(@NotNull final Situations situations,
                           @NotNull final WbbBuildStarter buildStarter) {
    mySituations = situations;
    myBuildStarter = buildStarter;
  }


  @Override
  public void buildFinished(@NotNull SRunningBuild build) {
    if (build.isPersonal()) return;

    final SBuildType bt = build.getBuildType();
    if (bt == null) return;

    final Situation situation = mySituations.getOrCreateFor(bt);
    situation.setValid(false);
    Logic.refreshSituation(situation, bt);

    if (situation.settings.isAutoBuild()) {
      final SortedSet<Long> suggestions = Logic.suggestCheckPoints(situation);
      if (suggestions == null || suggestions.isEmpty()) return;
      myBuildStarter.enqueue(situation, bt, suggestions);
    }
  }



}
