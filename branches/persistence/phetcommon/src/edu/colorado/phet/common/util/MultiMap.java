/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.util;

import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;

import java.util.*;

/**
 * MultiMap
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MultiMap implements Map {
    private long lastModified = 0;
    private TreeMap map = new TreeMap();

    /**
     * @param key
     * @param value
     * @deprecated
     */
    public Object add( Object key, Object value ) {
        return put( key, value );
    }

    /**
     * Adds an object to the map at the key specified. Note that the name for this method cannot
     * be "put", or it will override that method in the Map interface, which must be preserved for
     * XMLEncoder and XMLDecoder to work properly.
     *
     * @param key
     * @param value
     * @return The list at the key, prior to the addition of the specified value
     */
    public Object put( Object key, Object value ) {
        Object returnValue = map.get( key );
        ArrayList list = (ArrayList)returnValue;
        lastModified = System.currentTimeMillis();
        if( returnValue == null ) {
            list = new ArrayList();
            map.put( key, list );
            returnValue = list;
        }
        list.add( value );
        return returnValue;
    }

    public Object lastKey() {
        return map.lastKey();
    }

    public void putAll( Map map ) {
        lastModified++;
        map.putAll( map );
    }

    public boolean containsValue( Object value ) {
        boolean result = false;
        Iterator it = map.values().iterator();
        while( it.hasNext() && !result ) {
            result = ( (ArrayList)it.next() ).contains( value );
        }
        return result;
    }

    public void clear() {
        lastModified++;
        map.clear();
    }

    public Object remove( Object key ) {
        lastModified++;
        return map.remove( key );
    }

    public void removeValue( Object value ) {
        while( this.containsValue( value ) ) {
            Iterator i = this.iterator();
            while( i.hasNext() ) {
                if( i.next().equals( value ) ) {
                    i.remove();
                    break;
                }
            }
            lastModified++;
        }
    }

    public Iterator iterator() {
        return new ForwardIterator();
    }

    public Iterator reverseIterator() {
        return new ReverseIterator();
    }

    /**
     * Returns the number of values in the MultiMap. Note that this is not
     * neccessarilly the same as the number of entries.
     *
     * @return
     */
    public int size() {
        int n = 0;
        Iterator it = map.entrySet().iterator();
        while( it.hasNext() ) {
            Entry entry = (Entry)it.next();
            List list = (List)entry.getValue();
            n += list.size();
        }
        return n;
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public boolean containsKey( Object key ) {
        return map.containsKey( key );
    }

    /**
     * Returns all the values in the map. The list returned will have an entry for
     * every value in the map, rather than one for every key.
     *
     * @return
     */
    public Collection values() {
        ArrayList values = new ArrayList();
        Iterator it = map.entrySet().iterator();
        while( it.hasNext() ) {
            Entry entry = (Entry)it.next();
            List list = (List)entry.getValue();
            values.addAll( list );
        }
        return values;
    }

    public Set entrySet() {
        throw new RuntimeException( "not implemented" );
    }

    public Set keySet() {
        return map.keySet();
    }

    public Object get( Object key ) {
        return map.get( key );
    }

    ///////////////////////////////////////////////////////
    // Persistence support
    public TreeMap getMap() {
        return map;
    }

    public void setMap( TreeMap map ) {
        this.map = map;

        removeNullValues();

    }

    private void removeNullValues() {
        Set keys = map.keySet();

        for( Iterator iterator = keys.iterator(); iterator.hasNext(); ) {
            Object o = iterator.next();
            Object list = map.get( o );
            if( list instanceof List ) {
                List myList = (List)list;
                for( int i = 0; i < myList.size(); i++ ) {
                    Object o1 = myList.get( i );
                    if( o1 == null ) {
                        myList.remove( i );
                        i = -1;
                    }
                }
            }
        }
    }

    ///////////////////////////////////////////////////////
    // Inner classes
    //

    /**
     * Abstract iterator for MultiMaps
     */
    private abstract class MultiMapIterator implements Iterator {
        protected long timeCreated;
        protected Iterator myIterator;

        public MultiMapIterator() {
            timeCreated = MultiMap.this.lastModified;
        }

        protected void concurrentModificationCheck() {
            if( timeCreated < MultiMap.this.lastModified ) {
                throw new ConcurrentModificationException();
            }
        }

        protected List createTotalList() {
            ArrayList totalList = new ArrayList();

            Iterator mapIterator = map.entrySet().iterator();
            while( mapIterator.hasNext() ) {
                Object o = mapIterator.next();
                Entry entry = (Entry)o;
                List list = (List)entry.getValue();
                totalList.addAll( list );
            }
            return totalList;
        }

        public boolean hasNext() {
            concurrentModificationCheck();
            return myIterator.hasNext();
        }

        public Object next() {
            concurrentModificationCheck();
            return myIterator.next();
        }

        public void remove() {
            throw new RuntimeException( "Remove not implemented through iterator" );
        }
    }

    /**
     * This implementation takes O(n), even if you always want a low-index item.
     */
    private class ForwardIterator extends MultiMapIterator {

        ForwardIterator() {
            this.myIterator = createTotalList().iterator();
        }

    }
    // end of ForwardIterator

    /**
     * ReverseIterator
     */
    /**
     * This implementation takes O(n), even if you always want a low-index item.
     */
    private class ReverseIterator extends MultiMapIterator {

        ReverseIterator() {
            List list = createTotalList();
            Collections.reverse( list );
            this.myIterator = list.iterator();
        }

    }
    // end of ReverseIterator

}
