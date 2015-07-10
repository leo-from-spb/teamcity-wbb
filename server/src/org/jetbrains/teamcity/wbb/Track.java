package org.jetbrains.teamcity.wbb;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Leonid Bushuev from JetBrains
 **/
public class Track {

  public static class Mile {

    public final long authorId;

    @NotNull
    public final List<Revision> modificationIds;


    public Mile(long authorId, @NotNull List<Revision> modificationIds) {
      this.authorId = authorId;
      this.modificationIds = ImmutableList.copyOf(modificationIds);
      assert !modificationIds.isEmpty();
    }


    @NotNull
    public Long getLastModification() {
      int n = modificationIds.size();
      return modificationIds.get(n - 1).id;
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
