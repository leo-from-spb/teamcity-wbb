package org.jetbrains.teamcity.wbb;

import jetbrains.buildServer.responsibility.BuildTypeResponsibilityFacade;
import jetbrains.buildServer.serverSide.BuildHistory;
import jetbrains.buildServer.serverSide.BuildQueue;
import jetbrains.buildServer.serverSide.BuildServerListener;
import jetbrains.buildServer.serverSide.RunningBuildsManager;
import jetbrains.buildServer.users.UserModel;
import jetbrains.buildServer.util.EventDispatcher;
import org.jetbrains.annotations.NotNull;

/**
 * @author Leonid Bushuev from JetBrains
 **/
public class WbbInitializer {

  //// BEANS FROM TEAMCITY \\\\

  @NotNull
  private final EventDispatcher<BuildServerListener> eventDispatcher;

  @NotNull
  private final BuildQueue myBuildQueue;

  @NotNull
  private final RunningBuildsManager myRunningBuildsManager;



  //// MY SERVICE CLASSES \\\\

  @NotNull
  private final WbbServerListener serverListener;



  public WbbInitializer(@NotNull final EventDispatcher<BuildServerListener> eventDispatcher,
                        @NotNull final Situations situations,
                        @NotNull final WbbBuildStarter buildStarter,
                        @NotNull final BuildHistory buildHistory,
                        @NotNull final BuildTypeResponsibilityFacade responsibilityFacade,
                        @NotNull final UserModel userModel,
                        @NotNull final BuildQueue buildQueue,
                        @NotNull final RunningBuildsManager runningBuildsManager) {
    this.eventDispatcher = eventDispatcher;
    myBuildQueue = buildQueue;
    myRunningBuildsManager = runningBuildsManager;

    serverListener = new WbbServerListener(situations,
                                           buildStarter,
                                           buildHistory,
                                           responsibilityFacade,
                                           userModel,
                                           buildQueue,
                                           runningBuildsManager);
  }


  public void init() {
    eventDispatcher.addListener(serverListener);
  }


}
