package edu.colorado.phet.wickettest.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import edu.colorado.phet.wickettest.test.BasicLocalizedSimulation;
import edu.colorado.phet.wickettest.test.BasicSimulation;

public class HibernateUtils {

    private static org.hibernate.SessionFactory sessionFactory;

    private HibernateUtils() {
    }

    static {
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    public static SessionFactory getInstance() {
        return sessionFactory;
    }

    public Session openSession() {
        return sessionFactory.openSession();
    }

    public Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    public static void close() {
        if ( sessionFactory != null ) {
            sessionFactory.close();
        }
        sessionFactory = null;
    }

    public static List<BasicLocalizedSimulation> getAllSimulationsS( Session session, Locale locale ) {
        List simulations = session.createQuery( "select l from BasicLocalizedSimulation as l where l.locale = :locale" ).setLocale( "locale", locale ).list();
        List<BasicLocalizedSimulation> ret = new LinkedList<BasicLocalizedSimulation>();
        for ( Object simulation : simulations ) {
            ret.add( (BasicLocalizedSimulation) simulation );
        }
        return ret;
    }

    public static List<BasicSimulation> getAllSimulationsT() {
        List simulations = HibernateUtils.getInstance().getCurrentSession().createQuery( "select s from BasicSimulation as s" ).list();
        List<BasicSimulation> ret = new LinkedList<BasicSimulation>();
        for ( Object simulation : simulations ) {
            ret.add( (BasicSimulation) simulation );
        }
        return ret;
    }

    public static List<BasicSimulation> getAllSimulations() {
        Transaction tx = null;
        List simulations = null;
        List<BasicSimulation> ret = null;
        Session session = getInstance().getCurrentSession();
        try {
            tx = session.beginTransaction();
            simulations = session.createQuery( "select s from BasicSimulation as s" ).list();
            ret = new LinkedList<BasicSimulation>();
            for ( Object simulation : simulations ) {
                ret.add( (BasicSimulation) simulation );
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
        return ret;
    }
}