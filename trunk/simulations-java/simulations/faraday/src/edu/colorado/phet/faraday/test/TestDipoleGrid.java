
package edu.colorado.phet.faraday.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObservable;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Test app for the dipole model in #2236.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestDipoleGrid extends JFrame {
    
    private static final PDimension CANVAS_SIZE = new PDimension( 1024, 768 );
    private static final Point2D MAGNET_LOCATION = new Point2D.Double( CANVAS_SIZE.getWidth()/2, CANVAS_SIZE.getHeight()/2 );
    private static final PDimension MAGNET_SIZE = new PDimension( 250, 50 );
    private static final PDimension FIELD_SPACING = new PDimension( 25, 25 );

    public interface IBField {

        public Vector2D getField( double x, double y );
    }

    /*
     * TODO:
     * Investigate licensing.
     * Source: http://www.cs.princeton.edu/introcs/93integration/SimpsonsRule.java.html
     * Copyright 2000Ð2010 by Robert Sedgewick and Kevin Wayne. All rights reserved.
     */
    public static class SimpsonsRule {

        /**
         * Standard normal distribution density function.
         * Replace with any sufficiently smooth function.
         */
        public static double f( double x ) {
            return Math.exp( -x * x / 2 ) / Math.sqrt( 2 * Math.PI );
        }

        /**
         * Integrate f from a to b using Simpson's rule.
         * Increase N for more precision.
         */
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
            setLocation( getX() + delta.getWidth(), getY() + delta.getHeight() );
        }
    }
    
    public static class BField extends SimpleObservable {
        
        private Point2D location;
        private PDimension size;
        
        public BField( PDimension size, Magnet magnet ) {
            this.location = new Point2D.Double( 0, 0 );
            this.size = new PDimension( size );
            magnet.addObserver( new SimpleObserver() {
                public void update() {
                    notifyObservers();
                }
            } );
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
        
        public void setSize( double width, double height ) {
            this.size.setSize( width, height );
            notifyObservers();
        }
        
        public void setSize( PDimension size ) {
            setSize( size.getWidth(), size.getHeight() );
        }
        
        public PDimension getSizeReference() {
            return size;
        }
        
        public double getWidth() {
            return size.getWidth();
        }
        
        public double getHeight() {
            return size.getHeight();
        }
        
        public Vector2D getValue( Point2D p ) {
            return getValue( p.getX(), p.getY() );
        }
        
        public Vector2D getValue( double x, double y ) {
            double bx = 20 * Math.random();
            double by = 20 * Math.random();
            return new Vector2D.Double( bx, by ); //XXX
        }
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
            setOffset( location );
        }
    }
    
    public static class BFieldNode extends PComposite implements SimpleObserver {
        
        private final PDimension spacing;
        private final BField field;
        
        private final PPath boundsNode;
        private final PNode vectorsParentNode;
        
        public BFieldNode( PDimension spacing, BField field ) {
            this.spacing = new PDimension( spacing );
            this.field = field;
            field.addObserver( this );
            
            boundsNode = new PPath();
            boundsNode.setStroke( new BasicStroke( 3f ) );
            boundsNode.setStrokePaint( Color.GREEN );
            addChild( boundsNode );
            
            vectorsParentNode = new PNode();
            addChild( vectorsParentNode );
            
            update();
        }
        
        public void update() {
            setOffset( field.getLocationReference() );
            // draw a rectangle around the bounds
            boundsNode.setPathTo( new Rectangle2D.Double( 0, 0, field.getWidth(), field.getHeight() ) );
            // create vector nodes
            vectorsParentNode.removeAllChildren();
            for ( double y = field.getY(); y <= field.getY() + field.getHeight(); y += spacing.getHeight() ) {
                for ( double x = field.getX(); x <= field.getX() + field.getWidth(); x += spacing.getWidth() ) {
                    Vector2D vector = field.getValue( x, y );
                    VectorNode vectorNode = new VectorNode( vector );
                    vectorNode.setOffset( x, y );
                    vectorsParentNode.addChild( vectorNode );
                }
            }
        }
    }
    
    public static class VectorNode extends PComposite {
        
        public VectorNode( Vector2D vector ) {
            Point2D tailLocation = new Point2D.Double( 0, 0 );
            Point2D tipLocation = new Point2D.Double( vector.getX(), vector.getY() );
            double headHeight = 0.25 * vector.getMagnitude();
            double headWidth = headHeight;
            double tailWidth = 2;
            ArrowNode arrowNode = new ArrowNode( tailLocation, tipLocation, headHeight, headWidth, tailWidth );
            addChild( arrowNode );
        }
    }
    
    public TestDipoleGrid() {

        final PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( new Dimension( (int) CANVAS_SIZE.getWidth(), (int) CANVAS_SIZE.getHeight() ) );
        setContentPane( canvas );

        final Magnet magnet = new Magnet( MAGNET_LOCATION, MAGNET_SIZE );
        final BField field = new BField( CANVAS_SIZE, magnet );
        
        final MagnetNode magnetNode = new MagnetNode( magnet );
        magnetNode.addInputEventListener( new CursorHandler() );
        magnetNode.addInputEventListener( new PDragSequenceEventHandler() {
            @Override
            public void drag( PInputEvent event ) {
                PDimension delta = event.getDeltaRelativeTo( magnetNode.getParent() );
                magnet.translate( delta );
            }
        });

        final BFieldNode fieldNode = new BFieldNode( FIELD_SPACING, field );
        canvas.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                field.setSize( canvas.getSize().getWidth(), canvas.getSize().getHeight() );
            }
        });
        
        // rendering order
        canvas.getLayer().addChild( fieldNode );
        canvas.getLayer().addChild( magnetNode );
        
        pack();
    }

    public static void main( String[] args ) {
        JFrame frame = new TestDipoleGrid();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }

}
