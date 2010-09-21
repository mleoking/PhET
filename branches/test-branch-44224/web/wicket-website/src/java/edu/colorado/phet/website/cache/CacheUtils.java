package edu.colorado.phet.website.cache;

import org.apache.log4j.Logger;
import org.hibernate.Cache;
import org.hibernate.SessionFactory;

import edu.colorado.phet.website.translation.PhetLocalizer;
import edu.colorado.phet.website.util.HibernateUtils;

public class CacheUtils {

    private static final Logger logger = Logger.getLogger( CacheUtils.class.getName() );

    /**
     * Clears all of the caches
     */
    public static void clearAllCaches() {
        clearSecondLevelCache();
        clearTranslatedStringCache();
        clearPanelCache();
        clearSimulationCache();
    }

    /**
     * Clears the Hibernate second-level cache. This cache is where data pulled or sent to the database is cached.
     */
    public static void clearSecondLevelCache() {
        logger.info( "clearing second level cache" );
        SessionFactory factory = HibernateUtils.getInstance();

        Cache cache = factory.getCache();
        //cache.evictQueryRegions();
        cache.evictEntityRegions();
        cache.evictCollectionRegions();

        // deprecated, but cache.evictQueryRegions is throwing exceptions
        factory.evictQueries();
    }

    /**
     * Clears the cache where translated strings are held.
     */
    public static void clearTranslatedStringCache() {
        logger.info( "clearing translated string cache" );
        PhetLocalizer.get().clearCache();
    }

    /**
     * Clears the panel cache, which is composed of HTML copies of parts of pages
     */
    public static void clearPanelCache() {
        logger.info( "clearing the panel cache" );
        PanelCache.get().clear();
    }

    /**
     * Clears the simulation cache, where the preferred order of best simulations for a locale is cached.
     */
    public static void clearSimulationCache() {
        logger.info( "clearing the simulation cache" );
        SimulationCache.invalidate();
    }
}
