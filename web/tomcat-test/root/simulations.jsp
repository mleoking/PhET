<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="edu.colorado.phet.tomcattest.util.WebSimulation" %>
<%@ page import="edu.colorado.phet.tomcattest.util.SqlUtils" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="java.util.PropertyResourceBundle" %>
<%@ page import="edu.colorado.phet.tomcattest.util.WebStrings" %>
<%@ page import="edu.colorado.phet.common.phetcommon.util.LocaleUtils" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%

    System.out.println( "Displaying: " + request.getRequestURI() );

    Locale myLocale = (Locale) request.getAttribute( "locale" );

    List<WebSimulation> simulations = SqlUtils.getSimulationsMatching( application, null, null, myLocale );
    WebSimulation.orderSimulations( simulations, myLocale );

    List<WebSimulation> otherSimulations = SqlUtils.getAllSimulationsWithoutLocale( application, myLocale );
    WebSimulation.orderSimulations( otherSimulations, myLocale );

    WebStrings strings = new WebStrings( application, myLocale );

    String htmlTags = "";
    if( myLocale.getLanguage().equals( "ar" ) ) {
        htmlTags = " dir=\"rtl\"";
    }

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html<%=htmlTags%>>
<head>
    <title><%= strings.get( "simulations.simulations" ) %></title>
    <link href="/tomcattest.css" rel="stylesheet" type="text/css">
</head>
<body>
<div id="mini_sim_area">
    <h2 style="text-align: center;"><%= strings.get( "simulations.simulations" ) %></h2>
    <table id="mini_sim_table">
        <tr>
            <%
                for ( int i = 0; i < simulations.size(); i++ ) {
                    // TODO: better iteration
                    WebSimulation simulation = simulations.get( i );
                    if ( i != 0 && i % 3 == 0 ) {
                        out.print( "</tr><tr>" );
                    }
            %>
            <td>
                <div class="mini_sim_group">
                    <a href="/<%= LocaleUtils.localeToString( myLocale ) %>/simulation/<%= simulation.getSimulation() %>">
                        <img class="mini_sim_thumbnail" src="<%= simulation.getThumbnailUrl() %>"
                             alt="Thumbnail of the simulation '<%= simulation.getTitle() %>'">

                        <div class="mini_sim_title"><%= simulation.getTitle() %>
                        </div>
                    </a>
                </div>
            </td>
            <%
                }
            %>
        </tr>
    </table>
    <%
        if ( !otherSimulations.isEmpty() ) {
    %>
    <br>

    <h2 style="text-align: center;"><%= strings.get( "simulations.simulations-other-languages" ) %></h2>
    <table id="mini_sim_table">
        <tr>
            <%
                for ( int i = 0; i < otherSimulations.size(); i++ ) {
                    WebSimulation simulation = otherSimulations.get( i );
                    if ( i != 0 && i % 3 == 0 ) {
                        out.print( "</tr><tr>" );
                    }
            %>
            <td>
                <div class="mini_sim_group">
                    <a href="/simulation/<%= simulation.getSimulation() %>">
                        <img class="mini_sim_thumbnail" src="<%= simulation.getThumbnailUrl() %>"
                             alt="Thumbnail of the simulation '<%= simulation.getTitle() %>'">

                        <div class="mini_sim_title"><%= simulation.getTitle() %>
                        </div>
                    </a>
                </div>
            </td>
            <%
                }
            %>
        </tr>
    </table>
    <%
        }
    %>
    <%--
    <br>
    Debugging:<br>
    Simulations: <%= simulations.size() %><br>
    Non-translated simulations: <%= otherSimulations.size() %><br>
    --%>
</div>
</body>
</html>