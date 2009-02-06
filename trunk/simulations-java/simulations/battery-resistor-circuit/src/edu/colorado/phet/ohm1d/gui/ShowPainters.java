package edu.colorado.phet.ohm1d.gui;

import java.util.Vector;

import edu.colorado.phet.ohm1d.common.paint.LayeredPainter;
import edu.colorado.phet.ohm1d.common.paint.Painter;

public class ShowPainters {
    Vector v = new Vector();
    int[] levels;
    LayeredPainter lp;

    public ShowPainters( LayeredPainter lp, int level ) {
        this( lp, new int[]{level} );
    }

    public ShowPainters( LayeredPainter lp, int[] levels ) {
        this.lp = lp;
        this.levels = levels;
    }

    public void remove( Painter p ) {
        v.remove( p );
    }

    public void add( Painter p ) {
        v.add( p );
    }

    public static void setShowPainters( boolean t, Vector v, LayeredPainter lp, int level ) {
        for ( int i = 0; i < v.size(); i++ ) {
            Painter p = (Painter) v.get( i );
            if ( t ) {
                if ( !lp.hasPainter( p, level ) ) {
                    lp.addPainter( p, level );
                }
            }
            else {
                while ( lp.hasPainter( p, level ) ) {
                    lp.removePainter( p, level );
                }
            }
        }
    }

    public void setShowPainters( boolean t ) {
        for ( int i = 0; i < levels.length; i++ ) {
            setShowPainters( t, v, lp, levels[i] );
        }
    }
}
