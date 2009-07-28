package edu.colorado.phet.wickettest.menu;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.markup.html.link.Link;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.wickettest.content.IndexPage;
import edu.colorado.phet.wickettest.content.SimulationDisplay;
import edu.colorado.phet.wickettest.data.Category;
import edu.colorado.phet.wickettest.util.HibernateUtils;
import edu.colorado.phet.wickettest.util.Linkable;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.PhetLink;

public class NavMenu {
    private HashMap<String, NavLocation> cache = new HashMap<String, NavLocation>();
    private List<NavLocation> locations = new LinkedList<NavLocation>();

    public NavMenu() {
        NavLocation home = new NavLocation( null, "home", IndexPage.getLinker() );
        addMajorLocation( home );

        NavLocation simulations = new NavLocation( null, "simulations", SimulationDisplay.getLinker() );
        addMajorLocation( simulations );

        Session session = HibernateUtils.getInstance().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            Category root = (Category) session.createQuery( "select c from Category as c where c.root = true" ).uniqueResult();
            buildCategoryMenu( simulations, root );

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
    }

    public void buildCategoryMenu( NavLocation location, Category category ) {
        for ( Object o : category.getSubcategories() ) {
            final Category subCategory = (Category) o;
            NavLocation subLocation = new NavLocation( location, subCategory.getName(), new Linkable() {
                public Link getLink( String id, PageContext context ) {
                    return new PhetLink( id, context.getPrefix() + "simulations/category/" + subCategory.getName() );
                }
            } );
            location.addChild( subLocation );
            buildCategoryMenu( subLocation, subCategory );
        }
        if ( category.isRoot() ) {
            NavLocation subLocation = new NavLocation( location, "all", new Linkable() {
                public Link getLink( String id, PageContext context ) {
                    return new PhetLink( id, context.getPrefix() + "simulations" );
                }
            } );
            location.addChild( subLocation );
        }
    }

    public void addMajorLocation( NavLocation location ) {
        locations.add( location );
        addLocation( location );
    }

    public void addLocation( NavLocation location ) {
        cache.put( location.getKey(), location );
    }

    public List<NavLocation> getLocations() {
        return locations;
    }

    public NavLocation getLocationByKey( String key ) {
        return cache.get( key );
    }
}
