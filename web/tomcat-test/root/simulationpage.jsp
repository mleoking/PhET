<%@ page import="edu.colorado.phet.tomcattest.WebsiteSimulation" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<%
    String projectName = request.getParameter( "projectName" );
    String simulationName = request.getParameter( "simulationName" );
    WebsiteSimulation simulation = WebsiteSimulation.getSimulation( getServletConfig().getServletContext(), projectName, simulationName );
    if ( simulation == null ) {
%>
<head><title>An error has occurred</title></head>
<body>
<h1>An error has occurred!</h1>
</body>
<%
}
else {
%>
<head><title><%= simulation.getDisplayName() %> <%= simulation.getVersionString() %>
</title></head>
<body style="text-align: center;">
<div style="margin: 0px auto;">
    <h3><%= simulation.getDisplayName() %> <%= simulation.getVersionString() %>
    </h3>
    <img src="<%= simulation.getImageUrl() %>" alt="An image of the simulation"/>
</div>
</body>
<%
    }
%>
</html>