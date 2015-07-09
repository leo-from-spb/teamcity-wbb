package org.jetbrains.teamcity.wbb;

import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.serverSide.BuildHistory;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SFinishedBuild;
import jetbrains.buildServer.vcs.SVcsModification;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    final List<SFinishedBuild> history = bt.getHistory(null, false, true);
    final Incident incident = findIncident(history);
    situation.setIncident(incident);
    if (incident == null) return;

    final SFinishedBuild redBuild = bh.findEntry(incident.getRedBuildId());
    final SFinishedBuild greenBuild = bh.findEntry(incident.getGreenBuildId());

    if (redBuild == null) return;

    final List<SVcsModification> containingChanges = redBuild.getContainingChanges();

  }


  @Nullable
  static Incident findIncident(@NotNull final SBuildType bt) {
    final List<SFinishedBuild> history = bt.getHistory(null, false, true);
    return findIncident(history);
  }

  @Nullable
  private static Incident findIncident(@NotNull final List<SFinishedBuild> history) {
    final int n = history.size();
    if (n < 2) return null;

    int r = -1,
        g = -1;
    for (int i = 0; i < n && g < 0; i++) {
      SFinishedBuild b = history.get(i);
      if (b.isPersonal() || b.isInternalError() || b.getCanceledInfo() != null) continue;
      Status status = b.getBuildStatus();
      if (g < 0) {
        if (status.isFailed()) r = i; // sic! may be re-assigned several times,
                                      // we need the last red before the first green
        if (status.isSuccessful()) g = i;
      }
    }

    if (g < 0 || r < 0) return null;

    final SFinishedBuild redBuild = history.get(r);
    final SFinishedBuild greenBuild = history.get(g);

    return new Incident(greenBuild, redBuild);
  }

}
