/*
 * Written by Dawid Kurzyniec, on the basis of public specifications and
 * public domain sources from JSR 166, and released to the public domain,
 * as explained at http://creativecommons.org/licenses/publicdomain.
 */

/*
 * Copied from backport-util-concurrent so that weaved code can also be used
 * with 1.2 and 1.3 JVMs. Only 1.5+ methods are copied.
 */
package net.sourceforge.retroweaver.runtime.java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

/**
 * Augments {@link java.util.Collections} with methods added in Java 5.0
 * and higher. Adds support for dynamically typesafe collection wrappers,
 * and several utility methods.
 *
 * @see java.util.Collections
 */
public class Collections_ {

    private Collections_() {}

    // checked views

    public static Collection checkedCollection(Collection c, Class type) {
        return new CheckedCollection(c, type);
    }

    public static Set checkedSet(Set s, Class type) {
        return new CheckedSet(s, type);
    }

    public static SortedSet checkedSortedSet(SortedSet s, Class type) {
        return new CheckedSortedSet(s, type);
    }

    public static List checkedList(List l, Class type) {
        return new CheckedList(l, type);
    }

    public static Map checkedMap(Map m, Class keyType, Class valueType) {
        return new CheckedMap(m, keyType, valueType);
    }

    public static SortedMap checkedSortedMap(SortedMap m, Class keyType, Class valueType) {
        return new CheckedSortedMap(m, keyType, valueType);
    }

    // empty views

    public static Set emptySet() {
        return java.util.Collections.EMPTY_SET;
    }

    public static List emptyList() {
        return java.util.Collections.EMPTY_LIST;
    }

    public static Map emptyMap() {
        return java.util.Collections.EMPTY_MAP;
    }

    // other utils

    public static Comparator reverseOrder(Comparator cmp) {
        return (cmp instanceof ReverseComparator)
            ? ((ReverseComparator)cmp).cmp
            : cmp == null ? java.util.Collections.reverseOrder() : new ReverseComparator(cmp);
    }

    public static int frequency(Collection c, Object o) {
        int freq = 0;
        if (o == null) {
            for (Iterator itr = c.iterator(); itr.hasNext();) {
                if (itr.next() == null) freq++;
            }
        }
        else {
            for (Iterator itr = c.iterator(); itr.hasNext();) {
                if (o.equals(itr.next())) freq++;
            }
        }
        return freq;
    }

    public static boolean disjoint(Collection a, Collection b) { // NOPMD by xlv
        // set.contains() is usually faster than for other collections
        if (a instanceof Set && (!(b instanceof Set) || a.size() < b.size())) {
            Collection tmp = a;
            a = b;
            b = tmp;
        }
        for (Iterator itr = a.iterator(); itr.hasNext();) {
            if (b.contains(itr.next())) return false;
        }
        return true;
    }

    public static boolean addAll(Collection c, Object[] a) {
        boolean modified = false;
        for (int i=0; i<a.length; i++) {
            modified |= c.add(a[i]);
        }
        return modified;
    }

    // Checked collections
    private static class CheckedCollection implements Collection, Serializable {

        final Collection coll;
        final Class type;
        transient Object[] emptyArr;
        CheckedCollection(Collection coll, Class type) {
            if (coll == null || type == null) throw new NullPointerException(); // NOPMD by xlv
            this.coll = coll;
            this.type = type;
        }

        void typeCheck(Object obj) {
            if (!type.isInstance(obj)) {
                throw new ClassCastException(
                    "Attempted to insert an element of type " + obj.getClass().getName() +
                    " to a collection of type " + type.getName());
            }
        }

        public int size()                        { return coll.size(); }
        public void clear()                      { coll.clear(); }
        public boolean isEmpty()                 { return coll.isEmpty(); }
        public Object[] toArray()                { return coll.toArray(); }
        public Object[] toArray(Object[] a)      { return coll.toArray(a); }
        public boolean contains(Object o)        { return coll.contains(o); }
        public boolean remove(Object o)          { return coll.remove(o); }
        public boolean containsAll(Collection c) { return coll.containsAll(c); }
        public boolean removeAll(Collection c)   { return coll.removeAll(c); }
        public boolean retainAll(Collection c)   { return coll.retainAll(c); }
        public String toString()                 { return coll.toString(); }

        public boolean add(Object o) {
            typeCheck(o);
            return coll.add(o);
        }

        public boolean addAll(Collection c) {
            Object[] checked;
            try {
                checked = c.toArray(getEmptyArr());
            }
            catch (ArrayStoreException e) {
                throw new ClassCastException(
                    "Attempted to insert an element of invalid type " +
                    " to a collection of type " + type.getName());
            }
            return coll.addAll(java.util.Arrays.asList(checked));
        }

