package edu.colorado.phet.website.cache;

import java.util.HashSet;
import java.util.Set;

public class PanelCache {

    private static PanelCache instance;
    private static final Object lock = new Object();

    private Set<IPanelCacheEntry> cache = new HashSet<IPanelCacheEntry>();

    private PanelCache() {
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
            return cache.contains( entry );
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
            adding = !cache.contains( entry );
            if ( adding ) {
                cache.add( entry );
                entry.onEnterCache();
            }
        }
        return adding;
    }

    public void remove( IPanelCacheEntry entry ) {
        synchronized( lock ) {
            cache.remove( entry );
            entry.onExitCache();
        }
    }

}
