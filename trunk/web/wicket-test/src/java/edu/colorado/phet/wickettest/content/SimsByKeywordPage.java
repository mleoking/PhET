package edu.colorado.phet.wickettest.content;

import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.hibernate.Session;

import edu.colorado.phet.wickettest.components.PhetLink;
import edu.colorado.phet.wickettest.data.LocalizedSimulation;
import edu.colorado.phet.wickettest.data.Simulation;
import edu.colorado.phet.wickettest.menu.NavLocation;
import edu.colorado.phet.wickettest.panels.SimulationDisplayPanel;
import edu.colorado.phet.wickettest.templates.PhetRegularPage;
import edu.colorado.phet.wickettest.util.HibernateTask;
import edu.colorado.phet.wickettest.util.HibernateUtils;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.PhetUrlMapper;
import edu.colorado.phet.wickettest.util.links.AbstractLinker;
import edu.colorado.phet.wickettest.util.links.RawLinkable;

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

        // TODO: localize
        addTitle( keyword );

    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^simulations/keyword/([^/]+)$", SimsByKeywordPage.class, new String[]{"keyword"} );
    }

    public static RawLinkable getLinker( final String keyword ) {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return "simulations/keyword/" + keyword;
            }
        };
    }

    public static PhetLink createLink( String id, PageContext context, String keyword ) {
        String str = context.getPrefix() + "simulations/keyword/" + keyword;
        return new PhetLink( id, str );
    }

}