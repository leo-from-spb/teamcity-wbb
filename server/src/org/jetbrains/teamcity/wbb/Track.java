package org.jetbrains.teamcity.wbb;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Leonid Bushuev from JetBrains
 **/
public class Track {

  public static class Mile {

    public final long authorId;

    @NotNull
    public final List<Long> modificationIds;


    public Mile(long authorId, @NotNull List<Long> modificationIds) {
      this.authorId = authorId;
      this.modificationIds = modificationIds;
    }
  }


  public final List<Mile> miles;


  public Track(List<Mile> miles) {
    this.miles = miles;
  }


  public boolean hasChanges() {
    return miles.size() > 0;
  }

  public boolean isMileRevealed() {
    return miles.size() == 1;
  }

  public boolean isAuthorRevealed() {
    return miles.size() == 1 && miles.get(0).authorId > 0;
  }

  public long getRevealedAuthorId() {
    return miles.size() == 1 ? miles.get(0).authorId : 0;
  }

}
