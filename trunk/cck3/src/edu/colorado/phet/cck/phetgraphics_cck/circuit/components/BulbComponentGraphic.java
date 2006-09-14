/** Sam Reid*/
package edu.colorado.phet.cck.phetgraphics_cck.circuit.components;

import edu.colorado.phet.cck.model.analysis.CircuitSolutionListener;
import edu.colorado.phet.cck.model.components.Bulb;
import edu.colorado.phet.cck.model.components.CircuitComponent;
import edu.colorado.phet.cck.phetgraphics_cck.CCKModule;
import edu.colorado.phet.cck.phetgraphics_cck.CCKPhetGraphic;
import edu.colorado.phet.cck.phetgraphics_cck.circuit.IComponentGraphic;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.colorado.phet.common_cck.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common_cck.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common_cck.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common_cck.view.util.RectangleUtils;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: May 25, 2004
 * Time: 8:34:54 PM
 * Copyright (c) May 25, 2004 by Sam Reid
 */
public class BulbComponentGraphic extends CCKPhetGraphic implements IComponentGraphic {
    private Bulb component;
    private ModelViewTransform2D transform;
    private CCKModule module;
    private Component parent;
    private static final double WIDTH = 100;
    private static final double HEIGHT = 100;
    private AffineTransform affineTransform;
    private LightBulbGraphic lbg;
    private Rectangle2D.Double srcShape;
    private ArrayList listeners = new ArrayList();
    private SimpleObserver simpleObserver;
    private TransformListener transformListener;
    private CircuitSolutionListener circuitSolutionListener;
    private PhetShapeGraphic highlightGraphic;
    private Stroke highlightStroke = new BasicStroke( 5 );
    private double tilt;

    public BulbComponentGraphic( Component parent, Bulb component, ModelViewTransform2D transform, CCKModule module ) {
        super( parent );
        highlightGraphic = new PhetShapeGraphic( parent, new Area(), Color.yellow );
        this.parent = parent;
        this.component = component;
        this.transform = transform;
        this.module = module;
        simpleObserver = new SimpleObserver() {
            public void update() {
                changed();
            }
        };
        component.addObserver( simpleObserver );

        transformListener = new TransformListener() {
            public void transformChanged( ModelViewTransform2D mvt ) {
                changed();
            }
        };
        transform.addTransformListener( transformListener );
        circuitSolutionListener = new CircuitSolutionListener() {
            public void circuitSolverFinished() {
                changeIntensity();
            }
        };
        module.getCircuitSolver().addSolutionListener( circuitSolutionListener );

        srcShape = new Rectangle2D.Double( 0, 0, WIDTH, HEIGHT );
        lbg = new LightBulbGraphic( srcShape );
        lbg.setIntensity( 0 );
        double w = lbg.getCoverShape().getBounds().getWidth() / 2;
        double h = lbg.getCoverShape().getBounds().getHeight();
        tilt = -Math.atan2( w, h );
        updateTransform();
    }

    public static double determineTilt() {
        LightBulbGraphic lbg = new LightBulbGraphic( new Rectangle2D.Double( 0, 0, WIDTH, HEIGHT ) );
        double w = lbg.getCoverShape().getBounds().getWidth() / 2;
        double h = lbg.getCoverShape().getBounds().getHeight();
        return -Math.atan2( w, h );
    }

    public double getTilt() {
        return tilt;
    }

    private void changeIntensity() {
        double power = Math.abs( component.getCurrent() * component.getVoltageDrop() );
//        System.out.println( "power = " + power );
        double maxPower = 60;
//        double maxPower = 20;
//        double maxPower=100;
        if( power > maxPower ) {
            power = maxPower;
        }
//        double intensity = power / maxPower;
        double intensity = Math.pow( power / maxPower, 0.354 );
//        System.out.println( "intensity = " + intensity );
        Rectangle r1 = getBoundsWithBrighties();
        lbg.setIntensity( intensity );
        Rectangle r2 = getBoundsWithBrighties();
        if( r1 != null && r2 != null ) {
            fastRepaint( parent, r1, r2 );
        }
        for( int i = 0; i < listeners.size(); i++ ) {
            IntensityChangeListener intensityChangeListener = (IntensityChangeListener)listeners.get( i );
            intensityChangeListener.intensityChanged( this, intensity );
        }
    }

