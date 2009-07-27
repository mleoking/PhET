package edu.colorado.phet.wickettest.content;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.ResourceModel;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.wickettest.data.Category;
import edu.colorado.phet.wickettest.data.LocalizedSimulation;
import edu.colorado.phet.wickettest.data.Simulation;
import edu.colorado.phet.wickettest.panels.SimulationDisplayPanel;
import edu.colorado.phet.wickettest.util.*;

public class SimulationDisplay extends PhetRegularPage {
    public SimulationDisplay( PageParameters parameters ) {
        super( parameters );

        addTitle( new ResourceModel( "simulationDisplay.simulations" ) );

        PageContext context = getPageContext();

        List<LocalizedSimulation> simulations = null;
        Category category = null;

        Transaction tx = null;
        try {
            tx = getHibernateSession().beginTransaction();
            if ( parameters.containsKey( "category" ) ) {
                category = HibernateUtils.getCategoryByName( context.getSession(), parameters.getString( "category" ) );
                //simulations = HibernateUtils.getCategorySimulationsWithLocale( context.getSession(), category, context.getLocale() );
                simulations = new LinkedList<LocalizedSimulation>();
                for ( Object o : category.getSimulations() ) {
                    Simulation sim = (Simulation) o;
                    for ( Object p : sim.getLocalizedSimulations() ) {
                        LocalizedSimulation lsim = (LocalizedSimulation) p;
                        if ( lsim.getLocale().equals( getMyLocale() ) ) {
                            simulations.add( lsim );
                            break;
                        }
                    }
                }
            }
            else {
                simulations = HibernateUtils.getAllSimulationsWithLocale( context.getSession(), context.getLocale() );
            }
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

        add( new SimulationDisplayPanel( "simulation-display-panel", getPageContext(), simulations ) );
    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^simulations$", SimulationDisplay.class );
        mapper.addMap( "^simulations/category/([^/]+)$", SimulationDisplay.class, new String[]{"category"} );
    }

    public static PhetLink createLink( String id, Locale locale ) {
        String str = "/" + LocaleUtils.localeToString( locale ) + "/simulations";
        return new PhetLink( id, str );
    }

    public static Linkable getLinker() {
        return new Linkable() {
            public Link getLink( String id, PageContext context ) {
                return new PhetLink( id, context.getPrefix() + "simulations" );
            }
        };
    }
}