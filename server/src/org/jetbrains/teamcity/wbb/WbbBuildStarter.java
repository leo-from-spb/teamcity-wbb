package org.jetbrains.teamcity.wbb;

import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.serverSide.impl.BuildQueueImpl;
import jetbrains.buildServer.vcs.SVcsModification;
import jetbrains.buildServer.vcs.VcsModificationHistory;
import org.jetbrains.annotations.NotNull;

import java.util.SortedSet;

/**
 * @author Leonid Bushuev from JetBrains
 **/
public class WbbBuildStarter {

  @NotNull
  private final BuildCustomizerFactory myBuildCustomizerFactory;

  @NotNull
  private final VcsModificationHistory myModificationHistory;


  public WbbBuildStarter(@NotNull final BuildCustomizerFactory buildCustomizerFactory,
                         @NotNull final VcsModificationHistory modificationHistory) {
    myBuildCustomizerFactory = buildCustomizerFactory;
    myModificationHistory = modificationHistory;
  }


  void startIteration(@NotNull final Situation situation,
                      @NotNull final SBuildType bt) {
    final SortedSet<Long> suggestions = Logic.suggestCheckPoints(situation);
    if (suggestions == null || suggestions.isEmpty()) return;
    enqueue(situation, bt, suggestions);
  }


  void enqueue(@NotNull final Situation situation,
               @NotNull final SBuildType bt,
               @NotNull final SortedSet<Long> revisionsIds) {
    for (Long revisionId : revisionsIds) {
      enqueue(situation, bt, revisionId);
      situation.setValid(false);
    }
  }

  void enqueue(@NotNull final Situation situation,
               @NotNull final SBuildType bt,
               @NotNull final Long revisionId) {
    final SVcsModification modification = myModificationHistory.findChangeById(revisionId);
    if (modification == null) return;

    final BuildCustomizerEx bc = (BuildCustomizerEx) myBuildCustomizerFactory.createBuildCustomizer(bt, null);
    //bc.setDesiredBranchName(???);
    bc.setChangesUpTo(modification);
    //bc.setFreezeSettings(true);
    //bc.setStickToRevisions(true);
    final BuildPromotion promotion = bc.createPromotion();
    TriggeredByBuilder tbb = new TriggeredByBuilder();
    tbb.addParameter(BuildQueueImpl.TRIGGERED_BY_QUEUE_MERGING_ENABLED_PARAM, "false");
    promotion.addToQueue(tbb.toString());
  }



}
