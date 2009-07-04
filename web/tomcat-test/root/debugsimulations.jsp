<%@ page import="java.util.List" %>
<%@ page import="edu.colorado.phet.common.phetcommon.util.LocaleUtils" %>
<%@ page import="edu.colorado.phet.wickettest.WebSimulation" %>
<%@ page import="edu.colorado.phet.wickettest.util.SqlUtils" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    List<WebSimulation> simulations = SqlUtils.getAllSimulations( application );
    WebSimulation.orderSimulations( simulations, LocaleUtils.stringToLocale( "en" ) );
%>
<html>
<head><title>Simulations</title></head>
<body>
<%
    for ( int i = 0; i < simulations.size(); i++ ) {
        // TODO: better iteration
        WebSimulation simulation = simulations.get( i );
%>
<a href='/simulation/<%= simulation.getProject() %>/<%=simulation.getSimulation() %>'><%= simulation.getProject() %>
    / <%= simulation.getSimulation() %> / <%= simulation.getLocaleString() %> / <%= simulation.getTitle() %>
</a><br>
<%
    }
%>
</body>
</html>