package edu.colorado.phet.wickettest.content;

import java.util.*;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.ResourceModel;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.wickettest.data.Category;
import edu.colorado.phet.wickettest.data.LocalizedSimulation;
import edu.colorado.phet.wickettest.data.Simulation;
import edu.colorado.phet.wickettest.menu.NavLocation;
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
            if ( parameters.containsKey( "categories" ) ) {
                String categoriesString = parameters.getString( "categories" );
                System.out.println( "categoriesString = " + categoriesString );
                String[] categories = categoriesString.split( "/" );
                int categoryIndex = categories.length - 1;
                if ( categories[categoryIndex].equals( "" ) ) {
                    categoryIndex--;
                }
                String categoryName = categories[categoryIndex];
                category = HibernateUtils.getCategoryByName( context.getSession(), categoryName );
                if ( category == null ) {
                    throw new RuntimeException( "Couldn't find category" );
                }

                System.out.println( "category path: " + category.getCategoryPath() );

                simulations = new LinkedList<LocalizedSimulation>();
                addSimulationsFromCategory( simulations, category, new HashSet<Integer>() );
            }
            else {
                simulations = HibernateUtils.getAllSimulationsWithLocale( context.getSession(), context.getLocale() );
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
            initializeMenu( context.getApplication().getMenu().getLocationByKey( "all" ) );
        }
        else {
            NavLocation location = context.getApplication().getMenu().getLocationByKey( category.getName() );
            initializeMenu( location );
        }

        add( new SimulationDisplayPanel( "simulation-display-panel", getPageContext(), simulations ) );
    }

    private void addSimulationsFromCategory( List<LocalizedSimulation> simulations, Category category, Set<Integer> used ) {
        for ( Object o : category.getSimulations() ) {
            Simulation sim = (Simulation) o;
            for ( Object p : sim.getLocalizedSimulations() ) {
                LocalizedSimulation lsim = (LocalizedSimulation) p;
                if ( lsim.getLocale().equals( getMyLocale() ) ) {
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
                addSimulationsFromCategory( simulations, subcategory, used );
            }
        }
    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^simulations$", SimulationDisplay.class );
        mapper.addMap( "^simulations/category/([^?]+)([?](.*))?$", SimulationDisplay.class, new String[]{"categories", null, "query-string"} );
    }

    public static PhetLink createLink( String id, Locale locale ) {
        String str = "/" + LocaleUtils.localeToString( locale ) + "/simulations/category/featured";
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