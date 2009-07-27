package edu.colorado.phet.wickettest.content;

import java.util.Locale;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.wickettest.data.LocalizedSimulation;
import edu.colorado.phet.wickettest.panels.SimulationMainPanel;
import edu.colorado.phet.wickettest.util.HibernateUtils;
import edu.colorado.phet.wickettest.util.PhetLink;
import edu.colorado.phet.wickettest.util.PhetRegularPage;

public class SimulationPage extends PhetRegularPage {
    public SimulationPage( PageParameters parameters ) {
        super( parameters );

        String projectName = parameters.getString( "project" );
        String flavorName = parameters.getString( "flavor", projectName );

        LocalizedSimulation simulation = null;

        Session session = getHibernateSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            simulation = HibernateUtils.getBestSimulation( session, getMyLocale(), projectName, flavorName );
            tx.commit();
        }
        catch( RuntimeException e ) {
            if ( tx != null && tx.isActive() ) {
                try {
                    tx.rollback();
                }
                catch( HibernateException e1 ) {
                    System.out.println( "ERROR: Error rolling back transaction" );
                }
                throw e;
            }
        }

        if ( simulation == null ) {
            // TODO: localize
            addTitle( "Unknown Simulation" );
            add( new Label( "simulation-main-panel", "The simulation you specified could not be found." ) );
        }
        else {
            // TODO: localize
            addTitle( simulation.getTitle() + " " + simulation.getSimulation().getProject().getVersionString() );
            add( new SimulationMainPanel( "simulation-main-panel", simulation, getPageContext() ) );
        }
    }

    public static String getMappingString() {
        return "^simulation/([^/]+)(/([^/]+))?$";
    }

    public static String[] getMappingParameters() {
        return new String[]{"project", null, "flavor"};
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