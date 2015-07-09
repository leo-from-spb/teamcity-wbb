package org.jetbrains.teamcity.wbb;

import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SFinishedBuild;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.util.positioning.PositionAware;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import jetbrains.buildServer.web.openapi.buildType.BuildTypeTab;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author Leonid Bushuev from JetBrains
 **/
public class WbbTab extends BuildTypeTab // implements PositionAware
{

  public static final String WBB_TAB_CODE = "WBB";
  public static final String WBB_TAB_NAME = "WBB";


  public WbbTab(@NotNull final WebControllerManager webControllerManager,
                @NotNull final ProjectManager projectManager,
                @NotNull final PluginDescriptor pluginDescriptor) {
    super(WBB_TAB_CODE, WBB_TAB_NAME, webControllerManager, projectManager);
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
                           @NotNull SBuildType buildType,
                           @Nullable SUser user) {

  }


  @Override
  public boolean isAvailable(@NotNull HttpServletRequest request) {
    final SBuildType bt = getBuildType(request);
    if (bt == null) return false;
    final List<SFinishedBuild> history = bt.getHistory(null, false, true);
    int n = history.size();
    if (n < 2) return false;
    final SFinishedBuild topBuild = history.get(0);
    if (topBuild.getBuildStatus().isSuccessful()) return false;

    return super.isAvailable(request);
  }

}
