package edu.colorado.phet.ohm1d.common.paint;

import java.awt.*;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

public class LayeredPainter implements Painter {
    TreeMap painters = new TreeMap();

    public void removePainter( Painter p, int layer ) {
        remove( p, layer );
    }

    public boolean hasPainter( Painter p, int layer ) {
        if ( painters.get( new Integer( layer ) ) == null ) {
            return false;
        }
        return ( (Vector) painters.get( new Integer( layer ) ) ).contains( p );
    }

    public void remove( Painter p, int layer ) {
        Vector v = (Vector) painters.get( new Integer( layer ) );
        if ( v != null ) {
            v.remove( p );
        }
    }

    public void addPainter( Painter p ) {
        addPainter( p, 0 );
    }

    public void addPainter( Painter p, int level ) {
        //util.Debug.traceln("Painter added: "+p+", "+p.getClass());
        //new Exception().printStackTrace();

        Vector v = (Vector) painters.get( new Integer( level ) );
        if ( v == null ) {
            v = new Vector();
            painters.put( new Integer( level ), v );
        }
        v.add( p );
    }

    public void paint( Graphics2D g ) {
        Set e = painters.keySet();
        Iterator it = e.iterator();
        while ( it.hasNext() ) {
            Object key = it.next();
            Vector next = (Vector) painters.get( key );
            for ( int i = 0; i < next.size(); i++ ) {
                Painter p = (Painter) next.get( i );
                //util.Debug.traceln("num graphics painters: "+graphicsPainters.size());
                //util.Debug.traceln("Painter["+i+"]="+p);
                p.paint( g );
            }
        }
    }
}
