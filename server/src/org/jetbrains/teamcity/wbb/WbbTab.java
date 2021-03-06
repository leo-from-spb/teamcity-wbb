package org.jetbrains.teamcity.wbb;

import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.users.UserModel;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import jetbrains.buildServer.web.openapi.buildType.BuildTypeTab;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static org.jetbrains.teamcity.wbb.Logic.refreshSituation;

/**
 * @author Leonid Bushuev from JetBrains
 **/
public class WbbTab extends BuildTypeTab // implements PositionAware
{

  public static final String WBB_TAB_CODE = "WBB";
  public static final String WBB_TAB_NAME = "WBB";


  private final Situations mySituations;

  @NotNull
  private final BuildHistoryEx myBuildHistory;

  @NotNull
  private final UserModel myUserModel;

  @NotNull
  private final BuildQueue myBuildQueue;

  @NotNull
  private final RunningBuildsManager myRunningBuildsManager;



  public WbbTab(@NotNull final Situations situations,
                @NotNull final WebControllerManager webControllerManager,
                @NotNull final ProjectManager projectManager,
                @NotNull final PluginDescriptor pluginDescriptor,
                @NotNull final BuildHistoryEx buildHistory,
                @NotNull final UserModel userModel,
                @NotNull final BuildQueue buildQueue,
                @NotNull final RunningBuildsManager runningBuildsManager)
  {
    super(WBB_TAB_CODE, WBB_TAB_NAME, webControllerManager, projectManager);
    this.mySituations = situations;
    myBuildHistory = buildHistory;
    myUserModel = userModel;
    myBuildQueue = buildQueue;
    myRunningBuildsManager = runningBuildsManager;
    setPluginName("WBB");
    setIncludeUrl(pluginDescriptor.getPluginResourcesPath("/WBB.jsp"));
    setTabTitle("Who Broke Build");
    setPosition(jetbrains.buildServer.web.openapi.PositionConstraint.first());
    //setPosition(jetbrains.buildServer.web.openapi.PositionConstraint.after("buildTypeHistoryList"));
  }


  /*
  @NotNull
  @Override
  public String getOrderId() {
    return WBB_TAB_CODE;
  }

  @NotNull
  @Override
  public jetbrains.buildServer.util.positioning.PositionConstraint getConstraint() {
    return jetbrains.buildServer.util.positioning.PositionConstraint.after("buildTypeHistoryList");
  }
  */


  @Override
  protected void fillModel(@NotNull Map<String, Object> model,
                           @NotNull HttpServletRequest request,
                           @NotNull SBuildType bt,
                           @Nullable SUser user) {
    final Situation situation = mySituations.getOrCreateFor(bt);
    refreshSituation(situation, bt, myBuildHistory, myBuildQueue, myRunningBuildsManager);

    SituationBean bean = new SituationBean(situation, bt, myBuildHistory, myUserModel, myBuildQueue, myRunningBuildsManager);
    model.put("sb", bean);
  }


  @Override
  public boolean isAvailable(@NotNull HttpServletRequest request) {
    final SBuildType bt = getBuildType(request);
    if (bt == null) return false;

    final Situation situation = mySituations.getOrCreateFor(bt);
    refreshSituation(situation, bt, myBuildHistory, myBuildQueue, myRunningBuildsManager);
    return situation.isInIncident();
  }

}
