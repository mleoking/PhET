package edu.colorado.phet.wickettest.content;

import java.util.Locale;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.tomcattest.WebSimulation;
import edu.colorado.phet.tomcattest.util.SqlUtils;
import edu.colorado.phet.wickettest.util.PhetLink;
import edu.colorado.phet.wickettest.util.PhetPage;

public class SimulationPage extends PhetPage {
    public SimulationPage( PageParameters parameters ) {
        super( parameters );

        String[] spots = getRequestPath().split( "/" );

        if ( spots.length < 3 ) {
            throw new RuntimeException( "Handle this case soon" );
        }

        String projectName = spots[2];
        String simulationName;

        if ( spots.length < 4 ) {
            simulationName = projectName;
        }
        else {
            simulationName = spots[3];
        }

        WebSimulation simulation = SqlUtils.getBestSimulation( getContext(), projectName, simulationName, getMyLocale() );

        if ( simulation == null ) {
            System.out.println( "Simulation is null!!" );
        }

        // TODO: handle reordering for rtl
        Label title = new Label( "pageTitle", simulation.getTitle() + " " + simulation.getVersionString() );
        title.setRenderBodyOnly( true );
        add( title );

        add( new Label( "simulationTitle", simulation.getTitle() ) );

    }

    public static PhetLink createLink( String id, Locale locale, WebSimulation simulation ) {
        return createLink( id, locale, simulation.getProject(), simulation.getSimulation() );
    }

    public static PhetLink createLink( String id, Locale locale, String projectName, String simulationName ) {
        String str = "/" + LocaleUtils.localeToString( locale ) + "/simulation/" + projectName;
        if ( !projectName.equals( simulationName ) ) {
            str += "/" + simulationName;
        }
        return new PhetLink( id, str );
    }
}