package edu.colorado.phet.website.test;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.website.authentication.PhetSession;
import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.util.HibernateUtils;

public class InitializeUsers {

    private static final Logger logger = Logger.getLogger( InitializeUsers.class.getName() );

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
            user.setHashedPassword( "WH39ah79fP15QF79Tv0pOv0b/SY=" );
            session.save( user );

            user = new PhetUser();
            user.setTeamMember( false );
            user.setEmail( "testguest@phet.colorado.edu" );
            user.setHashedPassword( PhetSession.hashPassword( "phetti0" ) );
            session.save( user );

            tx.commit();
        }
        catch( RuntimeException e ) {
            logger.warn( e );
            if ( tx != null && tx.isActive() ) {
                try {
                    tx.rollback();
                }
                catch( HibernateException e1 ) {
                    logger.error( "Error rolling back transaction", e1 );
                }
                throw e;
            }
        }

    }

}
