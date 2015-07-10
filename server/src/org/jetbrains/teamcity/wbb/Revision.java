package org.jetbrains.teamcity.wbb;

/**
 * @author Leonid Bushuev from JetBrains
 **/
public final class Revision {

  public final long id;

  public final String comment;


  public Revision(long id, String comment) {
    this.id = id;
    this.comment = comment;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Revision revision = (Revision) o;

    if (id != revision.id) return false;
    return !(comment != null ? !comment.equals(revision.comment) : revision.comment != null);

  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }

  @Override
  public String toString() {
    String r = Long.toString(id);
    if (comment != null) r += ':' + comment;
    return  r;
  }
}
