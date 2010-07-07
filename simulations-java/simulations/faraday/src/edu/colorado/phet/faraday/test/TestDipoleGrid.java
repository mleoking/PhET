/* Copyright 2010, University of Colorado */

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
 * Test app for dipole mode fix in #2236.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestDipoleGrid extends JFrame {
    
    private static final PDimension MAGNET_SIZE = new PDimension( 250, 50 ); // faraday uses 250x50
    private static final PDimension BFIELD_SIZE = new PDimension( 1024, 768 );
    private static final PDimension BFIELD_SPACING = new PDimension( 40, 40 ); // faraday uses 40x40

    /*
     * TODO:
     * Investigate licensing of SimpsonsRule.
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
    
    /**
     * Container for model elements.
     */
    public static class TestModel {
        
        private final Magnet magnet;
        private final BField bField;
        
        public TestModel( PDimension magnetSize, PDimension fieldSize ) {
            magnet = new Magnet( new Point2D.Double( fieldSize.getWidth()/2, fieldSize.getHeight()/2 ), magnetSize );
            bField = new BField( fieldSize, magnet );
        }
        
        public Magnet getMagnet() {
            return magnet;
        }
        
        public BField getBField() {
            return bField;
        }
    }
    
    /**
     * Magnet model.
     */
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
    
    /**
     * B-field model.
     */
    public static class BField extends SimpleObservable {
        
        private IBFieldEvaluator evaluator;
        private Point2D location;
        private PDimension size;
        
        public BField( PDimension size, Magnet magnet ) {
            evaluator = new RandomBFieldEvaluator(); //XXX
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
            return evaluator.getValue( x, y );
        }
        
        public int randomSign() {
            return ( ( Math.random() < 0.5 ) ? 1 : -1 );
        }
    }
    
    /**
     * Interface for all B-field evaluation strategies.
     */
    public interface IBFieldEvaluator {
        public Vector2D getValue( double x, double y );
    }
    
    /**
     * Creates a totally random B-field vector.
     */
    public static class RandomBFieldEvaluator implements IBFieldEvaluator {

        public Vector2D getValue( double x, double y ) {
            double bx = 0.85 * BFIELD_SPACING.getWidth()/2 * Math.random() * randomSign();
            double by = 0.85 * BFIELD_SPACING.getHeight()/2 * Math.random() * randomSign();
            return new Vector2D.Double( bx, by ); //XXX
        }
        
        public int randomSign() {
            return ( ( Math.random() < 0.5 ) ? 1 : -1 );
        }
    }

    /**
     * Visual representation of the magnet.
     */
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
            setOffset( magnet.getLocationReference() );
        }
    }
    
    /**
     * Visual representation of the B-field, a grid of 2D vectors.
     */
    public static class BFieldNode extends PComposite implements SimpleObserver {
        
        private final PDimension spacing;
        private final BField bField;
        
        private final PPath boundsNode;
        private final PNode vectorsParentNode;
        
        public BFieldNode( PDimension spacing, BField bField ) {
            this.spacing = new PDimension( spacing );
            this.bField = bField;
            bField.addObserver( this );
            
            boundsNode = new PPath();
            boundsNode.setStroke( new BasicStroke( 3f ) );
            boundsNode.setStrokePaint( Color.GREEN );
            addChild( boundsNode );
            
            vectorsParentNode = new PNode();
            addChild( vectorsParentNode );
            
            update();
        }
        
        public void update() {
            setOffset( bField.getLocationReference() );
            // draw a rectangle around the bounds
            boundsNode.setPathTo( new Rectangle2D.Double( 0, 0, bField.getWidth(), bField.getHeight() ) );
            // create vector nodes
            vectorsParentNode.removeAllChildren();
            for ( double y = bField.getY(); y <= bField.getY() + bField.getHeight(); y += spacing.getHeight() ) {
                for ( double x = bField.getX(); x <= bField.getX() + bField.getWidth(); x += spacing.getWidth() ) {
                    Vector2D vector = bField.getValue( x, y );
                    Vector2DNode vectorNode = new Vector2DNode( vector );
                    vectorNode.setOffset( x, y );
                    vectorsParentNode.addChild( vectorNode );
                }
            }
        }
    }
    
    /**
     * Visual representation of a 2D vector.
     * The length and head-size vary with magnitude.
     */
    public static class Vector2DNode extends PComposite {
        
        public Vector2DNode( Vector2D vector ) {
            Point2D tailLocation = new Point2D.Double( 0, 0 );
            Point2D tipLocation = new Point2D.Double( vector.getX(), vector.getY() );
            double headHeight = 0.25 * vector.getMagnitude();
            double headWidth = headHeight;
            double tailWidth = 2;
            ArrowNode arrowNode = new ArrowNode( tailLocation, tipLocation, headHeight, headWidth, tailWidth );
            addChild( arrowNode );
        }
    }
    
    /**
     * Piccolo canvas.
     */
    public static class TestCanvas extends PhetPCanvas {

        public TestCanvas( final TestModel model ) {

            int canvasWidth = (int) model.getBField().getSizeReference().getWidth();
            int canvasHeight = (int) model.getBField().getSizeReference().getHeight();
            setPreferredSize( new Dimension( canvasWidth, canvasHeight ) );

            final MagnetNode magnetNode = new MagnetNode( model.getMagnet() );
            magnetNode.addInputEventListener( new CursorHandler() );
            magnetNode.addInputEventListener( new PDragSequenceEventHandler() {
                @Override
                public void drag( PInputEvent event ) {
                    PDimension delta = event.getDeltaRelativeTo( magnetNode.getParent() );
                    model.getMagnet().translate( delta );
                }
            } );

            final BFieldNode bFieldNode = new BFieldNode( BFIELD_SPACING, model.getBField() );
            addComponentListener( new ComponentAdapter() {
                @Override
                public void componentResized( ComponentEvent e ) {
                    // adjust the field size to fill the canvas
                    model.getBField().setSize( getSize().getWidth(), getSize().getHeight() );
                }
            } );

            // rendering order
            getLayer().addChild( bFieldNode );
            getLayer().addChild( magnetNode );
        }
    }
    
    /**
     * The main frame.
     */
    public TestDipoleGrid() {
        TestModel model = new TestModel( MAGNET_SIZE, BFIELD_SIZE );
        TestCanvas canvas = new TestCanvas( model );
        setContentPane( canvas );
        pack();
    }

    public static void main( String[] args ) {
        JFrame frame = new TestDipoleGrid();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }

}
