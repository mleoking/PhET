/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.components;

import edu.colorado.phet.cck3.CCK3Module;
import edu.colorado.phet.cck3.circuit.IComponentGraphic;
import edu.colorado.phet.cck3.circuit.kirkhoff.KirkhoffSolutionListener;
import edu.colorado.phet.cck3.common.LineSegment;
import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.fastpaint.FastPaint;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: May 29, 2004
 * Time: 12:57:37 AM
 * Copyright (c) May 29, 2004 by Sam Reid
 */
public class SeriesAmmeterGraphic implements IComponentGraphic {
    private Component parent;
    private SeriesAmmeter component;
    private ModelViewTransform2D transform;
    private CCK3Module module;
    private AffineTransform affineTx;

    private Stroke stroke = new BasicStroke( 5 );
    private Font font = new Font( "Lucida Sans", Font.BOLD, 17 );
    private Shape shape;
    private String text = "Ammeter";
    private String fixedMessage;
    private SimpleObserver simpleObserver;
    private TransformListener transformListener;
    private KirkhoffSolutionListener kirkhoffSolutionListener;

    public SeriesAmmeterGraphic( Component parent, final SeriesAmmeter component, ModelViewTransform2D transform, CCK3Module module, String fixedMessage ) {
        this( parent, component, transform, module );
        this.fixedMessage = fixedMessage;
    }

    public SeriesAmmeterGraphic( Component parent, final SeriesAmmeter component, ModelViewTransform2D transform, final CCK3Module module ) {
        this.parent = parent;
        this.component = component;
        this.transform = transform;
        this.module = module;

        doupdate();

        simpleObserver = new SimpleObserver() {
            public void update() {
                doupdate();
            }
        };
        component.addObserver( simpleObserver );
        transformListener = new TransformListener() {
            public void transformChanged( ModelViewTransform2D mvt ) {
                doupdate();
            }
        };
        transform.addTransformListener( transformListener );
        kirkhoffSolutionListener = new KirkhoffSolutionListener() {
            public void finishedKirkhoff() {
                DecimalFormat df = module.getDecimalFormat(); 
//                        new DecimalFormat( "#0.0#" );
                String form = df.format( Math.abs( component.getCurrent() ) );
                text = "" + form + " Amps";
                doupdate();
            }
        };
        module.getKirkhoffSolver().addSolutionListener( kirkhoffSolutionListener );
    }

    private void updateShape() {
        double newHeight = transform.modelToViewDifferentialY( component.getHeight() );
        Point2D start = transform.modelToView( component.getStartJunction().getPosition() );
        Point2D end = transform.modelToView( component.getEndJunction().getPosition() );

        this.shape = LineSegment.getSegment( start, end, newHeight );
    }

    private void doupdate() {
        Rectangle r1 = expand( getBounds() );
        updateShape();
        Rectangle r2 = expand( getBounds() );
        if( r1 != null && r2 != null ) {
            FastPaint.fastRepaint( parent, r1, r2 );
        }
        else if( r2 != null ) {
            FastPaint.fastRepaint( parent, r2 );
        }
    }

    private Rectangle expand( Rectangle bounds ) {
        if( bounds == null ) {
            return null;
        }
        int inset = 6;
        return new Rectangle( bounds.x - inset, bounds.y - inset, bounds.width + inset * 2, bounds.height + inset * 2 );
    }

    private Rectangle getBounds() {
        if( shape == null ) {
            return null;
        }
        return shape.getBounds();
    }

    public ModelViewTransform2D getModelViewTransform2D() {
        return transform;
    }

    public CircuitComponent getCircuitComponent() {
        return component;
    }

    public void delete() {
        component.removeObserver( simpleObserver );
        transform.removeTransformListener( transformListener );
        module.getKirkhoffSolver().removeSolutionListener( kirkhoffSolutionListener );
    }

    public boolean contains( int x, int y ) {
        return shape != null && shape.contains( x, y );
    }

    public void paint( Graphics2D g ) {
        Point2D start = transform.modelToView( component.getStartJunction().getPosition() );
        Point2D end = transform.modelToView( component.getEndJunction().getPosition() );
        Vector2D dir = new Vector2D.Double( start, end ).normalize();
        AbstractVector2D north = dir.getNormalVector();

        double angle = new Vector2D.Double( start, end ).getAngle();
        Rectangle r = shape.getBounds();

        g.setColor( Color.black );
        g.setStroke( stroke );
        g.draw( shape );

        //TODO this is slow, and not well written.
        Area area = new Area( shape );
        int numWindows = 3;
        double windowHeightFraction = .3;
        int windowHeight = transform.modelToViewDifferentialY( component.getHeight() * windowHeightFraction );
        double length = start.distance( end );
        double windowWidth = length / ( numWindows + 1.0 );
        double spacingWidth = ( length - windowWidth * numWindows ) / ( numWindows + 1 );
        double x = 0;
        north = north.getInstanceOfMagnitude( windowHeight / 2 ).getScaledInstance( -1 );
        ArrayList windows = new ArrayList();
        for( int i = 0; i < numWindows; i++ ) {
            x += spacingWidth;
            Point2D a = dir.getInstanceOfMagnitude( x ).getDestination( start );
            a = north.getDestination( a );
            x += windowWidth;
            Point2D b = dir.getInstanceOfMagnitude( x ).getDestination( start );
            b = north.getDestination( b );
            Shape seg = LineSegment.getSegment( a, b, windowHeight );
            windows.add( seg );
            area.subtract( new Area( seg ) );
        }
        g.setStroke( new BasicStroke( 1 ) );
        g.setColor( Color.black );
        for( int i = 0; i < windows.size(); i++ ) {
            Shape windowShape = (Shape)windows.get( i );
            g.draw( windowShape );
        }

        Point a = r.getLocation();
        Point b = new Point( (int)( a.getX() + r.getWidth() ), (int)( a.getY() + r.getHeight() ) );
        Color startColor = new Color( 255, 230, 250 );
        Color endColor = new Color( 230, 255, 230 );
        g.setPaint( new GradientPaint( a, startColor, b, endColor ) );
        g.fill( area );

        g.setColor( Color.black );

//        Point2D textLoc = north.getScaledInstance( -2.5 ).getDestination( start );
        Point2D textLoc = north.getScaledInstance( -2.9 ).getDestination( start );
        textLoc = dir.getInstanceOfMagnitude( 2 ).getDestination( textLoc );

        g.rotate( angle, textLoc.getX(), textLoc.getY() );
        g.setFont( font );
        String msg = text;
        if( fixedMessage != null ) {
            msg = fixedMessage;
        }
        g.drawString( msg, (float)textLoc.getX(), (float)textLoc.getY() );
        g.rotate( -angle, textLoc.getX(), textLoc.getY() );
    }
}
