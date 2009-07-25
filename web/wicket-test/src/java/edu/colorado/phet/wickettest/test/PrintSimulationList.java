package edu.colorado.phet.wickettest.test;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.wickettest.util.HibernateUtils;

public class PrintSimulationList {
    public static void main( String[] args ) {
        Session session = HibernateUtils.getInstance().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List<BasicSimulation> simulations = HibernateUtils.getAllSimulationsT();
            for ( BasicSimulation simulation : simulations ) {
                System.out.println( simulation.getName() + " in project " + simulation.getProject().getName() );

                // print out greek title (or english if there is no greek translation
                System.out.println( "   " + simulation.getBestLocalizedSimulation( LocaleUtils.stringToLocale( "el" ) ).getTitle() );
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
    }
}
