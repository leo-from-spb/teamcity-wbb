<%@ include file="/include.jsp" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:useBean id="sb" type="org.jetbrains.teamcity.wbb.SituationBean" scope="request"/>

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

            <c:if test="${not empty sb.redBuild and not empty sb.greenBuild}">
                <tr>
                    <td colspan="7">&nbsp;</td>
                </tr>
            </c:if>

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


    </c:if>

    <c:if test="${not empty sb.authors}">
        <br/>
        <div>
            <b>Suspected Authors:</b>
            <ul>
            <c:forEach items="${sb.authors}" var="a">
                <li>${a.name}</li>
            </c:forEach>
            </ul>
        </div>
    </c:if>


</div>