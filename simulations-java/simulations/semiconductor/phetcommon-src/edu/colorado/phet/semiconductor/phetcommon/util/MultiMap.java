/**
 * Class: MultiMap
 * Package: edu.colorado.phet.common.view.util
 * Author: Another Guy
 * Date: Dec 18, 2003
 */
package edu.colorado.phet.semiconductor.phetcommon.util;

import java.util.*;

public class MultiMap extends TreeMap {
    long lastModified = 0;

    public void add( Object key, Object value ) {
        put( key, value );
    }

    /**
     * Adds an object to the map at the key specified.
     *
     * @param key
     * @param value
     * @return The list at the key, prior to the addition of the specified value
     */
    public Object put( Object key, Object value ) {
        Object returnValue = this.get( key );
        ArrayList list = (ArrayList) returnValue;
        lastModified = System.currentTimeMillis();
        if ( returnValue == null ) {
            list = new ArrayList();
            super.put( key, list );
        }
        list.add( value );
        return returnValue;
    }

    public void putAll( Map map ) {
        lastModified++;
        super.putAll( map );
    }

    public boolean containsValue( Object value ) {
        boolean result = false;
        Iterator it = values().iterator();
        while ( it.hasNext() && !result ) {
            result = ( (ArrayList) it.next() ).contains( value );
        }
        return result;
    }

    public void clear() {
        lastModified++;
        super.clear();
    }

    public Object remove( Object key ) {
        lastModified++;
        try {
            return super.remove( key );
        }
        catch( ClassCastException cce ) {
            cce.printStackTrace();
            return null;
        }
    }

    public Iterator iterator() {
        return new ForwardIterator();
    }

    public Iterator reverseIterator() {
        return new ReverseIterator();
    }

    public void removeValue( Object value ) {
        Iterator forwardIterator = iterator();
        while ( forwardIterator.hasNext() ) {
            Object key = (Object) forwardIterator.next();
            if ( key == value ) {
                forwardIterator.remove();
                break;
            }
        }
    }

    //
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
            mapIterator = MultiMap.this.entrySet().iterator();
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
        private int currentListIdx;

        public ReverseIterator() {
            Object currentLastKey = MultiMap.this.lastKey();
            if ( currentLastKey != null ) {
                currentList = (ArrayList) MultiMap.this.get( currentLastKey );
                currentListIdx = currentList.size();
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
                    Iterator it = MultiMap.this.keySet().iterator();
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
            Iterator it = MultiMap.this.values().iterator();
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


}
