/* Copyright 2004, Sam Reid */
package edu.umd.cs.piccolo;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Support for incremental rendering hints.
 * Sam Reid
 */

public class PRenderingHints {
    private HashMap map = new HashMap();

    public void putRenderingHint( RenderingHints.Key key, Object value ) {
        map.put( key, value );
    }

    public void setAntialias( boolean antialias ) {
        putRenderingHint( RenderingHints.KEY_ANTIALIASING, antialias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF );
    }

    public void apply( Graphics2D graphics ) {
        Collection keys = map.keySet();
        for( Iterator iterator = keys.iterator(); iterator.hasNext(); ) {
            RenderingHints.Key key = (RenderingHints.Key)iterator.next();
            Object value = map.get( key );
            graphics.setRenderingHint( key, value );
        }
    }
}
