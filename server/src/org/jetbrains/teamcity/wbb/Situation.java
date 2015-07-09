package org.jetbrains.teamcity.wbb;

import org.jetbrains.annotations.Nullable;

/**
 * @author Leonid Bushuev from JetBrains
 **/
public class Situation {

  //// IDENTITY STATE AND SETTINGS \\\\

  /**
   * Internal id of the build configuration.
   */
  public final String btId;

  /**
   * External id of the build configuration.
   */
  public final String btName;


  // TODO load from build configuration
  public final WbbSettings settings = new WbbSettings();


  //// VARIABLE STATE \\\\

  private long myLastKnownBuildId;

  @Nullable
  private Incident myIncident;

  @Nullable
  private Track myTrack;


  private boolean myValid;




  public Situation(String btId, String btName) {
    this.btId = btId;
    this.btName = btName;
  }


  public long getLastKnownBuildId() {
    return myLastKnownBuildId;
  }

  public void setLastKnownBuildId(long lastKnownBuildId) {
    myLastKnownBuildId = lastKnownBuildId;
  }


  @Nullable
  public Incident getIncident() {
    return myIncident;
  }

  public void setIncident(@Nullable Incident incident) {
    myIncident = incident;
  }

  public boolean isInIncident() {
    return myIncident != null;
  }


  @Nullable
  public Track getTrack() {
    return myTrack;
  }

  public void setTrack(@Nullable Track track) {
    myTrack = track;
  }

  public boolean isTrackExist() {
    return myTrack != null && myTrack.miles != null && !myTrack.miles.isEmpty();
  }

  public boolean isValid() {
    return myValid;
  }

  public void setValid(boolean valid) {
    myValid = valid;
  }

}
