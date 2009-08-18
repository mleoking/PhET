package edu.colorado.phet.wickettest.content;

import java.util.*;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.StringResourceModel;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;

import edu.colorado.phet.wickettest.components.PhetLink;
import edu.colorado.phet.wickettest.data.Category;
import edu.colorado.phet.wickettest.data.LocalizedSimulation;
import edu.colorado.phet.wickettest.data.Simulation;
import edu.colorado.phet.wickettest.menu.NavLocation;
import edu.colorado.phet.wickettest.panels.SimulationDisplayPanel;
import edu.colorado.phet.wickettest.templates.PhetRegularPage;
import edu.colorado.phet.wickettest.util.HibernateUtils;
import edu.colorado.phet.wickettest.util.Linkable;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.PhetUrlMapper;

public class SimulationDisplay extends PhetRegularPage {
    public SimulationDisplay( PageParameters parameters ) {
        super( parameters );

        PageContext context = getPageContext();

        List<LocalizedSimulation> simulations = null;
        Category category = null;

        Transaction tx = null;
        try {
            tx = getHibernateSession().beginTransaction();
            if ( parameters.containsKey( "categories" ) ) {
                category = Category.getCategoryFromPath( getHibernateSession(), parameters.getString( "categories" ) );

                simulations = new LinkedList<LocalizedSimulation>();
                addSimulationsFromCategory( simulations, getMyLocale(), category );
            }
            else {
                simulations = HibernateUtils.getAllSimulationsWithLocale( getHibernateSession(), context.getLocale() );
                HibernateUtils.orderSimulations( simulations, context.getLocale() );
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

        NavLocation location;

        if ( category == null ) {
            location = getNavMenu().getLocationByKey( "all" );
        }
        else {
            location = category.getNavLocation( getNavMenu() );
        }

        initializeLocation( location );

        addTitle( new StringResourceModel( "simulationDisplay.title", this, null, new Object[]{new StringResourceModel( location.getLocalizationKey(), this, null )} ) );

        add( new SimulationDisplayPanel( "simulation-display-panel", getPageContext(), simulations ) );
    }

    // NOTE: must be in a transaction
    public static void addSimulationsFromCategory( List<LocalizedSimulation> simulations, Locale locale, Category category ) {
        addSimulationsFromCategory( simulations, locale, category, new HashSet<Integer>() );
    }

    private static void addSimulationsFromCategory( List<LocalizedSimulation> simulations, Locale locale, Category category, Set<Integer> used ) {
        for ( Object o : category.getSimulations() ) {
            Simulation sim = (Simulation) o;
            for ( Object p : sim.getLocalizedSimulations() ) {
                LocalizedSimulation lsim = (LocalizedSimulation) p;
                if ( lsim.getLocale().equals( locale ) ) {
                    if ( !used.contains( lsim.getId() ) ) {
                        simulations.add( lsim );
                        used.add( lsim.getId() );
                    }

                    break;
                }
            }
        }
        if ( category.isAuto() ) {
            for ( Object o : category.getSubcategories() ) {
                Category subcategory = (Category) o;
                addSimulationsFromCategory( simulations, locale, subcategory, used );
            }
        }
    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^simulations$", SimulationDisplay.class );
        mapper.addMap( "^simulations/category/([^?]+)([?](.*))?$", SimulationDisplay.class, new String[]{"categories", null, "query-string"} );
    }

    public static PhetLink createLink( String id, PageContext context ) {
        String str = context.getPrefix() + "simulations/category/featured";
        return new PhetLink( id, str );
    }

    public static Linkable getLinker() {
        return new Linkable() {
            public Link getLink( String id, PageContext context ) {
                return new PhetLink( id, context.getPrefix() + "simulations/category/featured" );
            }
        };
    }
}