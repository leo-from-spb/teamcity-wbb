package org.jetbrains.teamcity.wbb;

import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.util.Couple;
import jetbrains.buildServer.vcs.SVcsModification;
import jetbrains.buildServer.vcs.SelectPrevBuildPolicy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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
    final Couple<SFinishedBuild> incidentBuilds = findIncidentBuilds(bt, bh);
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
  static Incident findIncident(@NotNull final SBuildType bt,
                               @NotNull final BuildHistory bh) {
    final Couple<SFinishedBuild> builds = findIncidentBuilds(bt, bh);
    return builds != null ? new Incident(builds) : null;
  }



  @Nullable
  static Couple<SFinishedBuild> findIncidentBuilds(@NotNull final SBuildType bt, BuildHistory bh) {
    SFinishedBuild lastBuild = getLastBuild(bt);
    if (lastBuild == null) return null;
    if (!lastBuild.getBuildStatus().isFailed()) return null;

    final BuildTypeEx bte = (BuildTypeEx) bt;
    final BuildTypeOrderedBuilds ob = bte.getBuildTypeOrderedBuilds();

    SFinishedBuild rb,
                   gb = null;
    boolean incidentFound = false;
    rb = lastBuild;
    SFinishedBuild nb = getPrevBuild(rb, ob, bh);
    while (nb != null) {
      Status status = nb.getBuildStatus();
      if (status.isFailed()) {
        rb = nb;
        nb = getPrevBuild(rb, ob, bh);
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
  private static SFinishedBuild getPrevBuild(SBuild rb, BuildTypeOrderedBuilds obs, BuildHistory bh) {
    final List<OrderedBuild> list = obs.getBuildsBefore(rb);
    if (list.isEmpty()) return null;
    for (OrderedBuild ob : list) {
      if (ob.isCanceled() || ob.isPersonal() || !ob.isFinishedBuild()) continue;
      long buildId = ob.getBuildId();
      SFinishedBuild build = bh.findEntry(buildId);
      if (build != null) return build;
    }
    return null;
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


  @Nullable
  static SortedSet<Long> suggestCheckPoints(@NotNull final Situation situation) {
    final Track track = situation.getTrack();
    if (track == null) return null;
    final List<Track.Mile> miles = track.miles;
    if (miles == null || miles.size() < 2) return null;

    SortedSet<Long> suggestions = new TreeSet<Long>();

    final ArrayList<Track.Mile> backMiles = new ArrayList<Track.Mile>(miles);
    Collections.reverse(backMiles);

    int innerPoints = backMiles.size()-1;
    int slots = situation.settings.getParallelLimit();  // TODO minus already queued/running
    double delta = (innerPoints + 1.0) / (slots + 1.0);
    for (int i = 0; i < slots; i++) {
      int index = (int) Math.round(i * delta);
      if (index >= backMiles.size()) continue;
      Long point = backMiles.get(index).getLastModification();
      suggestions.add(point);
    }

    return suggestions;
  }



}
