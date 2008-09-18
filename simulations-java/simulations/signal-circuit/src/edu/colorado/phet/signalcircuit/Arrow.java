package edu.colorado.phet.signalcircuit;

import edu.colorado.phet.signalcircuit.electron.wire1d.WirePatch;
import edu.colorado.phet.signalcircuit.paint.Painter;
import edu.colorado.phet.signalcircuit.paint.vector.DefaultVectorPainter;
import edu.colorado.phet.signalcircuit.paint.vector.VectorPainter;
import edu.colorado.phet.signalcircuit.paint.vector.VectorPainterAdapter;
import edu.colorado.phet.signalcircuit.phys2d.DoublePoint;

import java.awt.*;
import java.util.Vector;

public class Arrow implements Painter, SignalListener, SwitchListener {
    double fore;
    double back;
    WirePatch wp;
    boolean signalOn;
    Vector history = new Vector();
    VectorPainterAdapter arrow;
    VectorPainterAdapter arrow2;
    int historySize = 2;

    public Arrow( WirePatch wp ) {
        this.wp = wp;
        VectorPainter vp = new DefaultVectorPainter( Color.red, new BasicStroke( 2 ) );
        arrow = new VectorPainterAdapter( vp );
        Stroke str = new BasicStroke( 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 5, new float[]{2, 2}, 0 );
        VectorPainter vp2 = new DefaultVectorPainter( Color.white, str );
        arrow2 = new VectorPainterAdapter( vp2 );
    }

    public void setSwitchClosed( boolean c ) {
        this.signalOn = c;
        if( !signalOn ) {
            history = new Vector();
        }
    }

    public synchronized void signalMoved( double fore, double back ) {
        this.fore = fore;
        this.back = back;
    }

    public synchronized void paint( Graphics2D g ) {
        //o.O.d(""+fore);
        double len = wp.getLength();
        while( fore >= len ) {
            fore -= len;
        }
        DoublePoint dp = wp.getPosition( fore );
        if( history.size() != 0 ) {
            DoublePoint last = (DoublePoint)history.get( 0 );
            if( fore == 0 ) {
                return;
            }
            Point prev = new Point( (int)last.getX(), (int)last.getY() );
            int length = 20;
            DoublePoint diff = dp.subtract( last );
            try {
                DoublePoint norm = diff.normalize().multiply( length );
                Point d = new Point( (int)norm.getX(), (int)norm.getY() );
                VectorPainterAdapter a = null;
                if( back <= fore ) {
                    a = arrow2;
                }
                else {
                    a = arrow;
                }
                a.setArrow( prev.x, prev.y, d.x, d.y );
                a.paint( g );
            }
            catch( Exception e ) {
            }
        }
        if( history.size() > historySize ) {
            history.remove( 0 );
        }
        history.add( dp );
    }
}
