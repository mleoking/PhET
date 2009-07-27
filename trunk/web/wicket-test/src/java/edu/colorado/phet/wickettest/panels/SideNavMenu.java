package edu.colorado.phet.wickettest.panels;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.wickettest.menu.NavMenuList;
import edu.colorado.phet.wickettest.util.PageContext;

public class SideNavMenu extends PhetPanel {

    public SideNavMenu( String id, final PageContext context ) {
        super( id, context );

        Session session = context.getSession();
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
        }

        add( new NavMenuList( "side-nav-menu", context, context.getApplication().getMenu().getLocations() ) );

    }

}