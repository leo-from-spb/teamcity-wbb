package org.jetbrains.teamcity.wbb;

import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.BuildHistory;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SBuildType;
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


  public WbbController(@NotNull final SBuildServer server,
                       @NotNull final WebControllerManager wcm,
                       @NotNull final Situations situations,
                       @NotNull final WbbBuildStarter buildStarter,
                       @NotNull final ProjectManager projectManager,
                       @NotNull final BuildHistory buildHistory) {
    super(server);
    mySituations = situations;
    myBuildStarter = buildStarter;
    myProjectManager = projectManager;
    myBuildHistory = buildHistory;
    wcm.registerController("/wbb/**", this);
  }

  @Nullable
  @Override
  protected ModelAndView doHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws Exception {
    String req = request.getRequestURL().toString().toLowerCase();
    Map<String, String[]> parameters = new TreeMap<String, String[]>(String.CASE_INSENSITIVE_ORDER);
    parameters.putAll(request.getParameterMap());
    String btName = getParameter(parameters);
    if (req.contains("/once.html")) {
      doIterateOnce(btName);
    }
    return null;
  }

  private String getParameter(Map<String, String[]> parameters) {
    String[] pa = parameters.get("buildTypeId");
    if (pa == null || pa.length == 0) return null;
    return pa[0];
  }

  private void doIterateOnce(String btName) {
    if (btName == null) return;
    final SBuildType bt = myProjectManager.findBuildTypeByExternalId(btName);
    if (bt == null) return;
    final Situation situation = mySituations.getOrCreateFor(bt);
    Logic.refreshSituation(situation, bt, myBuildHistory);
    if (situation.isInIncident()) {
      myBuildStarter.startIteration(situation, bt);
    }
  }


}
