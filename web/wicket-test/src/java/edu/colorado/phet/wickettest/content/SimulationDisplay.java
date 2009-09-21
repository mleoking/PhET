package edu.colorado.phet.wickettest.content;

import java.util.*;

import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.model.StringResourceModel;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;

import edu.colorado.phet.wickettest.WicketApplication;
import edu.colorado.phet.wickettest.components.InvisibleComponent;
import edu.colorado.phet.wickettest.components.PhetLink;
import edu.colorado.phet.wickettest.data.Category;
import edu.colorado.phet.wickettest.data.LocalizedSimulation;
import edu.colorado.phet.wickettest.data.Simulation;
import edu.colorado.phet.wickettest.menu.NavLocation;
import edu.colorado.phet.wickettest.panels.IndexLetterLinks;
import edu.colorado.phet.wickettest.panels.SimulationDisplayPanel;
import edu.colorado.phet.wickettest.panels.SimulationIndexPanel;
import edu.colorado.phet.wickettest.templates.PhetRegularPage;
import edu.colorado.phet.wickettest.util.HibernateUtils;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.PhetUrlMapper;
import edu.colorado.phet.wickettest.util.links.AbstractLinker;
import edu.colorado.phet.wickettest.util.links.RawLinkable;

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
                if ( category != null ) {
                    simulations = new LinkedList<LocalizedSimulation>();
                    addSimulationsFromCategory( simulations, getMyLocale(), category );
                }
            }
            else {
                simulations = HibernateUtils.preferredFullSimulationList( getHibernateSession(), context.getLocale() );
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

        if ( category == null ) {
            // didn't find the category
            throw new RestartResponseAtInterceptPageException( NotFoundPage.class );
        }

        boolean showIndex = false;

        if ( parameters.containsKey( "query-string" ) ) {
            System.out.println( "Query string: " + parameters.getString( "query-string" ) );
            if ( parameters.getString( "query-string" ).equals( "/index" ) ) {
                showIndex = true;
            }
            else {
                setResponsePage( NotFoundPage.class );
            }
        }
        else {
            System.out.println( "No query string" );
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

        if ( showIndex ) {
            HibernateUtils.orderSimulations( simulations, context.getLocale() );
            SimulationIndexPanel indexPanel = new SimulationIndexPanel( "simulation-display-panel", getPageContext(), simulations );
            add( indexPanel );

            add( new InvisibleComponent( "to-index-view" ) );
            String path = getMyPath();
            add( new PhetLink( "to-thumbnail-view", context.getPrefix() + path.substring( 0, path.length() - 6 ) ) );
            add( new IndexLetterLinks( "letter-links", context, indexPanel.getLetters() ) );
        }
        else {
            add( new SimulationDisplayPanel( "simulation-display-panel", getPageContext(), simulations ) );

            add( new PhetLink( "to-index-view", context.getPrefix() + getMyPath() + "/index" ) );
            add( new InvisibleComponent( "to-thumbnail-view" ) );
            add( new InvisibleComponent( "letter-links" ) );
        }
    }

    // NOTE: must be in a transaction
    public static void addSimulationsFromCategory( List<LocalizedSimulation> simulations, Locale locale, Category category ) {
        addSimulationsFromCategory( simulations, locale, category, new HashSet<Integer>() );
    }

    private static void addSimulationsFromCategory( List<LocalizedSimulation> simulations, Locale locale, Category category, Set<Integer> used ) {
        for ( Object o : category.getSimulations() ) {
            Simulation sim = (Simulation) o;
            if ( !sim.getProject().isVisible() ) {
                continue;
            }
            LocalizedSimulation englishSim = null;
            boolean added = false;
            for ( Object p : sim.getLocalizedSimulations() ) {
                LocalizedSimulation lsim = (LocalizedSimulation) p;
                if ( lsim.getLocale().equals( locale ) ) {
                    added = true;
                    if ( !used.contains( lsim.getId() ) ) {
                        simulations.add( lsim );
                        used.add( lsim.getId() );
                    }

                    break;
                }
                else if ( lsim.getLocale().equals( WicketApplication.getDefaultLocale() ) ) {
                    englishSim = lsim;
                }
            }
            if ( !added && englishSim != null ) {
                simulations.add( englishSim );
                used.add( englishSim.getId() );
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
        //mapper.addMap( "^simulations$", SimulationDisplay.class );
        mapper.addMap( "^simulations(/index)?$", SimulationDisplay.class, new String[]{"query-string"} );
        mapper.addMap( "^simulations/category/(.+?)(/index)?$", SimulationDisplay.class, new String[]{"categories", "query-string"} );
    }

    public static PhetLink createLink( String id, PageContext context ) {
        String str = context.getPrefix() + "simulations/category/featured";
        return new PhetLink( id, str );
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return "simulations/category/featured";
            }
        };
    }

}