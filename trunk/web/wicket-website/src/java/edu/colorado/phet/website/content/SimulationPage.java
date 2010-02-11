package edu.colorado.phet.website.content;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.website.components.PhetLink;
import edu.colorado.phet.website.data.Category;
import edu.colorado.phet.website.data.LocalizedSimulation;
import edu.colorado.phet.website.data.Simulation;
import edu.colorado.phet.website.menu.NavLocation;
import edu.colorado.phet.website.panels.SimulationMainPanel;
import edu.colorado.phet.website.templates.PhetMenuPage;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.links.AbstractLinker;

public class SimulationPage extends PhetMenuPage {

    private static Logger logger = Logger.getLogger( SimulationPage.class.getName() );

    public SimulationPage( PageParameters parameters ) {
        super( parameters );

        String projectName = parameters.getString( "project" );
        String flavorName = parameters.getString( "flavor", projectName );

        LocalizedSimulation simulation = null;
        Set<NavLocation> locations = new HashSet<NavLocation>();

        Session session = getHibernateSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            simulation = HibernateUtils.getBestSimulation( session, getMyLocale(), projectName, flavorName );
            if ( simulation != null ) {
                for ( Object o : simulation.getSimulation().getCategories() ) {
                    Category category = (Category) o;
                    locations.add( category.getNavLocation( getNavMenu() ) );
                }
            }
            tx.commit();
        }
        catch( RuntimeException e ) {
            logger.warn( e );
            if ( tx != null && tx.isActive() ) {
                try {
                    tx.rollback();
                }
                catch( HibernateException e1 ) {
                    logger.error( "ERROR: Error rolling back transaction", e1 );
                }
                throw e;
            }
        }

        if ( simulation == null ) {
            throw new RestartResponseAtInterceptPageException( NotFoundPage.class );
        }

        SimulationMainPanel simPanel = new SimulationMainPanel( "simulation-main-panel", simulation, getPageContext() );
        add( simPanel );
        addTitle( simPanel.getTitle() );

        initializeLocationWithSet( locations );

    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^simulation/([^/]+)(/([^/]+))?$", SimulationPage.class, new String[]{"project", null, "flavor"} );
    }

    // TODO: convert usage to getlinker
    public static PhetLink createLink( String id, PageContext context, LocalizedSimulation simulation ) {
        return createLink( id, context, simulation.getSimulation().getProject().getName(), simulation.getSimulation().getName() );
    }

    // TODO: convert usage to getlinker
    public static PhetLink createLink( String id, PageContext context, String projectName, String simulationName ) {
        String str = context.getPrefix() + "simulation/" + projectName;
        if ( !projectName.equals( simulationName ) ) {
            str += "/" + simulationName;
        }
        return new PhetLink( id, str );
    }

    public static AbstractLinker getLinker( final String projectName, final String simulationName ) {
        return new AbstractLinker() {
            @Override
            public String getSubUrl( PageContext context ) {
                String str = "simulation/" + projectName;
                if ( !projectName.equals( simulationName ) ) {
                    str += "/" + simulationName;
                }
                return str;
            }
        };
    }

    public static AbstractLinker getLinker( final Simulation simulation ) {
        return getLinker( simulation.getProject().getName(), simulation.getName() );
    }

    public static AbstractLinker getLinker( final LocalizedSimulation localizedSimulation ) {
        return getLinker( localizedSimulation.getSimulation() );
    }

}