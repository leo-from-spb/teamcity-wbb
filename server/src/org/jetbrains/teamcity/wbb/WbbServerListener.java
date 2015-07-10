package org.jetbrains.teamcity.wbb;

import jetbrains.buildServer.responsibility.BuildTypeResponsibilityFacade;
import jetbrains.buildServer.responsibility.ResponsibilityEntry;
import jetbrains.buildServer.responsibility.ResponsibilityEntryFactory;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.users.UserModel;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

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

  @NotNull
  private final BuildTypeResponsibilityFacade myResponsibilityFacade;

  @NotNull
  private final UserModel myUserModel;

  @NotNull
  private final BuildQueue myBuildQueue;

  @NotNull
  private final RunningBuildsManager myRunningBuildsManager;


  private static final String AUTO_ASSIGNED_COMMENT =
          "Automatically assigned by WBB plugin";



  public WbbServerListener(@NotNull final Situations situations,
                           @NotNull final WbbBuildStarter buildStarter,
                           @NotNull final BuildHistory buildHistory,
                           @NotNull final BuildTypeResponsibilityFacade responsibilityFacade,
                           @NotNull final UserModel userModel,
                           @NotNull final BuildQueue buildQueue,
                           @NotNull final RunningBuildsManager runningBuildsManager) {
    mySituations = situations;
    myBuildStarter = buildStarter;
    myBuildHistory = buildHistory;
    myResponsibilityFacade = responsibilityFacade;
    myUserModel = userModel;
    myBuildQueue = buildQueue;
    myRunningBuildsManager = runningBuildsManager;
  }


  @Override
  public void buildFinished(@NotNull SRunningBuild build) {
    if (build.isPersonal()) return;

    final SBuildType bt = build.getBuildType();
    if (bt == null) return;

    final Situation situation = mySituations.getOrCreateFor(bt);
    situation.setValid(false);
    Logic.refreshSituation(situation, bt, myBuildHistory, myBuildQueue, myRunningBuildsManager);

    final Track track = situation.getTrack();
    if (track == null) return;

    if (track.isAuthorRevealed()) {
      if (!situation.isAlreadyAssigned()) {
        long authorId = track.getRevealedAuthorId();
        SUser author = myUserModel.findUserById(authorId);
        if (author == null) return;
        ResponsibilityEntry responsibility =
                myResponsibilityFacade.findBuildTypeResponsibility(bt);
        if (responsibility != null && responsibility.getState() == ResponsibilityEntry.State.TAKEN) return; // already taken

        ResponsibilityEntry newEntry =
                ResponsibilityEntryFactory.createEntry(bt,
                                                       ResponsibilityEntry.State.TAKEN,
                                                       author,
                                                       null,
                                                       new Date(),
                                                       AUTO_ASSIGNED_COMMENT,
                                                       ResponsibilityEntry.RemoveMethod.WHEN_FIXED);
        myResponsibilityFacade.setBuildTypeResponsibility(bt, newEntry);
        situation.setAssignedToUserId(authorId);
      }
    }
    else {
      if (situation.settings.isAutoBuild()) {
        myBuildStarter.startIteration(situation, bt);
      }
    }

  }



}
