package org.jetbrains.teamcity.wbb;

import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SFinishedBuild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Leonid Bushuev from JetBrains
 **/
abstract class Logic {


  static void refreshSituation(@NotNull final Situation situation,
                               @NotNull final SBuildType bt) {
    final List<SFinishedBuild> history = bt.getHistory(null, false, true);
    long lastBuildId = history.isEmpty() ? 0 : history.get(0).getBuildId();
    if (situation.getLastKnownBuildId() == lastBuildId) return;

    // refresh
    final Incident incident = findIncident(history);
    situation.setIncident(incident);
    situation.setLastKnownBuildId(lastBuildId);
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

    int g = -1,
        r = -1;
    for (int i = 0; i < n && g < 0; i++) {
      SFinishedBuild b = history.get(i);
      if (b.isPersonal() || b.isInternalError() || b.getCanceledInfo() != null) continue;
      Status status = b.getBuildStatus();
      if (g < 0) {
        if (status.isSuccessful()) g = i;
        if (status.isFailed()) r = i; // sic! may be re-assigned several times,
                                      // we need the last red before the first green
      }
    }

    if (g < 0 || r < 0) return null;

    final SFinishedBuild greenBuild = history.get(g);
    final SFinishedBuild redBuild = history.get(r);

    return new Incident(greenBuild, redBuild);
  }

}
