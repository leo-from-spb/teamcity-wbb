<%@ include file="/include.jsp" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="sb" type="org.jetbrains.teamcity.wbb.SituationBean" scope="request"/>

<br/>

<div>

    <c:if test="${sb.situation.inIncident}">

        <table>

            <c:if test="${not empty sb.redBuild}">
                <tr>
                    <td colspan="7"><b>The first build that detects problems:</b></td>
                </tr>
                <tr>
                  <bs:buildRow build="${sb.redBuild}"
                               showBranchName="false"
                               showBuildNumber="true"
                               showStatus="true"
                               showArtifacts="true"
                               showCompactArtifacts="true"
                               showChanges="true"
                               showStartDate="true"
                               showDuration="true"
                               showProgress="false"
                               showStop="false"
                               showAgent="true"
                               showPin="false"
                               showTags="false"
                               showUsedByOtherBuildsIcon="true"/>
                </tr>
            </c:if>

            <tr>
                <td colspan="7">&nbsp;</td>
            </tr>

            <c:if test="${sb.hasIntermediateBuilds}">
                <tr>
                    <td colspan="7"><b>Intermediate builds:</b></td>
                </tr>
                <c:forEach items="${sb.intermediateBuilds}" var="ib">
                    <tr>
                        <c:if test="${not empty ib.queuedBuild}">
                            <bs:queuedBuild queuedBuild="${ib.queuedBuild}"
                                            estimateColspan="4"
                                            showNumber="true"
                                            showBranches="true"
                                            hideIcon="false"/>
                        </c:if>
                        <c:if test="${not empty ib.runningBuild}">
                            <bs:buildRow build="${ib.runningBuild}"
                                         showBranchName="false"
                                         showBuildNumber="true"
                                         showStatus="true"
                                         showArtifacts="true"
                                         showCompactArtifacts="true"
                                         showChanges="true"
                                         showStartDate="true"
                                         showDuration="true"
                                         showProgress="false"
                                         showStop="false"
                                         showAgent="true"
                                         showPin="false"
                                         showTags="false"
                                         showUsedByOtherBuildsIcon="true"/>
                        </c:if>
                    </tr>
                </c:forEach>
            </c:if>

            <tr>
                <td colspan="7">&nbsp;</td>
            </tr>

            <c:if test="${not empty sb.greenBuild}">
                <tr>
                    <td colspan="7"><b>The last successful build:</b></td>
                </tr>
                <tr>
                  <bs:buildRow build="${sb.greenBuild}"
                               showBranchName="false"
                               showBuildNumber="true"
                               showStatus="true"
                               showArtifacts="true"
                               showCompactArtifacts="true"
                               showChanges="true"
                               showStartDate="true"
                               showDuration="true"
                               showProgress="false"
                               showStop="false"
                               showAgent="true"
                               showPin="false"
                               showTags="false"
                               showUsedByOtherBuildsIcon="true"/>
                </tr>
            </c:if>

        </table>

        <br/>
        <form action="/wbb/button.html">
            <input type="button" value="Do Once" onclick="{return WbbForm.doAction('iteration')}"/>
            <input type="button" value="Auto ON" onclick="{return WbbForm.doAction('autoON')}"/>
            <input type="button" value="Auto OFF" onclick="{return WbbForm.doAction('autoOFF')}"/>
        </form>


    </c:if>

    <c:if test="${not empty sb.authors}">
        <br/>
        <div>
            <b>Suspected Authors:</b>
            <ul>
            <c:forEach items="${sb.authors}" var="a">
                <li><c:out value="${a.name}"/></li>
            </c:forEach>
            </ul>
        </div>
    </c:if>

    <c:if test="${not empty sb.autoAssignedUser}">
        <br/>
        Automatically assigned investigation to
        <b><c:out value="${sb.autoAssignedUser.name}"/></b>
    </c:if>


    <script type="text/javascript">

        WbbForm = {
            doAction: function(operation) {
                $j(":button").disabled = true;
                var q = window.location.search.substring(1);
                $j.ajax({
                            url: "/wbb/button.html?operation="+operation+"&"+q,
                            method: 'post',
                            dataType: 'json',
                            onSuccess: function(z) {
                                location.reload();
                            }
                        });
                return false;
            }
        }

    </script>


    
</div>