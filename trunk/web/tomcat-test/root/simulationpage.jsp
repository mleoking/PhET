<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="edu.colorado.phet.common.phetcommon.util.LocaleUtils" %>
<%@ page import="edu.colorado.phet.wickettest.WebSimulation" %>
<%@ page import="edu.colorado.phet.wickettest.util.SqlUtils" %>
<%@ page import="edu.colorado.phet.tomcattest.util.WebStrings" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    System.out.println( "Displaying: " + request.getRequestURI() );


    Locale detectedLocale = (Locale) request.getAttribute( "locale" );
    String detectedLocaleString = null;
    if ( detectedLocale != null ) {
        detectedLocaleString = LocaleUtils.localeToString( detectedLocale );
    }

    Locale myLocale = detectedLocale;
    String simulationName = request.getParameter( "simulationName" );

    List<WebSimulation> simulations = SqlUtils.getSimulationsMatching( application, null, simulationName, null );
    WebSimulation.orderSimulations( simulations, myLocale );

    WebStrings strings = new WebStrings( application, myLocale );

    String htmlTags = "";
    if( myLocale.getLanguage().equals( "ar" ) ) {
        htmlTags = " dir=\"rtl\"";
    }

    if ( simulations.isEmpty() ) {
%>
An error has occurred!
<%
}
else {
    WebSimulation simulation = simulations.get( 0 );
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html<%= htmlTags %>>
<head>
    <title><%= simulation.getTitle() %> <%= simulation.getVersionString() %>
    </title>
    <link href="/tomcattest.css" rel="stylesheet" type="text/css">
</head>
<body>
<div id="sim_holder">
    <h2><%= simulation.getTitle() %>
    </h2>

    <div id="sim_image">
        <a href="<%= simulation.getRunUrl() %>">
            <img src="<%= simulation.getImageUrl() %>"
                 alt="Screenshot for the simulation '<%= simulation.getTitle() %>'">
        </a>
    </div>

    <div id="sim_description">
        <%= simulation.getDescription() %>
    </div>

    <h4><%= strings.get( "simulation.translated-versions" )%></h4>

    <table>
        <%
            for ( int i = 0; i < simulations.size(); i++ ) {
                WebSimulation translatedSim = simulations.get( i );
                Locale tLocale = translatedSim.getLocale();
                if ( tLocale.equals( myLocale ) ) {
                    continue;
                }
        %>
        <tr>
            <td>
                <a href="<%= translatedSim.getRunUrl() %>">
                    <%= tLocale.getDisplayName( myLocale )%>
                </a>
            </td>
            <td>
                <%= tLocale.getDisplayName( tLocale ) %>
            </td>
            <td>
                <%= translatedSim.getTitle() %>
            </td>
        </tr>
        <%
            }
        %>
    </table>
    <%--
    <br>
    Debugging:<br>
    Locale: <%= detectedLocaleString %><br>
    --%>
</div>
</body>
</html>
<%
    }
%>