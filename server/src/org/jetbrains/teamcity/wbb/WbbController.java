package org.jetbrains.teamcity.wbb;

import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Leonid Bushuev from JetBrains
 **/
public class WbbController extends BaseController {

  @NotNull
  private final Situations mySituations;

  @NotNull
  private final WbbBuildStarter myBuildStarter;

  @NotNull
  private final ProjectManager myProjectManager;

  @NotNull
  private final BuildHistory myBuildHistory;

  @NotNull
  private final BuildQueue myBuildQueue;

  @NotNull
  private final RunningBuildsManager myRunningBuildsManager;



  public WbbController(@NotNull final SBuildServer server,
                       @NotNull final WebControllerManager wcm,
                       @NotNull final Situations situations,
                       @NotNull final WbbBuildStarter buildStarter,
                       @NotNull final ProjectManager projectManager,
                       @NotNull final BuildHistory buildHistory,
                       @NotNull final BuildQueue buildQueue,
                       @NotNull final RunningBuildsManager runningBuildsManager) {
    super(server);
    mySituations = situations;
    myBuildStarter = buildStarter;
    myProjectManager = projectManager;
    myBuildHistory = buildHistory;
    myBuildQueue = buildQueue;
    myRunningBuildsManager = runningBuildsManager;
    wcm.registerController("/wbb/**", this);
  }

  @Nullable
  @Override
  protected ModelAndView doHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws Exception {
    String req = request.getRequestURL().toString().toLowerCase();
    Map<String, String[]> parameters = new TreeMap<String, String[]>(String.CASE_INSENSITIVE_ORDER);
    parameters.putAll(request.getParameterMap());
    String operation = getParameter(parameters, "operation");
    if (operation == null) return null;
    String btName = getParameter(parameters, "buildTypeId");
    if (btName == null) return null;

    if (operation.equalsIgnoreCase("iteration")) {
      doIterateOnce(btName);
    }
    if (operation.equalsIgnoreCase("autoON")) {
      doAutoOn(btName);
    }
    if (operation.equalsIgnoreCase("autoOFF")) {
      doAutoOff(btName);
    }

    return null;
  }

  private String getParameter(Map<String, String[]> parameters, final String parameterName) {
    String[] pa = parameters.get(parameterName);
    if (pa == null || pa.length == 0) return null;
    return pa[0];
  }

  private void doIterateOnce(@NotNull final String btName) {
    final SBuildType bt = myProjectManager.findBuildTypeByExternalId(btName);
    if (bt == null) return;
    final Situation situation = mySituations.getOrCreateFor(bt);
    Logic.refreshSituation(situation, bt, myBuildHistory, myBuildQueue, myRunningBuildsManager);
    if (situation.isInIncident()) {
      myBuildStarter.startIteration(situation, bt);
    }
  }

  private void doAutoOn(@NotNull final String btName) {
    final SBuildType bt = myProjectManager.findBuildTypeByExternalId(btName);
    if (bt == null) return;
    final Situation situation = mySituations.getOrCreateFor(bt);
    situation.settings.setAutoAssign(true);
    Logic.refreshSituation(situation, bt, myBuildHistory, myBuildQueue, myRunningBuildsManager);
    if (situation.isInIncident()) {
      myBuildStarter.startIteration(situation, bt);
    }
  }

  private void doAutoOff(@NotNull final String btName) {
    final SBuildType bt = myProjectManager.findBuildTypeByExternalId(btName);
    if (bt == null) return;
    final Situation situation = mySituations.getOrCreateFor(bt);
    situation.settings.setAutoAssign(false);
  }


}
