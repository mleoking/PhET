/* Copyright 2004, Sam Reid */
package edu.colorado.phet.selfdrivenparticlemodel.tutorial.unit2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * User: Sam Reid
 * Date: Aug 26, 2005
 * Time: 1:15:13 AM
 * Copyright (c) Aug 26, 2005 by Sam Reid
 */

public class MyMultiMap {
    HashMap map = new HashMap();

    public void add( Number x, Number y ) {
        if( !map.containsKey( x ) ) {
            map.put( x, new ArrayList() );
        }

        getList( x ).add( y );

    }

    public Set keySet() {
        return map.keySet();
    }

    public int numValues( Number key ) {
        return getList( key ).size();
    }

    public List getList( Number key ) {
        return (List)map.get( key );
    }
}
