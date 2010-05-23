package edu.colorado.phet.website.util;

import java.util.*;

import org.apache.log4j.Logger;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.data.Category;
import edu.colorado.phet.website.data.LocalizedSimulation;
import edu.colorado.phet.website.data.Simulation;
import edu.colorado.phet.website.data.Translation;
import edu.colorado.phet.website.translation.PhetLocalizer;

/**
 * Collections of utility functions that mainly deal with the website's interaction with Hibernate
 * <p/>
 * TODO: move other functions that aren't primarily related to hibernate elsewhere
 */
public class HibernateUtils {

    private static org.hibernate.SessionFactory sessionFactory;

    private static final Logger logger = Logger.getLogger( HibernateUtils.class.getName() );

    private HibernateUtils() {
        // don't instantiate
        throw new AssertionError();
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

    public static List<LocalizedSimulation> getAllVisibleSimulationsWithLocale( Session session, Locale locale ) {
        List simulations = session.createQuery( "select l from LocalizedSimulation as l where l.locale = :locale and l.simulation.project.visible = true and l.simulation.simulationVisible = true" ).setLocale( "locale", locale ).list();
        List<LocalizedSimulation> ret = new LinkedList<LocalizedSimulation>();
        for ( Object simulation : simulations ) {
            ret.add( (LocalizedSimulation) simulation );
        }
        return ret;
    }

    /**
     * Find the best LocalizedSimulation that matches the given locale and simulation name.
     * <p/>
     * NOTE: Simulation names should be unique. Matches exact locale, then language, then English.
     * NOTE: Session must be within a transaction
     *
     * @param session    Hibernate session (in a transaction)
     * @param locale     Desired locale
     * @param simulation Simulation name
     * @return The best LocalizedSimulation
     */
    public static LocalizedSimulation getBestSimulation( Session session, Locale locale, String simulation ) {
        Locale englishLocale = LocaleUtils.stringToLocale( "en" );
        Locale languageLocale = LocaleUtils.stringToLocale( locale.getLanguage() );
        boolean useLanguage = !languageLocale.equals( locale );

        Query query = session.createQuery( "select l from LocalizedSimulation as l, Simulation as s, Project as p where (l.simulation = s AND s.project = p AND s.name = :flavor AND (l.locale = :english OR l.locale = :locale" + ( useLanguage ? " OR l.locale = :lang" : "" ) + "))" );
        query.setString( "flavor", simulation );
        query.setLocale( "locale", locale );
        if ( useLanguage ) {
            query.setLocale( "lang", languageLocale );
        }
        query.setLocale( "english", englishLocale );
        List simulations = query.list();

        if ( simulations.size() == 0 ) {
            return null;
        }

        if ( simulations.size() == 1 ) {
            return (LocalizedSimulation) simulations.get( 0 );
        }

        if ( simulations.size() <= 3 ) {
            for ( Object o : simulations ) {
                if ( ( (LocalizedSimulation) o ).getLocale().equals( locale ) ) {
                    return (LocalizedSimulation) o;
                }
            }
            if ( useLanguage ) {
                for ( Object o : simulations ) {
                    if ( ( (LocalizedSimulation) o ).getLocale().equals( languageLocale ) ) {
                        return (LocalizedSimulation) o;
                    }
                }
            }
            return (LocalizedSimulation) simulations.get( 0 );
        }

        throw new RuntimeException( "WARNING: matches more than 3 simulations!" );
    }

    public static final String[] SIM_TITLE_IGNORE_WORDS = {"The", "La", "El"};

    public static String getLeadingSimCharacter( String name, Locale locale ) {
        String str = name;
        for ( String ignoreWord : SIM_TITLE_IGNORE_WORDS ) {
            if ( str.startsWith( ignoreWord + " " ) ) {
                str = str.substring( ignoreWord.length() + 1 );
            }
        }
        return str.substring( 0, 1 ).toUpperCase( locale );
    }

    public static String encodeCharacterId( String chr ) {
        /*
        StringBuffer buf = new StringBuffer();
        byte[] bytes = chr.getBytes();
        for ( Byte b : bytes ) {
            buf.append( Integer.toHexString( b.intValue() ) );
        }
        return buf.toString();
        */
        return chr;
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
        final PhetLocalizer phetLocalizer = (PhetLocalizer) PhetWicketApplication.get().getResourceSettings().getLocalizer();

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

                boolean aTranslated = a.getLocale().equals( locale );
                boolean bTranslated = b.getLocale().equals( locale );

                if ( !bTranslated && aTranslated ) {
                    return -1;
                }

                if ( !aTranslated && bTranslated ) {
                    return 1;
                }

                if ( a.getSimulation().getName().equals( b.getSimulation().getName() ) ) {
                    if ( a.getLocale().equals( locale ) ) {
                        return -1;
                    }
                    if ( b.getLocale().equals( locale ) ) {
                        return 1;
                    }

                    String localeA = StringUtils.getLocaleTitle( a.getLocale(), locale, phetLocalizer );
                    String localeB = StringUtils.getLocaleTitle( b.getLocale(), locale, phetLocalizer );
                    return localeA.compareToIgnoreCase( localeB );
                }

                String aGlobalTitle = map.get( a.getSimulation().getName() );
                String bGlobalTitle = map.get( b.getSimulation().getName() );

                boolean aGlobal = aGlobalTitle != null;
                boolean bGlobal = bGlobalTitle != null;

                if ( aGlobal && bGlobal ) {

                    for ( String ignoreWord : SIM_TITLE_IGNORE_WORDS ) {
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

    /**
     * Used for translation panels / etc, so this should preferably return a simulation in the preferredLocale if it exists
     *
     * @param session         Hibernate session
     * @param preferredLocale Desired locale of the simulation
     * @return A LocalizedSimulation instance
     */
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

    public static List<Translation> getVisibleTranslations( Session session ) {
        List<Translation> ret = new LinkedList<Translation>();
        List li = session.createQuery( "select t from Translation as t where t.visible = true" ).list();

        for ( Object o : li ) {
            Translation translation = (Translation) o;
            if ( translation.getLocale().equals( PhetWicketApplication.getDefaultLocale() ) ) {
                continue;
            }
            ret.add( translation );
        }

        return ret;
    }

    public static LocalizedSimulation pickBestTranslation( Simulation sim, Locale locale ) {
        LocalizedSimulation defaultSim = null;
        LocalizedSimulation languageDefaultSim = null;
        for ( Object o : sim.getLocalizedSimulations() ) {
            LocalizedSimulation lsim = (LocalizedSimulation) o;
            if ( lsim.getLocale().equals( locale ) ) {
                return lsim;
            }
            else if ( lsim.getLocale().equals( PhetWicketApplication.getDefaultLocale() ) ) {
                defaultSim = lsim;
            }
            else if ( lsim.getLocale().getLanguage().equals( locale.getLanguage() ) ) {
                languageDefaultSim = lsim;
            }
        }
        return languageDefaultSim == null ? defaultSim : languageDefaultSim;
    }

    public static void addPreferredFullSimulationList( List<LocalizedSimulation> lsims, Session session, Locale locale ) {
        logger.debug( "1" );
        Criteria criteria = session.createCriteria( Simulation.class )
                .setFetchMode( "localizedSimulations", FetchMode.SELECT )
                .add( Restrictions.eq( "simulationVisible", new Boolean( true ) ) );
        criteria.createCriteria( "project" ).add( Restrictions.eq( "visible", new Boolean( true ) ) );
        //List sims = session.createQuery( "select s from Simulation as s where s.project.visible = true and s.simulationVisible = true" ).list();
        List sims = criteria.list();
        logger.debug( "2" );
        for ( Object sim : sims ) {
            Simulation simulation = (Simulation) sim;
            lsims.add( pickBestTranslation( simulation, locale ) );
        }
        logger.debug( "3" );
    }

    public static List<LocalizedSimulation> preferredFullSimulationList( Session session, Locale locale ) {
        LinkedList<LocalizedSimulation> ret = new LinkedList<LocalizedSimulation>();
        addPreferredFullSimulationList( ret, session, locale );
        return ret;
    }

    /**
     * Wraps an action within a session opening and transaction scope. Handles runtime exceptions.
     * Use wrapTransaction directly if you have access to a requestcycle. This is mainly meant for use when initializing
     * the application.
     *
     * @param task The task to run
     * @return Success (false if task.run returns false OR a runtime exception occurs).
     */
    public static boolean wrapSession( HibernateTask task ) {
        Session session = getInstance().openSession();
        boolean ret = wrapTransaction( session, task );
        session.close();
        return ret;
    }

    /**
     * Wraps an action within a transaction scope. Handles runtime exceptions.
     *
     * @param session Session to use
     * @param task    Task to run
     * @return Success (false if task.run returns false OR a runtime exception occurs).
     */
    public static boolean wrapTransaction( Session session, HibernateTask task ) {
        Transaction tx = null;
        boolean ret;
        try {
            tx = session.beginTransaction();
            tx.setTimeout( 600 );

            ret = task.run( session );

            //logger.debug( "tx isactive: " + tx.isActive() );
            //logger.debug( "tx wascommited: " + tx.wasCommitted() );
            if ( tx.isActive() ) {
                tx.commit();
            }
            else {
                //logger.warn( "tx not active", new RuntimeException( "exception made for stack trace" ) );
            }
        }
        catch( RuntimeException e ) {
            ret = false;
            logger.warn( "Exception", e );
            if ( tx != null && tx.isActive() ) {
                try {
                    logger.warn( "Attempting to roll back" );
                    tx.rollback();
                }
                catch( HibernateException e1 ) {
                    logger.error( "ERROR: Error rolling back transaction!", e1 );
                }
                throw e;
            }
        }
        return ret;
    }

}