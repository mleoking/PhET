package edu.colorado.phet.website.cache;

import java.util.*;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.event.PostDeleteEvent;
import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostUpdateEvent;

import edu.colorado.phet.website.data.LocalizedSimulation;
import edu.colorado.phet.website.data.Project;
import edu.colorado.phet.website.data.Simulation;
import edu.colorado.phet.website.data.util.AbstractChangeListener;
import edu.colorado.phet.website.data.util.HibernateEventListener;
import edu.colorado.phet.website.util.HibernateUtils;

/**
 * Used for holding lists of simulations, etc.
 */
public class SimulationCache {
    private static Map<Locale, Map<Integer, Integer>> preferredSims = new HashMap<Locale, Map<Integer, Integer>>();
    private static Map<Locale, List<Integer>> fullSortedLocalizedMap = new HashMap<Locale, List<Integer>>();

    private static final Logger logger = Logger.getLogger( SimulationCache.class.getName() );

    static {
        AbstractChangeListener invalidator = new AbstractChangeListener() {
            @Override
            public void onInsert( Object object, PostInsertEvent event ) {
                invalidate();
            }

            @Override
            public void onUpdate( Object object, PostUpdateEvent event ) {
                invalidate();
            }

            @Override
            public void onDelete( Object object, PostDeleteEvent event ) {
                invalidate();
            }
        };
        HibernateEventListener.addListener( Project.class, invalidator );
        HibernateEventListener.addListener( Simulation.class, invalidator );
        HibernateEventListener.addListener( LocalizedSimulation.class, invalidator );

    }

    public static synchronized void invalidate() {
        logger.debug( "SimulationCache INVALIDATED" );
        preferredSims = new HashMap<Locale, Map<Integer, Integer>>();
        fullSortedLocalizedMap = new HashMap<Locale, List<Integer>>();
    }

    /**
     * Add best localized simulations (in order) for a particular locale to the list passed in. Session must be open and
     * in the middle of a transaction.
     *
     * @param list    List to add localized simulations to
     * @param session Hibernate session
     * @param locale  Locale (matters for picking of localized simulations AND sorting)
     */
    public static synchronized void addSortedLocalizedSimulations( List<LocalizedSimulation> list, Session session, Locale locale ) {
        if ( fullSortedLocalizedMap.get( locale ) == null ) {
            LinkedList<Integer> lsimIdList = new LinkedList<Integer>();
            fullSortedLocalizedMap.put( locale, lsimIdList );
            Map<Integer, Integer> preferredHash = preferredSims.get( locale );
            if ( preferredHash == null ) {
                preferredHash = new HashMap<Integer, Integer>();
                preferredSims.put( locale, preferredHash );
            }
            Criteria criteria = session.createCriteria( Simulation.class )
                    .setFetchMode( "localizedSimulations", FetchMode.SELECT )
                    .add( Restrictions.eq( "simulationVisible", new Boolean( true ) ) );
            criteria.createCriteria( "project" ).add( Restrictions.eq( "visible", new Boolean( true ) ) );
            List sims = criteria.list();
            List<LocalizedSimulation> lsims = new LinkedList<LocalizedSimulation>();
            for ( Object o : sims ) {
                Simulation sim = (Simulation) o;
                LocalizedSimulation lsim = HibernateUtils.pickBestTranslation( sim, locale );
                preferredHash.put( sim.getId(), lsim.getId() );
                lsims.add( lsim );
            }
            HibernateUtils.orderSimulations( lsims, locale );
            for ( LocalizedSimulation lsim : lsims ) {
                lsimIdList.add( lsim.getId() );
            }
        }
        for ( Integer id : fullSortedLocalizedMap.get( locale ) ) {
            list.add( (LocalizedSimulation) session.load( LocalizedSimulation.class, id ) );
        }
    }
}
