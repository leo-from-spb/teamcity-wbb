package org.jetbrains.teamcity.wbb;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Leonid Bushuev from JetBrains
 **/
public final class IntermediateBuild implements Comparable<IntermediateBuild> {

  public final long modificationId;

  public final long promotionId;

  public final long buildId;

  @Nullable
  public final String queuedItemId;

  public final boolean running;


  public IntermediateBuild(long modificationId, long promotionId, @NotNull String queuedItemId) {
    this.modificationId = modificationId;
    this.promotionId = promotionId;
    this.queuedItemId = queuedItemId;
    this.buildId = 0;
    this.running = false;
  }

  public IntermediateBuild(long modificationId, long promotionId, long buildId) {
    this.modificationId = modificationId;
    this.promotionId = promotionId;
    this.buildId = buildId;
    this.queuedItemId = null;
    this.running = true;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    IntermediateBuild that = (IntermediateBuild) o;

    if (modificationId != that.modificationId) return false;
    if (promotionId != that.promotionId) return false;
    if (buildId != that.buildId) return false;
    if (running != that.running) return false;
    return !(queuedItemId != null ? !queuedItemId.equals(that.queuedItemId) : that.queuedItemId != null);

  }

  @Override
  public int hashCode() {
    return (int) (modificationId ^ (modificationId >>> 32));
  }


  @Override
  public int compareTo(IntermediateBuild that) {
    if (this == that) return 0;

    int z = compareLongs(this.modificationId, that.modificationId);
    if (z == 0) z = compareLongs(this.promotionId, that.promotionId);
    if (z == 0) z = compareLongs(this.buildId, that.buildId);
    return z;
  }


  private static int compareLongs(long a, long b) {
    if (a < b) return -1;
    if (a > b) return +1;
    return 0;
  }
}