    private static void fastRepaint( Component parent, Rectangle r1, Rectangle r2 ) {
        parent.repaint( r1.x, r1.y, r1.width, r1.height );
        parent.repaint( r2.x, r2.y, r2.width, r2.height );
    }

    private void updateTransform() {
        double sign = 1;
        if( !component.isConnectAtRight() ) {
            sign = -1;
        }
        this.affineTransform = createTransform( transform, component, WIDTH, HEIGHT, sign * tilt );
    }

    private static AffineTransform createTransform( ModelViewTransform2D transform, Bulb component, double width, double height, double theta ) {
        Point2D srcpt = transform.getAffineTransform().transform( component.getStartJunction().getPosition(), null );
        Point2D dstpt = transform.getAffineTransform().transform( component.getEndJunction().getPosition(), null );
        double newHeight = transform.modelToViewDifferentialY( component.getHeight() );
        double newLength = transform.modelToViewDifferentialX( component.getWidth() );
        double angle = new ImmutableVector2D.Double( srcpt, dstpt ).getAngle() - Math.PI / 2;

        angle += theta;
        AffineTransform trf = new AffineTransform();
        trf.rotate( angle, srcpt.getX(), srcpt.getY() );
        trf.translate( -newLength / 2, -newHeight * .93 );//TODO .93 is magick!
        trf.translate( srcpt.getX(), srcpt.getY() );
        trf.scale( newLength / width, newHeight / height );

        return trf;
    }

    private void changed() {
        Rectangle orig = getBoundsWithBrighties();
        updateTransform();
        highlightGraphic.setShape( getHighlightArea() );
        highlightGraphic.setVisible( component.isSelected() );
        fastRepaint( parent, orig, getBoundsWithBrighties() );
    }

    private Shape getHighlightArea() {
        Rectangle2D b = lbg.getBounds();
        b = RectangleUtils.expandRectangle2D( b, 5, 5 );
        Shape out = highlightStroke.createStrokedShape( b );
        Shape trf = affineTransform.createTransformedShape( out );
        return trf;
    }

    private Rectangle getBoundsWithBrighties() {
        if( lbg == null ) {
            return null;
        }
        Rectangle2D shape = lbg.getFullShape();
        shape = RectangleUtils.expandRectangle2D( shape, 2, 2 );
        return affineTransform.createTransformedShape( shape ).getBounds();
    }

    public Rectangle getBounds() {
        Rectangle2D expanded = RectangleUtils.expandRectangle2D( srcShape, 2, 2 );
        return affineTransform.createTransformedShape( expanded ).getBounds();
    }

    public ModelViewTransform2D getModelViewTransform2D() {
        return transform;
    }

    public CircuitComponent getCircuitComponent() {
        return component;
    }

    public void delete() {
        transform.removeTransformListener( transformListener );
        component.removeObserver( simpleObserver );
        module.getCircuitSolver().removeSolutionListener( circuitSolutionListener );
    }

    public void paint( Graphics2D g ) {
        if( affineTransform == null ) {
            return;
        }
        AffineTransform orig = g.getTransform();
        g.transform( affineTransform );
        lbg.paint( g );
        g.setTransform( orig );
        highlightGraphic.paint( g );
    }

    public boolean contains( int x, int y ) {
        return getBounds().contains( x, y );
    }

    protected Rectangle determineBounds() {
        return getBoundsWithBrighties();
    }

    public Shape getCoverShape() {
        return affineTransform.createTransformedShape( lbg.getCoverShape() );
    }

    interface IntensityChangeListener {
        public void intensityChanged( BulbComponentGraphic bulbComponentGraphic, double intensity );
    }

    public void addIntensityChangeListener( IntensityChangeListener icl ) {
        listeners.add( icl );
    }

    public void removeIntensityChangeListener( IntensityChangeListener intensityListener ) {
        listeners.remove( intensityListener );
    }
}
