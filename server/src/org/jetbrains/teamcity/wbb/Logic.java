package org.jetbrains.teamcity.wbb;

import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.serverSide.BuildHistory;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SFinishedBuild;
import jetbrains.buildServer.util.Couple;
import jetbrains.buildServer.vcs.SVcsModification;
import jetbrains.buildServer.vcs.SelectPrevBuildPolicy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Leonid Bushuev from JetBrains
 **/
abstract class Logic {


  static void refreshSituation(@NotNull final Situation situation,
                               @NotNull final SBuildType bt,
                               @NotNull final BuildHistory bh) {
    boolean valid = situation.isValid();
    if (valid) {
      final List<SFinishedBuild> history1 = bt.getHistory(null, true, false);
      long lastBuildId = history1.isEmpty() ? 0 : history1.get(0).getBuildId();
      valid = situation.getLastKnownBuildId() == lastBuildId;
      situation.setLastKnownBuildId(lastBuildId);
    }

    if (valid) return;

    // refresh
    final Couple<SFinishedBuild> incidentBuilds = findIncidentBuilds(bt);
    if (incidentBuilds == null) {
      situation.setIncident(null);
      situation.setTrack(null);
      situation.setValid(true);
      return;
    }

    final Incident incident = new Incident(incidentBuilds);
    situation.setIncident(incident);

    final Track track = analyzeChanges(incidentBuilds);
    situation.setTrack(track);
  }




  @Nullable
  static Incident findIncident(@NotNull final SBuildType bt) {
    final Couple<SFinishedBuild> builds = findIncidentBuilds(bt);
    return builds != null ? new Incident(builds) : null;
  }


  @Nullable
  static Couple<SFinishedBuild> findIncidentBuilds(@NotNull final SBuildType bt) {
    SFinishedBuild lastBuild = getLastBuild(bt);
    if (lastBuild == null) return null;
    if (!lastBuild.getBuildStatus().isFailed()) return null;

    SFinishedBuild rb,
                   gb = null;
    boolean incidentFound = false;
    rb = lastBuild;
    SFinishedBuild nb = rb.getPreviousFinished();
    while (nb != null) {
      Status status = nb.getBuildStatus();
      if (status.isFailed()) {
        rb = nb;
        nb = rb.getPreviousFinished();
        continue;
      }
      if (status.isSuccessful()) {
        gb = nb;
        incidentFound = true;
        break;
      }
      nb = nb.getPreviousFinished();
    }
    if (!incidentFound) return null;

    return Couple.of(rb, gb);
  }

  @Nullable
  private static SFinishedBuild getLastBuild(@NotNull SBuildType bt) {
    final List<SFinishedBuild> history = bt.getHistory(null, false, true);
    return history.isEmpty() ? null : history.get(0);
    // return bt.getLastChangesFinished(); // it's only from master branch
  }


  static Track analyzeChanges(@NotNull final Couple<SFinishedBuild> builds) {
    SFinishedBuild rb = builds.a,
                   gb = builds.b;
    final List<SVcsModification> changes =
            rb.getChanges(SelectPrevBuildPolicy.SINCE_LAST_SUCCESSFULLY_FINISHED_BUILD, false);

    ArrayList<Track.Mile> miles = new ArrayList<Track.Mile>(changes.size());
    ArrayList<Long> currentChanges = new ArrayList<Long>(changes.size());
    long currentAuthor = 0;
    for (SVcsModification change : changes) {
      long author = getAuthor(change);
      if (currentAuthor != author) {
        if (!currentChanges.isEmpty()) miles.add(new Track.Mile(currentAuthor, currentChanges));
        currentChanges.clear();
        currentAuthor = author;
      }
      currentChanges.add(change.getId());
    }
    if (!currentChanges.isEmpty()) miles.add(new Track.Mile(currentAuthor, currentChanges));

    return new Track(miles);
  }

  private static long getAuthor(SVcsModification change) {
    final List<Long> committerIds = change.getCommitterIds();
    if (committerIds.isEmpty()) return 0;
    else return committerIds.get(0);
  }


}
