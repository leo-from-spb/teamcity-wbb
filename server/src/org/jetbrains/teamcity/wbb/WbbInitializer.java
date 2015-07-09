package org.jetbrains.teamcity.wbb;

import jetbrains.buildServer.serverSide.BuildServerListener;
import jetbrains.buildServer.util.EventDispatcher;
import org.jetbrains.annotations.NotNull;

/**
 * @author Leonid Bushuev from JetBrains
 **/
public class WbbInitializer {

  //// BEANS FROM TEAMCITY \\\\

  @NotNull
  private final EventDispatcher<BuildServerListener> eventDispatcher;


  //// MY SERVICE CLASSES \\\\

  @NotNull
  private final PrimaryServerListener serverListener;



  public WbbInitializer(@NotNull final EventDispatcher<BuildServerListener> eventDispatcher) {
    this.eventDispatcher = eventDispatcher;

    serverListener = new PrimaryServerListener();
  }


  public void init() {
    //noinspection unchecked
    eventDispatcher.addListener(serverListener);
  }


}
