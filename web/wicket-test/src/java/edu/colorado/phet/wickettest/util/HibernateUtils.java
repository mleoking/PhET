package edu.colorado.phet.wickettest.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
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

    public static LocalizedSimulation getBestSimulation( Session session, Locale locale, String project, String flavor ) {
        Locale englishLocale = LocaleUtils.stringToLocale( "en" );

        Query query = session.createQuery( "select l from LocalizedSimulation as l, Simulation as s, Project as p where (l.simulation = s AND s.project = p AND p.name = :project AND s.name = :flavor AND (l.locale = :english OR l.locale = :locale))" );
        query.setString( "project", project );
        query.setString( "flavor", flavor );
        query.setLocale( "locale", locale );
        query.setLocale( "english", englishLocale );
        List simulations = query.list();

        if ( simulations.size() == 0 ) {
            return null;
        }

        if ( simulations.size() == 1 ) {
            return (LocalizedSimulation) simulations.get( 0 );
        }

        if ( simulations.size() == 2 ) {
            LocalizedSimulation firstSim = (LocalizedSimulation) simulations.get( 0 );
            if ( firstSim.getLocale().equals( locale ) ) {
                return firstSim;
            }
            else {
                return (LocalizedSimulation) simulations.get( 1 );
            }
        }

        throw new RuntimeException( "WARNING: matches more than 2 simulations!" );
    }

}