/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.forces1d.phetcommon.util;

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
        ArrayList list = (ArrayList) returnValue;
        lastModified = System.currentTimeMillis();
        if ( returnValue == null ) {
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
        while ( it.hasNext() && !result ) {
            result = ( (ArrayList) it.next() ).contains( value );
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
        while ( this.containsValue( value ) ) {
            Iterator i = this.iterator();
            while ( i.hasNext() ) {
                if ( i.next().equals( value ) ) {
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
        while ( it.hasNext() ) {
            Entry entry = (Entry) it.next();
            List list = (List) entry.getValue();
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
        while ( it.hasNext() ) {
            Entry entry = (Entry) it.next();
            List list = (List) entry.getValue();
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
    }

    ///////////////////////////////////////////////////////
    // Inner classes
    //

    /**
     * Abstract iterator for MultiMaps
     */
    private abstract class MultiMapIterator implements Iterator {
        protected long timeCreated;

        public MultiMapIterator() {
            timeCreated = MultiMap.this.lastModified;
        }

        protected void concurrentModificationCheck() {
            if ( timeCreated < MultiMap.this.lastModified ) {
                throw new ConcurrentModificationException();
            }
        }
    }


    private class ForwardIterator extends MultiMapIterator {
        private Iterator mapIterator;
        private Iterator listIterator;
        private ArrayList currentList;

        ForwardIterator() {
            mapIterator = map.entrySet().iterator();
//            mapIterator = MultiMap.this.entrySet().iterator();
            if ( mapIterator.hasNext() ) {
                nextListIterator();
            }
        }

        public boolean hasNext() {
            concurrentModificationCheck();
            if ( mapIterator.hasNext() ) {
                return true;
            }
            else if ( listIterator != null ) {
                return listIterator.hasNext();
            }
            return false;
        }

        public Object next() {
            concurrentModificationCheck();
            if ( listIterator.hasNext() ) {
                return listIterator.next();
            }
            else if ( mapIterator.hasNext() ) {
                nextListIterator();
                return this.next();
            }
            return null;
        }

        public void remove() {
            concurrentModificationCheck();
            listIterator.remove();
            if ( currentList.size() == 0 ) {
                mapIterator.remove();
            }
            MultiMap.this.lastModified++;
        }

        private void nextListIterator() {
            currentList = (ArrayList) ( (Map.Entry) mapIterator.next() ).getValue();
            listIterator = currentList.iterator();
        }
    }
    // end of ForwardIterator

    /**
     * ReverseIterator
     */
    private class ReverseIterator extends MultiMapIterator {
        private ArrayList currentList;
        private int currentListIdx = 0;

        public ReverseIterator() {
            if ( !map.isEmpty() ) {
                Object currentLastKey = map.lastKey();
                if ( currentLastKey != null ) {
                    currentList = (ArrayList) map.get( currentLastKey );
                    currentListIdx = currentList.size();
                }
            }
        }

        public boolean hasNext() {
            concurrentModificationCheck();
            if ( currentList != null && currentListIdx > 0 ) {
                return true;
            }
            else {
                nextList();
                if ( currentList != null ) {
                    currentListIdx = currentList.size();
                    return hasNext();
                }
            }
            return false;
        }

        public Object next() {
            concurrentModificationCheck();
            if ( currentList != null && currentListIdx > 0 ) {
                currentListIdx--;
                return currentList.get( currentListIdx );
            }
            else {
                nextList();
                if ( currentList != null ) {
                    currentListIdx = currentList.size();
                    return next();
                }
            }
            return null;
        }

        public void remove() {
            concurrentModificationCheck();
            if ( currentList != null ) {
                currentList.remove( currentListIdx );
                if ( currentList.isEmpty() ) {
                    Iterator it = map.keySet().iterator();
                    boolean found = false;
                    while ( it.hasNext() && !found ) {
                        Object o = it.next();
                        if ( o == currentList ) {
                            MultiMap.this.remove( o );
                            found = true;
                        }
                    }
                }
                MultiMap.this.lastModified++;
            }
        }

        private void nextList() {
            Iterator it = map.values().iterator();
            ArrayList nextList = null;
            boolean found = false;
            while ( it.hasNext() && !found ) {
                Object o = it.next();
                if ( o == currentList ) {
                    currentList = nextList;
                    if ( currentList != null ) {
                        currentListIdx = currentList.size();
                    }
                    found = true;
                }
                else {
                    nextList = (ArrayList) o;
                }
            }
        }
    }
    // end of ReverseIterator

    public String toString() {
        return map.toString();
    }

}
