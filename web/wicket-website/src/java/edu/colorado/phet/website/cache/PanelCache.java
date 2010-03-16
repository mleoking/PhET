package edu.colorado.phet.website.cache;

import java.util.HashMap;

import org.apache.log4j.Logger;

public class PanelCache {

    private static PanelCache instance;
    private static final Object lock = new Object();

    private HashMap<IPanelCacheEntry, IPanelCacheEntry> cache = new HashMap<IPanelCacheEntry, IPanelCacheEntry>();

    private static Logger logger = Logger.getLogger( PanelCache.class.getName() );

    private PanelCache() {
        // singleton
    }

    /**
     * @return The singleton instance of the panel cache
     */
    public static PanelCache get() {
        synchronized( lock ) {
            if ( instance == null ) {
                instance = new PanelCache();
            }
        }
        return instance;
    }

    public boolean contains( IPanelCacheEntry entry ) {
        synchronized( lock ) {
            return cache.containsKey( entry );
        }
    }

    public IPanelCacheEntry getMatching( IPanelCacheEntry entry ) {
        synchronized( lock ) {
            return cache.get( entry );
        }
    }

    /**
     * Adds an entry if it does not already exist in the cache.
     * Relies on all of the entries' equals() and hashcode() to be correct
     *
     * @param entry The entry to add
     * @return Whether it was added. Will be false if it is already in the cache
     */
    public boolean addIfMissing( IPanelCacheEntry entry ) {
        boolean adding;
        synchronized( lock ) {
            adding = !cache.containsKey( entry );
            if ( adding ) {
                cache.put( entry, entry );
                entry.onEnterCache( this );
            }
        }
        if ( adding ) {
            logger.debug( "added to cache: " + entry );
        }
        return adding;
    }

    public void remove( IPanelCacheEntry entry ) {
        logger.debug( "attempting to remove from cache: " + entry );
        synchronized( lock ) {
            cache.remove( entry );
            entry.onExitCache();
        }
    }

}
