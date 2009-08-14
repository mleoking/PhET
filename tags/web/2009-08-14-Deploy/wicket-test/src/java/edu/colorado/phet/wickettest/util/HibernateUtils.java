package edu.colorado.phet.wickettest.util;

import java.util.*;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.wickettest.data.Category;
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

    public static List<LocalizedSimulation> getLocalizedSimulationsMatching( Session session, String projectName, String simulationName, Locale locale ) {

        boolean useSimulation = simulationName != null || projectName != null;
        boolean useProject = projectName != null;

        // select
        String queryString = "select l from LocalizedSimulation as l";
        if ( useSimulation ) {
            queryString += ", Simulation as s";
        }
        if ( useProject ) {
            queryString += ", Project as p";
        }

        // where conditions
        queryString += " where (";

        List<String> conditions = new LinkedList<String>();

        // joins
        if ( useSimulation ) {
            conditions.add( "l.simulation = s" );
        }
        if ( useProject ) {
            conditions.add( "s.project = p" );
        }

        // constraints
        if ( projectName != null ) {
            conditions.add( "p.name = :project" );
        }
        if ( simulationName != null ) {
            conditions.add( "s.name = :flavor" );
        }
        if ( locale != null ) {
            conditions.add( "l.locale = :locale" );
        }

        boolean prev = false;
        for ( String condition : conditions ) {
            if ( prev ) {
                queryString += " AND ";
            }
            queryString += condition;
            prev = true;
        }

        queryString += ")";

        Query query = session.createQuery( queryString );

        if ( projectName != null ) {
            query.setString( "project", projectName );
        }
        if ( simulationName != null ) {
            query.setString( "flavor", simulationName );
        }
        if ( locale != null ) {
            query.setLocale( "locale", locale );
        }

        List simulations = query.list();
        List<LocalizedSimulation> ret = new LinkedList<LocalizedSimulation>();
        for ( Object simulation : simulations ) {
            ret.add( (LocalizedSimulation) simulation );
        }
        return ret;

    }

    public static Category getCategoryByName( Session session, String categoryName ) {
        return (Category) session.createQuery( "select c from Category as c where c.name = :name" ).setString( "name", categoryName ).uniqueResult();
    }

    public static List<LocalizedSimulation> getCategorySimulationsWithLocale( Session session, Category category, Locale locale ) {
        Query query = session.createQuery( "select l from LocalizedSimulation as l, Simulation as s, Category as c where (l.simulation = s AND (s in elements(c.simulations)) AND l.locale = :locale AND c = :category) order by indices(c)" );
        query.setLocale( "locale", locale );
        query.setEntity( "category", category );
        List simulations = query.list();
        List<LocalizedSimulation> ret = new LinkedList<LocalizedSimulation>();
        for ( Object simulation : simulations ) {
            ret.add( (LocalizedSimulation) simulation );
        }
        return ret;
    }

    public static List<LocalizedSimulation> getAllSimulationsWithLocale( Session session, Locale locale ) {
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

    /**
     * Sort a list of localized simulations for a particular locale. This means simulations will be sorted
     * first by the title in the locale parameter (if there is a title), then by locale.
     *
     * @param list   The list of simulations to order
     * @param locale The locale to use for ordering
     */
    public static void orderSimulations( List<LocalizedSimulation> list, final Locale locale ) {
        final HashMap<String, String> map = new HashMap<String, String>();

        for ( LocalizedSimulation sim : list ) {
            boolean correctLocale = locale.equals( sim.getLocale() );
            if ( !map.containsKey( sim.getSimulation().getName() ) || correctLocale ) {
                if ( correctLocale ) {
                    map.put( sim.getSimulation().getName(), sim.getTitle() );
                }
                else {
                    map.put( sim.getSimulation().getName(), null );
                }
            }
        }

        Collections.sort( list, new Comparator<LocalizedSimulation>() {
            public int compare( LocalizedSimulation a, LocalizedSimulation b ) {

                if ( a.getSimulation().getName().equals( b.getSimulation().getName() ) ) {
                    if ( a.getLocale().equals( locale ) ) {
                        return -1;
                    }
                    if ( b.getLocale().equals( locale ) ) {
                        return 1;
                    }
                    return a.getLocale().getDisplayName( locale ).compareToIgnoreCase( b.getLocale().getDisplayName( locale ) );
                }

                String aGlobalTitle = map.get( a.getSimulation().getName() );
                String bGlobalTitle = map.get( b.getSimulation().getName() );

                boolean aGlobal = aGlobalTitle != null;
                boolean bGlobal = bGlobalTitle != null;

                if ( aGlobal && bGlobal ) {
                    final String[] ignoreWords = {"The", "La", "El"};
                    for ( String ignoreWord : ignoreWords ) {
                        if ( aGlobalTitle.startsWith( ignoreWord + " " ) ) {
                            aGlobalTitle = aGlobalTitle.substring( ignoreWord.length() + 1 );
                        }
                        if ( bGlobalTitle.startsWith( ignoreWord + " " ) ) {
                            bGlobalTitle = bGlobalTitle.substring( ignoreWord.length() + 1 );
                        }
                    }
                    return aGlobalTitle.compareToIgnoreCase( bGlobalTitle );
                }
                else if ( aGlobal ) {
                    return -1;
                }
                else if ( bGlobal ) {
                    return 1;
                }
                else {
                    return a.getSimulation().getName().compareToIgnoreCase( b.getSimulation().getName() );
                }
            }
        } );
    }

    public static LocalizedSimulation getExampleSimulation( Session session, Locale preferredLocale ) {
        LocalizedSimulation simulation = null;
        Query query = session.createQuery( "select ls from LocalizedSimulation as ls, Simulation as s where (ls.simulation = s and s.name = 'circuit-construction-kit-dc' and ls.locale = :locale)" );
        query.setLocale( "locale", preferredLocale );
        List list = query.list();
        if ( !list.isEmpty() ) {
            simulation = (LocalizedSimulation) list.get( 0 );
        }
        else {
            query = session.createQuery( "select ls from LocalizedSimulation as ls, Simulation as s where (ls.simulation = s and ls.locale = :locale)" );
            query.setLocale( "locale", preferredLocale );
            list = query.list();
            if ( !list.isEmpty() ) {
                simulation = (LocalizedSimulation) list.get( 0 );
            }
            else {
                simulation = (LocalizedSimulation) session.createQuery( "select ls from LocalizedSimulation as ls, Simulation as s where (ls.simulation = s and s.name = 'circuit-construction-kit-dc' and ls.locale = :locale)" ).setLocale( "locale", LocaleUtils.stringToLocale( "en" ) ).uniqueResult();
            }
        }
        return simulation;
    }


}