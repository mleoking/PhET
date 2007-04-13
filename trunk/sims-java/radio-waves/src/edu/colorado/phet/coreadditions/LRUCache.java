/**
 * Class: LRUCache
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Nov 14, 2003
 */
package edu.colorado.phet.coreadditions;

import java.util.HashMap;
import java.util.TreeMap;

/**
 * A cache of objects following an LRU discipline. The objects are cached based on
 * their hashCode() and equals() methods. So to use it to cache a particular class
 * of object, wrap that class in a class that supports hashCode() and equals() for the
 * characteristics of the class of interest.
 */
public class LRUCache {
    private HashMap cache = new HashMap();
    private TreeMap lruMap = new TreeMap();
    private int cacheSize;
    private long cacheCounter;

    public LRUCache( int cacheSize ) {
        this.cacheSize = cacheSize;
    }

    public Object get( Object key ) {
        return cache.get( key );
    }

    public void put( Object key, Object value ) {
        if( cache.size() >= cacheSize ) {
            Object lruCacheEntry = lruMap.get( lruMap.firstKey() );
            cache.remove( lruCacheEntry );
            lruMap.remove( lruMap.firstKey() );
        }
        cache.put( key, value );
        lruMap.put( new Long( cacheCounter++ ), value );
    }
}
