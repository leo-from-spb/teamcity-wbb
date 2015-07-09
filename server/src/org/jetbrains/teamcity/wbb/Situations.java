package org.jetbrains.teamcity.wbb;

import jetbrains.buildServer.BuildType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Leonid Bushuev from JetBrains
 **/
public class Situations {


  /**
   * Map of internal id -> situation
   */
  private Map<String,Situation> btSituations = new ConcurrentHashMap<String, Situation>();


  @NotNull
  public synchronized Situation getOrCreateFor(@NotNull final BuildType bt) {
    String id = bt.getBuildTypeId();
    Situation situation = btSituations.get(id);
    if (situation == null) {
      String name = bt.getExternalId();
      situation = new Situation(id, name);
      btSituations.put(id, situation);
    }
    return situation;
  }


}
