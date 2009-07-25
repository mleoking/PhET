package edu.colorado.phet.wickettest.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import edu.colorado.phet.wickettest.data.LocalizedSimulation;

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


    public static List<LocalizedSimulation> getAllSimulationsS( Session session, Locale locale ) {
        List simulations = session.createQuery( "select l from LocalizedSimulation as l where l.locale = :locale" ).setLocale( "locale", locale ).list();
        List<LocalizedSimulation> ret = new LinkedList<LocalizedSimulation>();
        for ( Object simulation : simulations ) {
            ret.add( (LocalizedSimulation) simulation );
        }
        return ret;
    }

}