package org.jetbrains.teamcity.wbb;

import jetbrains.buildServer.serverSide.BuildHistory;
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
  private final WbbServerListener serverListener;



  public WbbInitializer(@NotNull final EventDispatcher<BuildServerListener> eventDispatcher,
                        @NotNull final Situations situations,
                        @NotNull final WbbBuildStarter buildStarter,
                        @NotNull final BuildHistory buildHistory) {
    this.eventDispatcher = eventDispatcher;

    serverListener = new WbbServerListener(situations, buildStarter, buildHistory);
  }


  public void init() {
    //noinspection unchecked
    eventDispatcher.addListener(serverListener);
  }


}
