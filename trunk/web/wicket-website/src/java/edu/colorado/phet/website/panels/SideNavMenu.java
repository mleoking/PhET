package edu.colorado.phet.website.panels;

import java.util.List;
import java.util.Set;

import edu.colorado.phet.website.menu.NavLocation;
import edu.colorado.phet.website.menu.NavMenuList;
import edu.colorado.phet.website.util.PageContext;

public class SideNavMenu extends PhetPanel {

    public SideNavMenu( String id, final PageContext context, Set<NavLocation> currentLocations ) {
        super( id, context );

        /*Session session = getHibernateSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

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
        }*/

        List<NavLocation> locations = getNavMenu().getLocations();
        add( new NavMenuList( "side-nav-menu", context, locations, currentLocations, 0 ) );

    }

}