        public Iterator iterator() {
            return new Itr(coll.iterator());
        }

        protected Object[] getEmptyArr() {
            if (emptyArr == null) emptyArr = (Object[])Array.newInstance(type, 0);
            return emptyArr;
        }

        class Itr implements Iterator {
            final Iterator itr;
            Itr(Iterator itr)            { this.itr = itr; }
            public boolean hasNext()     { return itr.hasNext(); }
            public Object next()         { return itr.next(); }
            public void remove()         { itr.remove(); }
        }
    }

    private static class CheckedList extends CheckedCollection
        implements List, Serializable
    {
        final List list;
        CheckedList(List list, Class type) {
            super(list, type);
            this.list = list;
        }
        public Object get(int index)     { return list.get(index); }
        public Object remove(int index)  { return list.remove(index); }
        public int indexOf(Object o)     { return list.indexOf(o); }
        public int lastIndexOf(Object o) { return list.lastIndexOf(o); }

        public int hashCode()            { return list.hashCode(); }
        public boolean equals(Object o)  { return o == this || list.equals(o); }

        public Object set(int index, Object element) {
            typeCheck(element);
            return list.set(index, element);
        }

        public void add(int index, Object element) {
            typeCheck(element);
            list.add(index, element);
        }

        public boolean addAll(int index, Collection c) {
            Object[] checked;
            try {
                checked = c.toArray(getEmptyArr());
            }
            catch (ArrayStoreException e) {
                throw new ClassCastException(
                    "Attempted to insert an element of invalid type " +
                    " to a list of type " + type.getName());
            }

            return list.addAll(index, java.util.Arrays.asList(checked));
        }

        public List subList(int fromIndex, int toIndex) {
            return new CheckedList(list.subList(fromIndex, toIndex), type);
        }

        public ListIterator listIterator() {
            return new ListItr(list.listIterator());
        }

        public ListIterator listIterator(int index) {
            return new ListItr(list.listIterator(index));
        }

        private class ListItr implements ListIterator {
            final ListIterator itr;
            ListItr(ListIterator itr)    { this.itr = itr; }
            public boolean hasNext()     { return itr.hasNext(); }
            public boolean hasPrevious() { return itr.hasPrevious(); }
            public int nextIndex()       { return itr.nextIndex(); }
            public int previousIndex()   { return itr.previousIndex(); }
            public Object next()         { return itr.next(); }
            public Object previous()     { return itr.previous(); }
            public void remove()         { itr.remove(); }

            public void set(Object element) {
                typeCheck(element);
                itr.set(element);
            }

            public void add(Object element) {
                typeCheck(element);
                itr.add(element);
            }
        }
    }

    private static class CheckedSet extends CheckedCollection
        implements Set, Serializable
    {
        CheckedSet(Set set, Class type) {
            super(set, type);
        }

        public int hashCode()            { return coll.hashCode(); }
        public boolean equals(Object o)  { return o == this || coll.equals(o); }
    }

    private static class CheckedSortedSet extends CheckedSet
        implements SortedSet, Serializable
    {
        final SortedSet set;
        CheckedSortedSet(SortedSet set, Class type) {
            super(set, type);
            this.set = set;
        }
        public Object first()          { return set.first(); }
        public Object last()           { return set.last(); }
        public Comparator comparator() { return set.comparator(); }

        public SortedSet headSet(Object toElement) {
            return new CheckedSortedSet(set.headSet(toElement), type);
        }

        public SortedSet tailSet(Object fromElement) {
            return new CheckedSortedSet(set.tailSet(fromElement), type);
        }

        public SortedSet subSet(Object fromElement, Object toElement) {
            return new CheckedSortedSet(set.subSet(fromElement, toElement), type);
        }
    }

//    public static NavigableSet checkedNavigableSet(NavigableSet set, Class type) {
//        return new CheckedNavigableSet(set, type);
//    }
//
//    private static class CheckedNavigableSet extends CheckedSortedSet
//        implements NavigableSet, Serializable
//    {
//        final NavigableSet set;
//        CheckedNavigableSet(NavigableSet set, Class type) {
//            super(set, type);
//            this.set = set;
//        }
//        public Object lower(Object e)   { return set.lower(e); }
//        public Object floor(Object e)   { return set.floor(e); }
//        public Object ceiling(Object e) { return set.ceiling(e); }
//        public Object higher(Object e)  { return set.higher(e); }
//        public Object pollFirst()       { return set.pollFirst(); }
//        public Object pollLast()        { return set.pollLast(); }
//
//        public Iterator descendingIterator() {
//            return new Itr(set.descendingIterator());
//        }
//
//        public NavigableSet navigableSubSet(Object fromElement,
//                                            Object toElement) {
//            return new CheckedNavigableSet(
//                set.navigableSubSet(fromElement, toElement), type);
//        }
//
//        public NavigableSet navigableHeadSet(Object toElement) {
//            return new CheckedNavigableSet(set.navigableHeadSet(toElement), type);
//        }
//
//        public NavigableSet navigableTailSet(Object fromElement) {
//            return new CheckedNavigableSet(set.navigableTailSet(fromElement), type);
//        }
//    }

