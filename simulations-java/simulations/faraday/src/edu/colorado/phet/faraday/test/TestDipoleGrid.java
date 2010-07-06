
package edu.colorado.phet.faraday.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObservable;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Test app for the dipole model in #2236.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestDipoleGrid extends JFrame {
    
    private static final Dimension CANVAS_SIZE = new Dimension( 1024, 768 );
    private static final Point2D MAGNET_LOCATION = new Point2D.Double( CANVAS_SIZE.getWidth()/2, CANVAS_SIZE.getHeight()/2 );
    private static final PDimension MAGNET_SIZE = new PDimension( 250, 50 );

    public interface IBField {

        public Vector2D getField( double x, double y );
    }

    public static class SimpsonsRule {

        /**********************************************************************
         * Standard normal distribution density function.
         * Replace with any sufficiently smooth function.
         **********************************************************************/
        public static double f( double x ) {
            return Math.exp( -x * x / 2 ) / Math.sqrt( 2 * Math.PI );
        }

        /**********************************************************************
         * Integrate f from a to b using Simpson's rule.
         * Increase N for more precision.
         **********************************************************************/
        public static double integrate( double a, double b ) {
            int N = 10000; // precision parameter
            double h = ( b - a ) / ( N - 1 ); // step size

            // 1/3 terms
            double sum = 1.0 / 3.0 * ( f( a ) + f( b ) );

            // 4/3 terms
            for ( int i = 1; i < N - 1; i += 2 ) {
                double x = a + h * i;
                sum += 4.0 / 3.0 * f( x );
            }

            // 2/3 terms
            for ( int i = 2; i < N - 1; i += 2 ) {
                double x = a + h * i;
                sum += 2.0 / 3.0 * f( x );
            }

            return sum * h;
        }
    }

    public static class Magnet extends SimpleObservable {

        private final PDimension size;
        private Point2D location;

        public Magnet( Point2D location, PDimension size ) {
            this.size = new PDimension( size );
            this.location = new Point2D.Double( location.getX(), location.getY() );
        }

        public PDimension getSizeReference() {
            return size;
        }
        
        public void setLocation( double x, double y ) {
            location.setLocation( x, y );
            notifyObservers();
        }

        public void setLocation( Point2D location ) {
            setLocation( location.getX(), location.getY() );
            notifyObservers();
        }

        public Point2D getLocationReference() {
            return location;
        }
        
        public double getX() {
            return location.getX();
        }
        
        public double getY() {
            return location.getY();
        }
        
        public void translate( PDimension delta ) {
            System.out.println( "location before=" + getLocationReference() );
            setLocation( getX() + delta.getWidth(), getY() + delta.getHeight() );
            System.out.println( "location after=" + getLocationReference() );
        }
    }


    public TestDipoleGrid( String gridFilename ) {

        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( CANVAS_SIZE );
        setContentPane( canvas );

        final Magnet magnet = new Magnet( MAGNET_LOCATION, MAGNET_SIZE );
        final MagnetNode magnetNode = new MagnetNode( magnet );
        magnetNode.addInputEventListener( new CursorHandler() );
        magnetNode.addInputEventListener( new PDragSequenceEventHandler() {
            @Override
            public void drag( PInputEvent event ) {
                PDimension delta = event.getDeltaRelativeTo( magnetNode.getParent() );
                System.out.println( "delta=" + delta );//XXX
                magnet.translate( delta );
            }
        });

        canvas.getLayer().addChild( magnetNode );

        pack();
    }

    public static class MagnetNode extends PPath implements SimpleObserver {

        private final Magnet magnet;

        public MagnetNode( Magnet magnet ) {
            this.magnet = magnet;
            magnet.addObserver( this );

            double x = -MAGNET_SIZE.width / 2;
            double y = -MAGNET_SIZE.height / 2;
            setPathTo( new Rectangle2D.Double( x, y, magnet.getSizeReference().width, magnet.getSizeReference().height ) );
            setStroke( new BasicStroke( 3f ) );
            setStrokePaint( Color.RED );
            setPaint( new Color( 0, 0, 0, 0 ) );
            
            update();
        }

        public void update() {
            Point2D location = magnet.getLocationReference();
            System.out.println( "location=" + location );//XXX
            setOffset( location );
        }
    }


    public static void main( String[] args ) {
        JFrame frame = new TestDipoleGrid( "foo" /* filename */);
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }

}
