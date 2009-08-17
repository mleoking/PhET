package edu.colorado.phet.wickettest.test;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.wickettest.data.PhetUser;
import edu.colorado.phet.wickettest.util.HibernateUtils;
import edu.colorado.phet.wickettest.util.PhetSession;

public class InitializeUsers {

    public static void main( String[] args ) {

        Transaction tx = null;
        Session session = HibernateUtils.getInstance().openSession();
        try {
            tx = session.beginTransaction();

            PhetUser user;

            user = new PhetUser();
            user.setTeamMember( true );
            user.setEmail( "olsonsjc@gmail.com" );

            // sets password hash, not the actual password
            user.setPassword( "WH39ah79fP15QF79Tv0pOv0b/SY=" );
            session.save( user );

            user = new PhetUser();
            user.setTeamMember( false );
            user.setEmail( "testguest@phet.colorado.edu" );
            user.setPassword( PhetSession.hashPassword( "phetti0" ) );
            session.save( user );

            tx.commit();
        }
        catch( RuntimeException e ) {
            if ( tx != null && tx.isActive() ) {
                try {
                    tx.rollback();
                }
                catch( HibernateException e1 ) {
                    System.out.println( "Error rolling back transaction" );
                }
                throw e;
            }
        }

    }

}
