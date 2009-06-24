<%@ page import="java.util.List" %>
<%@ page import="edu.colorado.phet.common.phetcommon.util.LocaleUtils" %>
<%@ page import="edu.colorado.phet.tomcattest.WebSimulation" %>
<%@ page import="edu.colorado.phet.tomcattest.util.SqlUtils" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    List<WebSimulation> simulations = SqlUtils.getSimulationsMatching( application, null, null, LocaleUtils.stringToLocale( "en" ) );
    WebSimulation.orderSimulations( simulations, LocaleUtils.stringToLocale( "en" ) );
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>Simulations</title>
    <link href="/tomcattest.css" rel="stylesheet" type="text/css">
</head>
<body>
<div id="mini_sim_area">
    <h2 style="text-align: center;">Simulations</h2>
    <table id="mini_sim_table">
        <tr>
            <%
                for ( int i = 0; i < simulations.size(); i++ ) {
                    // TODO: better iteration
                    WebSimulation simulation = simulations.get( i );
                    if( i != 0 && i % 3 == 0 ) {
                        out.print( "</tr><tr>" );
                    }
            %>
            <td>
                <div class="mini_sim_group">
                    <a href="/simulation/<%= simulation.getSimulation() %>">
                        <img class="mini_sim_thumbnail" src="<%= simulation.getThumbnailUrl() %>"
                             alt="Thumbnail of the simulation '<%= simulation.getTitle() %>'">

                        <div class="mini_sim_title"><%= simulation.getTitle() %></div>
                    </a>
                </div>
            </td>
            <%
                }
            %>
        </tr>
    </table>
</div>
</body>
</html>