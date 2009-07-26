package edu.colorado.phet.wickettest.content;

import java.util.Locale;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.wickettest.WebSimulation;
import edu.colorado.phet.wickettest.data.LocalizedSimulation;
import edu.colorado.phet.wickettest.panels.SimulationMainPanel;
import edu.colorado.phet.wickettest.util.PhetLink;
import edu.colorado.phet.wickettest.util.PhetPage;
import edu.colorado.phet.wickettest.util.SqlUtils;

public class SimulationPage extends PhetPage {
    public SimulationPage( PageParameters parameters ) {
        super( parameters, true );

        String projectName = parameters.getString( "project" );
        String flavorName = parameters.getString( "flavor", projectName );

        WebSimulation simulation = SqlUtils.getBestSimulation( getContext(), projectName, flavorName, getMyLocale() );

        if ( simulation == null ) {
            System.out.println( "Simulation is null!!" );

            // TODO: localize
            addTitle( "Unknown Simulation" );
            add( new Label( "page-title", "Unknown Simulation" ) );
            add( new Label( "simulation-main-panel", "The simulation you specified could not be found." ) );
        }
        else {

            // TODO: handle reordering for rtl
            addTitle( simulation.getTitle() + " " + simulation.getVersionString() );

            add( new SimulationMainPanel( "simulation-main-panel", simulation, getMyLocale() ) );

        }
    }

    public static String getMappingString() {
        return "^simulation/([^/]+)(/([^/]+))?$";
    }

    public static String[] getMappingParameters() {
        return new String[]{"project", null, "flavor"};
    }

    public static PhetLink createLink( String id, Locale locale, WebSimulation simulation ) {
        return createLink( id, locale, simulation.getProject(), simulation.getSimulation() );
    }

    public static PhetLink createLink( String id, Locale locale, LocalizedSimulation simulation ) {
        return createLink( id, locale, simulation.getSimulation().getProject().getName(), simulation.getSimulation().getName() );
    }

    public static PhetLink createLink( String id, Locale locale, String projectName, String simulationName ) {
        String str = "/" + LocaleUtils.localeToString( locale ) + "/simulation/" + projectName;
        if ( !projectName.equals( simulationName ) ) {
            str += "/" + simulationName;
        }
        return new PhetLink( id, str );
    }
}