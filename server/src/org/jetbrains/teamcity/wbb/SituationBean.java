package org.jetbrains.teamcity.wbb;

import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.users.UserModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SortedSet;

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

  @Nullable
  private LinkedHashSet<SUser> myAuthors;



  @NotNull
  private final BuildHistoryEx myBuildHistory;

  @NotNull
  private final UserModel myUserModel;

  @NotNull
  private final BuildQueue myBuildQueue;

  @NotNull
  private final RunningBuildsManager myRunningBuildsManager;




  public SituationBean(@NotNull final Situation situation,
                       @NotNull final SBuildType bt,
                       @NotNull final BuildHistoryEx buildHistory,
                       @NotNull final UserModel userModel,
                       @NotNull final BuildQueue buildQueue,
                       @NotNull final RunningBuildsManager runningBuildsManager) {
    mySituation = situation;
    myBt = bt;
    myBuildHistory = buildHistory;
    myUserModel = userModel;
    myBuildQueue = buildQueue;
    myRunningBuildsManager = runningBuildsManager;

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


  public static final class IB {
    final boolean myRunning;
    final SQueuedBuild myQueuedBuild;
    final SRunningBuild myRunningBuild;

    public IB(SQueuedBuild queuedBuild) {
      myRunning = false;
      myQueuedBuild = queuedBuild;
      myRunningBuild = null;
    }

    public IB(SRunningBuild runningBuild) {
      myRunning = true;
      myQueuedBuild = null;
      myRunningBuild = runningBuild;
    }

    public boolean isRunning() { return myRunning; }
    public SQueuedBuild getQueuedBuild() { return myQueuedBuild; }
    public SRunningBuild getRunningBuild() { return myRunningBuild; }
  }

  public boolean isHasIntermediateBuilds() {
    final SortedSet<IntermediateBuild> intermediateBuilds = mySituation.getIntermediateBuilds();
    return intermediateBuilds != null && !intermediateBuilds.isEmpty();
  }

  public List<IB> getIntermediateBuilds() {
    final List<IB> result = new ArrayList<IB>(4);
    final SortedSet<IntermediateBuild> ibs = mySituation.getIntermediateBuilds();
    if (ibs != null) {
      for (IntermediateBuild ib : ibs) {
        if (ib.running) {
          SRunningBuild runningBuild = myRunningBuildsManager.findRunningBuildById(ib.buildId);
          if (runningBuild != null) result.add(0, new IB(runningBuild));
        }
        else {
          //noinspection ConstantConditions
          final SQueuedBuild queuedBuild = myBuildQueue.findQueued(ib.queuedItemId);
          if (queuedBuild != null) result.add(0, new IB(queuedBuild));
        }
      }
    }
    return result;
  }




  @Nullable
  public synchronized LinkedHashSet<SUser> getAuthors() {
    if (myAuthors == null) {
      myAuthors = new LinkedHashSet<SUser>();
      boolean wasUnknownUser = false;  // TODO
      if (mySituation.isTrackExist()) {
        //noinspection ConstantConditions
        for (Track.Mile mile : mySituation.getTrack().miles) {
          if (mile.authorId == 0) {
            wasUnknownUser = true;
            continue;
          }
          final SUser author = myUserModel.findUserById(mile.authorId);
          myAuthors.add(author);
        }
      }
    }
    return myAuthors;
  }

  @Nullable
  public SUser getAutoAssignedUser() {
    final long aauId = getSituation().getAssignedToUserId();
    if (aauId == 0) return null;
    return myUserModel.findUserById(aauId);
  }

}
