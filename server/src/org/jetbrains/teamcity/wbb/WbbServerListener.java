package org.jetbrains.teamcity.wbb;

import jetbrains.buildServer.responsibility.BuildTypeResponsibilityFacade;
import jetbrains.buildServer.responsibility.ResponsibilityEntry;
import jetbrains.buildServer.responsibility.ResponsibilityEntryFactory;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.users.UserModel;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;

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
          "Automatically assigned by WBB plugin.";



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
        final Track.Mile revealedMile = track.getRevealedMile();
        if (revealedMile == null) return;
        long authorId = revealedMile.authorId;
        SUser author = myUserModel.findUserById(authorId);
        if (author == null) return;
        ResponsibilityEntry responsibility =
                myResponsibilityFacade.findBuildTypeResponsibility(bt);
        if (responsibility != null && responsibility.getState() == ResponsibilityEntry.State.TAKEN) return; // already taken

        final List<Revision> revisions = revealedMile.revisions;
        StringBuilder message = new StringBuilder(AUTO_ASSIGNED_COMMENT);
        message.append('\n');
        message.append(revisions.size() == 1
                       ? "The change that might broken the build:\n"
                       : "Changes that might broken the build:\n");
        for (Revision revision : revisions) {
          message.append(revision.comment);
          if (!revision.comment.endsWith("\n")) message.append('\n');
        }

        ResponsibilityEntry newEntry =
                ResponsibilityEntryFactory.createEntry(bt,
                                                       ResponsibilityEntry.State.TAKEN,
                                                       author,
                                                       null,
                                                       new Date(),
                                                       message.toString(),
                                                       ResponsibilityEntry.RemoveMethod.WHEN_FIXED);
        myResponsibilityFacade.setBuildTypeResponsibility(bt, newEntry);
        situation.setAssignedToUserId(authorId);
        situation.setState(Situation.State.DONE);
      }
    }
    else {
      if (situation.settings.isAutoActivate()) {
        myBuildStarter.startIteration(situation, bt);
      }
    }

  }



}
