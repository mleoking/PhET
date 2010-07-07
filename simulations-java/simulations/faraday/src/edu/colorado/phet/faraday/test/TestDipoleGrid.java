/* Copyright 2010, University of Colorado */

package edu.colorado.phet.faraday.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Stroke;
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
 * Test app for dipole model fix in #2236.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestDipoleGrid extends JFrame {
    
    private static final PDimension CANVAS_SIZE = new PDimension( 1024, 768 );
    private static final PDimension MAGNET_SIZE = new PDimension( 250, 50 ); // faraday uses 250x50
    private static final PDimension BFIELD_GRID_SPACING = new PDimension( 40, 40 ); // faraday uses 40x40
    private static final int SIMPSONS_RULE_ITERATIONS = 100; // increase for more precision
    private static final double BFIELD_SCALING_FACTOR = 3;

    /**
     * Interface for all B-field evaluation strategies.
     */
    public interface IBFieldEvaluator {
        public Vector2D getValue( double x, double y );
    }
    
    /**
     * Creates a random B-field vector that fits within the grid spacing
     * of the B-field visualization.
     */
    public static class RandomBFieldEvaluator implements IBFieldEvaluator {

        public Vector2D getValue( double x, double y ) {
            double bx = ( BFIELD_GRID_SPACING.getWidth() / 2 ) * Math.random() * randomSign();
            double by = ( BFIELD_GRID_SPACING.getHeight() / 2 ) * Math.random() * randomSign();
            return new Vector2D.Double( bx, by );
        }

        private int randomSign() {
            return ( ( Math.random() < 0.5 ) ? 1 : -1 );
        }
    }
    
    /**
     * Evaluates the B-field at a point using Mike Dubson's algorithm.
     */
    public static class DubsonBFieldEvaluator implements IBFieldEvaluator {
        
        private final BxFunction bxFunction;
        private final ByFunction byFunction;
        
        public DubsonBFieldEvaluator( Magnet magnet) {
            bxFunction = new BxFunction( magnet.getSizeReference() );
            byFunction = new ByFunction( magnet.getSizeReference() );
        }
        
        public Vector2D getValue( double x, double y ) {
            double bx = bxFunction.evaluate( x, y );
            double by = byFunction.evaluate( x, y );
            return new Vector2D.Double( bx, by );
        }
    }
    
    /*
     * TODO:
     * Investigate licensing of SimpsonsRule implementation.
     * Source: http://www.cs.princeton.edu/introcs/93integration/SimpsonsRule.java.html
     * Copyright 2000Ð2010 by Robert Sedgewick and Kevin Wayne. All rights reserved.
     */
    public static abstract class SimpsonsRule {
        
        public abstract double f( double x );

        /**
         * Integrate f from a to b using Simpson's rule.
         * Increase N for more precision.
         */
        public double integrate( double a, double b ) {
            
            int N = SIMPSONS_RULE_ITERATIONS; // precision parameter
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
     * Base class for Bx and By inner integral.
     * These integrals differ only in their numerator,
     * so base classes implement the getNumerator method.
     */
    public abstract static class BInnerIntegral extends SimpsonsRule {
        
        protected final double x, xp, y, R;
        
        public BInnerIntegral( double x, double xp, double y, double R ) {
            this.x = x;
            this.xp = xp;
            this.y = y;
            this.R = R;
        }
        
        public double f( double phi ) {
            double demoninator = Math.pow( ( R * R ) + ( y * y ) - ( 2 * y * R * Math.sin( phi ) ) + ( ( x - xp ) * ( x - xp ) ), 3. / 2. );
            return getNumerator( phi ) / demoninator;
        }
        
        protected abstract double getNumerator( double phi );
    }
    
    /**
     * Inner integral of Bx function.
     */
    public static class BxInnerIntegral extends BInnerIntegral {
        
        public BxInnerIntegral( double x, double xp, double y, double R ) {
            super( x, xp, y, R );
        }
        
        protected double getNumerator( double phi ) {
            return R - ( y * Math.sin( phi ) );
        }
    }
    
    /**
     * Inner integral of By function.
     */
    public static class ByInnerIntegral extends BInnerIntegral {
        
        public ByInnerIntegral( double x, double xp, double y, double R ) {
            super( x, xp, y, R );
        }
        
        protected double getNumerator( double phi ) {
            return ( x - xp ) * Math.sin( phi );
        }
    }
    
    /**
     * Base class for Bx and By outer integral.
     * These integrals differ only in their inner integral,
     * so base classes implement the getIntegral method.
     */
    public abstract static class BOuterIntegral extends SimpsonsRule {
        
        protected final double x, y, R;
        
        public BOuterIntegral( double x, double y, double R ) {
            this.x = x;
            this.y = y;
            this.R = R;
        }
        
        public double f( double xp ) {
            return getIntegral( xp ).integrate( 0, 2 * Math.PI );
        }
        
        protected abstract BInnerIntegral getIntegral( double xp );
    }
    
    /**
     * Outer integral of Bx function.
     */
    public static class BxOuterIntegral extends BOuterIntegral {
        
        public BxOuterIntegral( double x, double y, double R ) {
            super( x, y, R );
        }
        
        protected BInnerIntegral getIntegral( double xp ) {
            return new BxInnerIntegral( x, xp, y, R );
        }
    }
    
    /**
     * Outer integral of By function.
     */
    public static class ByOuterIntegral extends BOuterIntegral {
        
        public ByOuterIntegral( double x, double y, double R ) {
            super( x, y, R );
        }
        
        protected BInnerIntegral getIntegral( double xp ) {
            return new ByInnerIntegral( x, xp, y, R );
        }
    }
    
    /**
     * Base class for Bx and By functions.
     * These function differ only in the type of their outer integral,
     * so base classes implement the getIntegral method.
     */
    public static abstract class BFunction {

        private static final double C = 1;

        protected final double a; // magnet width
        protected final double R; // cylindrical magnet radius
        protected final double multiplier;

        public BFunction( PDimension magnetSize ) {
            a = magnetSize.getWidth();
            R = magnetSize.getHeight() / 2;
            multiplier = ( C * R ) / ( 4 * Math.PI );
        }
        
        public double evaluate( double x, double y ) {
            return multiplier * getIntegral( x, y ).integrate( -a / 2, a / 2 );
        }
        
        protected abstract BOuterIntegral getIntegral( double x, double y );
    }
    
    /**
     * Bx function, B-field vector x component.
     */
    public static class BxFunction extends BFunction {
        
        public BxFunction( PDimension magnetSize ) {
            super( magnetSize );
        }
        
        protected BOuterIntegral getIntegral( double x, double y ) {
            return new BxOuterIntegral( x, y, R );
        }
    }
    
    /**
     * By function, B-field vector y component.
     */
    public static class ByFunction extends BFunction {
        
        public ByFunction( PDimension magnetSize ) {
            super( magnetSize );
        }
        
        protected BOuterIntegral getIntegral( double x, double y ) {
            return new ByOuterIntegral( x, y, R );
        }
    }
    
    /**
     * Magnet model.
     */
    public static class Magnet extends SimpleObservable {

        private Point2D location;
        private final PDimension size;
        private final IBFieldEvaluator evaluator;

        public Magnet( Point2D location, PDimension size ) {
            this.location = new Point2D.Double( location.getX(), location.getY() );
            this.size = new PDimension( size );
            this.evaluator = new RandomBFieldEvaluator(); //XXX
//            this.evaluator = new DubsonBFieldEvaluator( this ); //XXX
        }

        public void setLocation( double x, double y ) {
            if ( x != getX() || y != getY() ) {
                location.setLocation( x, y );
                notifyObservers();
            }
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
        
        public PDimension getSizeReference() {
            return size;
        }
        
        public double getWidth() {
            return size.getWidth();
        }
        
        public double getHeight() {
            return size.getHeight();
        }
        
        public Vector2D getBFieldValue( double x, double y ) {
            return evaluator.getValue( x, y );
        }
    }
    
    /**
     * Visual representation of the magnet.
     * Origin is at the center of the bounding box.
     */
    public static class MagnetNode extends PPath implements SimpleObserver {
        
        private static final Color FILL_COLOR = new Color( 0, 0, 0, 0 ); // transparent, so we can drag the magnet, but still see the B-field inside
        private static final Color STROKE_COLOR = Color.RED;
        private static final Stroke STROKE = new BasicStroke( 3f );

        private final Magnet magnet;

        public MagnetNode( Magnet magnet ) {
            
            this.magnet = magnet;
            magnet.addObserver( this );

            double x = -magnet.getWidth() / 2;
            double y = -magnet.getHeight() / 2;
            setPathTo( new Rectangle2D.Double( x, y, magnet.getWidth(), magnet.getHeight() ) );
            setPaint( FILL_COLOR );
            setStrokePaint( STROKE_COLOR );
            setStroke( STROKE );
            
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
        
        private static final Stroke BOUNDS_STROKE = new BasicStroke( 3f );
        private static final Color BOUNDS_STROKE_COLOR = Color.GREEN;
        
        private PDimension size;
        private final PDimension spacing;
        private final Magnet magnet;
        
        private final PPath boundsNode;
        private final PNode vectorsParentNode;
        private final IVectorScaler vectorScaler;
        
        public BFieldNode( PDimension size, PDimension spacing, Magnet magnet ) {
            
            setPickable( false );
            setChildrenPickable( false );
            
            this.size = new PDimension( size );
            this.spacing = new PDimension( spacing );
            this.magnet = magnet;
            magnet.addObserver( this );
            vectorScaler = new UnityVectorScaler(); //XXX
//            vectorScaler = new DubsonVectorScaler(); //XXX
            
            boundsNode = new PPath();
            boundsNode.setStroke( BOUNDS_STROKE );
            boundsNode.setStrokePaint( BOUNDS_STROKE_COLOR );
            boundsNode.setPickable( false );
            addChild( boundsNode );
            
            vectorsParentNode = new PNode();
            vectorsParentNode.setPickable( false );
            vectorsParentNode.setChildrenPickable( false );
            addChild( vectorsParentNode );
            
            update();
        }
        
        public void setFieldSize( PDimension size ) {
            if ( !size.equals( this.size ) ) {
                this.size.setSize( size );
                update();
            }
        }
        
        public void update() {
            // draw a rectangle around the bounds
            boundsNode.setPathTo( new Rectangle2D.Double( 0, 0, size.getWidth(), size.getHeight() ) );
            // create vector nodes
            vectorsParentNode.removeAllChildren();
            for ( double y = 0; y <= size.getHeight(); y += spacing.getHeight() ) {
                for ( double x = 0; x <= size.getWidth(); x += spacing.getWidth() ) {
                    Vector2D vector = magnet.getBFieldValue( x, y );
//                    System.out.println( "BFieldNode.update x=" + x + " y=" + y + " bx=" + vector.getX() + " by= " + vector.getY() );//XXX this will affect drag performance!
                    Vector2D vectorScaled = vectorScaler.scale( vector );
                    Vector2DNode vectorNode = new Vector2DNode( vectorScaled );
                    vectorNode.setOffset( x, y );
                    vectorsParentNode.addChild( vectorNode );
                }
            }
        }
    }
    
    /**
     * Interface for vector scaling strategies.
     */
    public interface IVectorScaler {
        public Vector2D scale( Vector2D v );
    }
    
    /**
     * Vector scaling strategy that results in no change to the input vector.
     */
    public static class UnityVectorScaler implements IVectorScaler {
        
        public Vector2D scale( Vector2D v ) {
            return v;
        }
    }
    
    /**
     * Mike Dubsons algorithm for scaling B-field vectors for display.
     */
    public static class DubsonVectorScaler implements IVectorScaler {

        private final double factor;

        public DubsonVectorScaler() {
            this( BFIELD_SCALING_FACTOR );
        }

        public DubsonVectorScaler( double factor ) {
            this.factor = factor;
        }

        public Vector2D scale( Vector2D v ) {
            double multiplier = Math.pow( v.getMagnitude(), 1 / factor );
            double bxScaled = multiplier * ( v.getX() / v.getMagnitude() );
            double byScaled = multiplier * ( v.getY() / v.getMagnitude() );
            return new Vector2D.Double( bxScaled, byScaled );
        }
    }
    
    /**
     * Visual representation of a 2D vector.
     * The length and head-size are proportional to the magnitude.
     */
    public static class Vector2DNode extends PComposite {
        
        public Vector2DNode( Vector2D vector ) {
            
            setPickable( false );
            setChildrenPickable( false );
            
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

        public TestCanvas( PDimension canvasSize, PDimension bFieldSpacing, final Magnet magnet ) {

            setPreferredSize( new Dimension( (int)canvasSize.getWidth(), (int)canvasSize.getHeight() ) );

            // magnet node
            final MagnetNode magnetNode = new MagnetNode( magnet );
            magnetNode.addInputEventListener( new CursorHandler() );
            magnetNode.addInputEventListener( new PDragSequenceEventHandler() {
                @Override
                public void drag( PInputEvent event ) {
                    // adjust the magnet position as the magnet node is dragged
                    PDimension delta = event.getDeltaRelativeTo( magnetNode.getParent() );
                    magnet.translate( delta );
                }
            } );

            // B-field node
            final BFieldNode bFieldNode = new BFieldNode( canvasSize, bFieldSpacing, magnet );
            addComponentListener( new ComponentAdapter() {
                @Override
                public void componentResized( ComponentEvent e ) {
                    // adjust the field size to fill the canvas
                    bFieldNode.setFieldSize( new PDimension( getSize() ) );
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
        Point2D magnetLocation = new Point2D.Double( CANVAS_SIZE.getWidth() / 2, CANVAS_SIZE.getHeight() / 2 ); // centered in canvas
        Magnet magnet = new Magnet( magnetLocation, MAGNET_SIZE );
        TestCanvas canvas = new TestCanvas( CANVAS_SIZE, BFIELD_GRID_SPACING, magnet );
        setContentPane( canvas );
        pack();
    }

    public static void main( String[] args ) {
        JFrame frame = new TestDipoleGrid();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
