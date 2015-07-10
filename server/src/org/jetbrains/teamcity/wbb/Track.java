package org.jetbrains.teamcity.wbb;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Leonid Bushuev from JetBrains
 **/
public class Track {

  public static class Mile {

    public final long authorId;

    @NotNull
    public final List<Revision> revisions;


    public Mile(long authorId, @NotNull List<Revision> revisions) {
      this.authorId = authorId;
      this.revisions = ImmutableList.copyOf(revisions);
      assert !revisions.isEmpty();
    }


    @NotNull
    public Long getLastModification() {
      int n = revisions.size();
      return revisions.get(n - 1).id;
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


  public SortedSet<Long> getAllChangeIds() {
    SortedSet<Long> ids = new TreeSet<Long>();
    for (Mile mile : miles) {
      for (Revision revision : mile.revisions) ids.add(revision.id);
    }
    return ids;
  }

}