    private static class CheckedMap implements Map, Serializable {
        final Map map;
        final Class keyType, valueType;
        transient Set entrySet;

        CheckedMap(Map map, Class keyType, Class valueType) {
            if (map == null || keyType == null || valueType == null) {
                throw new NullPointerException(); // NOPMD by xlv
            }
            this.map = map;
            this.keyType = keyType;
            this.valueType = valueType;
        }

        private void typeCheckKey(Object key) {
            if (!keyType.isInstance(key)) {
                throw new ClassCastException(
                    "Attempted to use a key of type " + key.getClass().getName() +
                    " with a map with keys of type " + keyType.getName());
            }
        }

        private void typeCheckValue(Object value) {
            if (!valueType.isInstance(value)) {
                throw new ClassCastException(
                    "Attempted to use a value of type " + value.getClass().getName() +
                    " with a map with values of type " + valueType.getName());
            }
        }

        public int hashCode()                  { return map.hashCode(); }
        public boolean equals(Object o)        { return o == this || map.equals(o); }

        public int size()                      { return map.size(); }
        public void clear()                    { map.clear(); }
        public boolean isEmpty()               { return map.isEmpty(); }
        public boolean containsKey(Object key) { return map.containsKey(key); }
        public boolean containsValue(Object value)
                                               { return map.containsValue(value); }

        // key and value sets do not support additions
        public Collection values()             { return map.values(); }
        public Set keySet()                    { return map.keySet(); }

        private transient Object[] emptyKeyArray;
        private transient Object[] emptyValueArray;

        public void putAll(Map m) {
            // for compatibility with 5.0, all-or-nothing semantics
            if (emptyKeyArray == null) {
                emptyKeyArray = (Object[])Array.newInstance(keyType, 0);
            }
            if (emptyValueArray == null) {
                emptyValueArray = (Object[])Array.newInstance(valueType, 0);
            }

            Object[] keys, values;

            try {
                keys = m.keySet().toArray(emptyKeyArray);
            }
            catch (ArrayStoreException e) {
                throw new ClassCastException(
                    "Attempted to use an invalid key type " +
                    " with a map with keys of type " + keyType.getName());
            }
            try {
                values = m.keySet().toArray(emptyKeyArray);
            }
            catch (ArrayStoreException e) {
                throw new ClassCastException(
                    "Attempted to use an invalid value type " +
                    " with a map with values of type " + valueType.getName());
            }
            if (keys.length != values.length) {
                throw new ConcurrentModificationException();
            }
            for (int i=0; i<keys.length; i++) {
                map.put(keys[i], values[i]);
            }
        }

        public Set entrySet() {
            if (entrySet == null) entrySet = new EntrySetView(map.entrySet());
            return entrySet;
        }

        public Object get(Object key)          { return map.get(key); }
        public Object remove(Object key)       { return map.remove(key); }

        public Object put(Object key, Object value) {
            typeCheckKey(key);
            typeCheckValue(value);
            return map.put(key, value);
        }

        private class EntrySetView extends AbstractSet implements Set {
            final Set entrySet;
            EntrySetView(Set entrySet)        { this.entrySet = entrySet; }
            public int size()                 { return entrySet.size(); }
            public boolean isEmpty()          { return entrySet.isEmpty(); }
            public boolean remove(Object o)   { return entrySet.remove(o); }
            public void clear()               { entrySet.clear(); }

            public boolean contains(Object o) {
                if (!(o instanceof Map.Entry)) return false;
                return entrySet.contains(new EntryView((Map.Entry)o));
            }

            public Iterator iterator() {
                final Iterator itr = entrySet.iterator();
                return new Iterator() {
                    public boolean hasNext() { return itr.hasNext(); }
                    public Object next()     { return new EntryView((Map.Entry)itr.next()); }
                    public void remove()     { itr.remove(); }
                };
            }

