/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.util;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

/**
 * User: Sam Reid
 * Date: Dec 3, 2003
 * Time: 12:11:28 PM
 * Copyright (c) Dec 3, 2003 by Sam Reid
 */
public class Histogram {
    Hashtable map;
    private EqualsComparator ec;

    public static interface EqualsComparator {
        boolean equals(Object a, Object b);
    }

    public Histogram() {
        this(new EqualsComparator() {
            public boolean equals(Object a, Object b) {
                return a.equals(b);
            }
        });
    }

    public Histogram(EqualsComparator ec) {
        this.ec = ec;
        map = new Hashtable();
    }

    public Object getEquivalentKey(Object obj) {
        Set keys = map.keySet();
        for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
            Object o = (Object) iterator.next();
            if (ec.equals(o, obj)) {
                return o;
            }
        }
        return null;
    }

    public void add(Object obj) {
        Object eq = getEquivalentKey(obj);
        if (eq != null) {
            int count = getCount(eq);
            map.put(eq, new Integer(count + 1));
        } else
            map.put(obj, new Integer(1));
    }

    private int getCount(Object obj) {
        Integer i = (Integer) map.get(obj);
        return i.intValue();
    }

    public String toString() {
        return map.toString();
    }

    public int numKeys() {
        return map.size();
    }
}
