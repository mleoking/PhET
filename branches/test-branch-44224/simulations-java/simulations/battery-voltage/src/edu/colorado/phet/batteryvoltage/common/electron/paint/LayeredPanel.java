package edu.colorado.phet.batteryvoltage.common.electron.paint;

import java.awt.*;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.*;

public class LayeredPanel extends JPanel {
    TreeMap painters = new TreeMap();

    public LayeredPanel() {
    }

    public void remove( Painter p, int layer ) {
        ( (Vector) painters.get( new Integer( layer ) ) ).remove( p );
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

    public void paintComponent( Graphics g ) {
        super.paintComponent( g );
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        Set e = painters.keySet();
        Iterator it = e.iterator();
        while ( it.hasNext() ) {
            Object key = it.next();
            Vector next = (Vector) painters.get( key );
            for ( int i = 0; i < next.size(); i++ ) {
                Painter p = (Painter) next.get( i );
                //util.Debug.traceln("num graphics painters: "+graphicsPainters.size());
                //util.Debug.traceln("Painter["+i+"]="+p);
                p.paint( g2 );
            }
        }
    }
}