            public Object[] toArray() {
                Object[] a = entrySet.toArray();
                if (a.getClass().getComponentType().isAssignableFrom(EntryView.class)) {
                    for (int i=0; i<a.length; i++) {
                        a[i] = new EntryView( (Entry) a[i]);
                    }
                    return a;
                }
                else {
                    Object[] newa = new Object[a.length];
                    for (int i=0; i<a.length; i++) {
                        newa[i] = new EntryView( (Entry) a[i]);
                    }
                    return newa;
                }
            }

             public Object[] toArray(Object[] a) {
                 Object[] base;
                 if (a.length == 0) {
                     base = a;
                 }
                 else {
                     base = (Object[])(Array.newInstance(a.getClass().getComponentType(), a.length));
                 }
                 base = entrySet.toArray(base);
                 // if the returned array is type-incompatible with EntryView,
                 // tough - we can't tolerate this anyway
                 for (int i=0; i<base.length; i++) {
                     base[i] = new EntryView((Map.Entry)base[i]);
                 }
                 if (base.length > a.length) {
                     a = base;
                 }
                 else {
                     // need to copy back to a
                     System.arraycopy(base, 0, a, 0, base.length);
                     if (base.length < a.length) a[base.length] = null;
                 }
                 return a;
            }
        }

        private class EntryView implements Map.Entry, Serializable {
            final Map.Entry entry;
            EntryView(Map.Entry entry) {
                this.entry = entry;
            }
            public Object getKey()          { return entry.getKey(); }
            public Object getValue()        { return entry.getValue(); }
            public int hashCode()           { return entry.hashCode(); }
            public boolean equals(Object o) {
                if (o == this) return true;
                if (!(o instanceof Map.Entry)) return false;
                Map.Entry e = (Map.Entry)o;
                return eq(getKey(), e.getKey()) && eq(getValue(), e.getValue());
            }

            public Object setValue(Object val) {
                typeCheckValue(val);
                return entry.setValue(val);
            }
        }
    }

    private static boolean eq(Object o1, Object o2) {
        return (o1 == null) ? o2 == null : o1.equals(o2);
    }

    private static class CheckedSortedMap extends CheckedMap
                                          implements SortedMap {
        final SortedMap map;
        CheckedSortedMap(SortedMap map, Class keyType, Class valueType) {
            super(map, keyType, valueType);
            this.map = map;
        }
        public Comparator comparator()  { return map.comparator(); }
        public Object firstKey()        { return map.firstKey(); }
        public Object lastKey()         { return map.lastKey(); }

        public SortedMap subMap(Object fromKey, Object toKey) {
            return new CheckedSortedMap(map.subMap(fromKey, toKey), keyType, valueType);
        }

        public SortedMap headMap(Object toKey) {
            return new CheckedSortedMap(map.headMap(toKey), keyType, valueType);
        }

        public SortedMap tailMap(Object fromKey) {
            return new CheckedSortedMap(map.tailMap(fromKey), keyType, valueType);
        }
    }

    private static class SetFromMap extends AbstractSet implements Serializable {

        private final static Object PRESENT = Boolean.TRUE;

        final Map map;
        transient Set keySet;

        SetFromMap(Map map) {
            this.map = map;
            this.keySet = map.keySet();
        }

        public int hashCode()               { return keySet.hashCode(); }
        public int size()                   { return map.size(); }
        public void clear()                 { map.clear(); }
        public boolean isEmpty()            { return map.isEmpty(); }
        public boolean add(Object o)        { return map.put(o, PRESENT) == null; }
        public boolean contains(Object o)   { return map.containsKey(o); }
        public boolean equals(Object o)     { return o == this || keySet.equals(o); }
        public boolean remove(Object o)     { return map.remove(o) == PRESENT; }

        public boolean removeAll(Collection c) { return keySet.removeAll(c); }
        public boolean retainAll(Collection c) { return keySet.retainAll(c); }
        public Iterator iterator()             { return keySet.iterator(); }
        public Object[] toArray()              { return keySet.toArray(); }
        public Object[] toArray(Object[] a)    { return keySet.toArray(a); }

        public boolean addAll(Collection c) {
            boolean modified = false;
            for (Iterator it = c.iterator(); it.hasNext();) {
                modified |= (map.put(it.next(), PRESENT) == null);
            }
            return modified;
        }

        private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException
        {
            in.defaultReadObject();
            keySet = map.keySet();
        }
    }

    private static class ReverseComparator implements Comparator, Serializable {
        final Comparator cmp;
        ReverseComparator(Comparator cmp) {
            this.cmp = cmp;
        }
        public int compare(Object o1, Object o2) {
            return cmp.compare(o2, o1);
        }
    }
}
