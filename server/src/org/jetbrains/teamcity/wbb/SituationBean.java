package org.jetbrains.teamcity.wbb;

import jetbrains.buildServer.serverSide.BuildHistoryEx;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SFinishedBuild;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.users.UserModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;

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





  public SituationBean(@NotNull final Situation situation,
                       @NotNull final SBuildType bt,
                       @NotNull final BuildHistoryEx buildHistory,
                       @NotNull final UserModel userModel) {
    mySituation = situation;
    myBt = bt;
    myBuildHistory = buildHistory;
    myUserModel = userModel;

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
