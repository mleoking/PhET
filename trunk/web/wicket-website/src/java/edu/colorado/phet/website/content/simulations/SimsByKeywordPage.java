package edu.colorado.phet.website.content.simulations;

import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.hibernate.Session;

import edu.colorado.phet.website.components.RawLink;
import edu.colorado.phet.website.data.LocalizedSimulation;
import edu.colorado.phet.website.data.Simulation;
import edu.colorado.phet.website.menu.NavLocation;
import edu.colorado.phet.website.panels.simulation.SimulationDisplayPanel;
import edu.colorado.phet.website.templates.PhetRegularPage;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.hibernate.HibernateTask;
import edu.colorado.phet.website.util.hibernate.HibernateUtils;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class SimsByKeywordPage extends PhetRegularPage {
    public SimsByKeywordPage( PageParameters parameters ) {
        super( parameters );

        String keyword = parameters.getString( "keyword" );
        final String key = "keyword." + keyword;

        initializeLocation( new NavLocation( getNavMenu().getLocationByKey( "simulations.by-keyword" ), key, SimsByKeywordPage.getLinker( keyword ) ) );

        final List<LocalizedSimulation> simulations = new LinkedList<LocalizedSimulation>();

        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                List sims = session.createQuery( "select s from Simulation as s, Keyword as k where k.key = :keyword and (k member of s.keywords)" )
                        .setString( "keyword", key ).list();
                for ( Object sim : sims ) {
                    Simulation simulation = (Simulation) sim;
                    simulations.add( HibernateUtils.pickBestTranslation( simulation, getPageContext().getLocale() ) );
                }
                return true;
            }
        } );

        HibernateUtils.orderSimulations( simulations, getPageContext().getLocale() );

        add( new SimulationDisplayPanel( "simulation-display-panel", getPageContext(), simulations ) );

        setTitle( getLocalizer().getString( key, this ) );

    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^simulations/keyword/([^/]+)$", SimsByKeywordPage.class, new String[] { "keyword" } );
    }

    public static RawLinkable getLinker( final String keyword ) {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return "simulations/keyword/" + keyword;
            }
        };
    }

    public static RawLink createLink( String id, PageContext context, String keyword ) {
        String str = context.getPrefix() + "simulations/keyword/" + keyword;
        return new RawLink( id, str );
    }

}