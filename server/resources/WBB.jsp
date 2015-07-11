<%@ include file="/include.jsp" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="sb" type="org.jetbrains.teamcity.wbb.SituationBean" scope="request"/>

<br/>

<style type="text/css">

    .dig {
        color: mediumvioletred;
    }

</style>



<div>

    <c:if test="${sb.situation.inIncident}">

        <table width="90%">

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


    </c:if>

    <c:if test="${sb.hasTrack}">
        <br/>
        <div>
            <b>Changes that might be the reason of the problem:</b><br/>
            <b class="dig">${sb.changeCount}</b> changes (or <b class="dig">${sb.groupedChangeCount}</b> change groups, grouped by authors).<br>
            <br/>
            <c:if test="${not empty sb.authors}">
                <div>
                    Suspected Authors:
                    <ul>
                    <c:forEach items="${sb.authors}" var="a">
                        <li><c:out value="${a.name}"/></li>
                    </c:forEach>
                    </ul>
                </div>
            </c:if>
        </div>
    </c:if>


    <c:if test="${not empty sb.autoAssignedUser}">
        <br/>
        <p>
        Investigation was automatically assigned to
        <b><c:out value="${sb.autoAssignedUser.name}"/></b>
        </p>
        <br/>
    </c:if>


    <br/>
    <div>
        <b>Settings:</b>
        <table>
            <tr>
                <td>Start to find who broke build automatically:</td>
                <td>${sb.autoStart}</td>
            </tr>
            <tr>
                <td>Automatically assign investigations:</td>
                <td>${sb.autoAssign}</td>
            </tr>
            <tr>
                <td>Double check:</td>
                <td>${sb.doubleCheck}</td>
            </tr>
            <tr>
                <td>Maximum parallel builds:</td>
                <td>${sb.parallelLimit}</td>
            </tr>
        </table>

        <br/>
        <form action="/wbb/button.html">
            <input type="button" value="Do Once" onclick="{return WbbForm.doAction('iteration')}"/>
            <input type="button" value="Auto ON" onclick="{return WbbForm.doAction('autoON')}"/>
            <input type="button" value="Auto OFF" onclick="{return WbbForm.doAction('autoOFF')}"/>
        </form>
    </div>






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