/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.components;

import edu.colorado.phet.cck3.CCK3Module;
import edu.colorado.phet.cck3.circuit.IComponentGraphic;
import edu.colorado.phet.cck3.circuit.kirkhoff.KirkhoffSolutionListener;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common.view.util.GraphicsUtil;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: May 25, 2004
 * Time: 8:34:54 PM
 * Copyright (c) May 25, 2004 by Sam Reid
 */
public class BulbComponentGraphic implements IComponentGraphic {
    private Bulb component;
    private ModelViewTransform2D transform;
    private CCK3Module module;
    private Component parent;
    private static final double WIDTH = 100;
    private static final double HEIGHT = 100;
    private AffineTransform affineTransform;

    LightBulbGraphic lbg;
    private Rectangle2D.Double srcShape;

    public BulbComponentGraphic( Component parent, Bulb component, ModelViewTransform2D transform, CCK3Module module ) {
        this.parent = parent;
        this.component = component;
        this.transform = transform;
        this.module = module;
        component.addObserver( new SimpleObserver() {
            public void update() {
                changed();
            }
        } );

        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2D mvt ) {
                changed();
            }
        } );
        module.getKirkhoffSolver().addSolutionListener( new KirkhoffSolutionListener() {
            public void finishedKirkhoff() {
                changeIntensity();
            }
        } );
        srcShape = new Rectangle2D.Double( 0, 0, WIDTH, HEIGHT );
        lbg = new LightBulbGraphic( srcShape );
        lbg.setIntensity( 0 );
        updateTransform();
    }

    private void changeIntensity() {
        double power = Math.abs( component.getCurrent() * component.getVoltageDrop() );
//        System.out.println( "power = " + power );
        double maxPower = 35;
        if( power > maxPower ) {
            power = maxPower;
        }
        double intensity = power / maxPower;
        Rectangle r1 = getBoundsWithBrighties();
        lbg.setIntensity( intensity );
        Rectangle r2 = getBoundsWithBrighties();
        if( r1 != null && r2 != null ) {
            GraphicsUtil.fastRepaint( parent, r1, r2 );
        }
    }

    private void updateTransform() {
        this.affineTransform = createTransform( transform, component, WIDTH, HEIGHT );
    }

    public static AffineTransform createTransform( ModelViewTransform2D transform, Bulb component, double getTargetWidth, double getTargetHeight ) {
        Point2D srcpt = transform.toAffineTransform().transform( component.getStartJunction().getPosition(), null );
        Point2D dstpt = transform.toAffineTransform().transform( component.getEndJunction().getPosition(), null );
//        double dist = srcpt.distance( dstpt );
//        System.out.println( "dist = " + dist );
        double newHeight = transform.modelToViewDifferentialY( component.getHeight() );

//        double newLength = transform.modelToViewDifferentialX( component.getLength() );
//        double newLength = dist;
        double newLength = transform.modelToViewDifferentialX( component.getWidth() );
        double angle = new ImmutableVector2D.Double( srcpt, dstpt ).getAngle() - Math.PI / 2;
        AffineTransform trf = new AffineTransform();
        trf.rotate( angle, srcpt.getX(), srcpt.getY() );
//        trf.translate( -newLength / 2, -newHeight / 2 );
        trf.translate( -newLength / 2, -newHeight * .93 );//TODO .93 is magick!
        trf.translate( srcpt.getX(), srcpt.getY() );
        trf.scale( newLength / getTargetWidth, newHeight / getTargetHeight );

        return trf;
    }

    private void changed() {
        GraphicsUtil.fastRepaint( parent, getBoundsWithBrighties() );
        updateTransform();
        GraphicsUtil.fastRepaint( parent, getBoundsWithBrighties() );
    }

    private Rectangle getBoundsWithBrighties() {
        Rectangle2D shape = lbg.getFullShape();
        shape = expand( shape, 2, 2 );
        return affineTransform.createTransformedShape( shape ).getBounds();
    }

    private static Rectangle2D expand( Rectangle2D rect, double insetX, double insetY ) {
        Rectangle2D.Double expanded = new Rectangle2D.Double( rect.getX() - insetX, rect.getY() - insetY, rect.getWidth() + insetX * 2, rect.getHeight() + insetY * 2 );
        return expanded;
    }

    private Rectangle getBounds() {
        double inset = 2;
        Rectangle2D.Double expanded = new Rectangle2D.Double( srcShape.getX() - inset, srcShape.getY() - inset, srcShape.getWidth() + inset * 2, srcShape.getHeight() + inset * 2 );
        return affineTransform.createTransformedShape( expanded ).getBounds();
    }

    public ModelViewTransform2D getModelViewTransform2D() {
        return transform;
    }

    public CircuitComponent getComponent() {
        return component;
    }

    public void paint( Graphics2D g ) {
        if( affineTransform == null ) {
            return;
        }
        AffineTransform orig = g.getTransform();
        g.transform( affineTransform );
        lbg.paint( g );

        g.setTransform( orig );
    }

    public boolean contains( int x, int y ) {
        return getBounds().contains( x, y );
    }

    public Shape getCoverShape() {
        return affineTransform.createTransformedShape( lbg.getCoverShape() );
    }
}
