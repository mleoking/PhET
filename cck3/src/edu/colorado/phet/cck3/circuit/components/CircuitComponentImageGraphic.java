/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.components;

import edu.colorado.phet.cck3.circuit.CircuitGraphic;
import edu.colorado.phet.cck3.circuit.IComponentGraphic;
import edu.colorado.phet.cck3.common.CCKCompositePhetGraphic;
import edu.colorado.phet.cck3.model.components.Battery;
import edu.colorado.phet.cck3.model.components.CircuitComponent;
import edu.colorado.phet.common_cck.math.ImmutableVector2D;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.colorado.phet.common_cck.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common_cck.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common_cck.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common_cck.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common_cck.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common_cck.view.util.RectangleUtils;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: May 25, 2004
 * Time: 8:34:54 PM
 * Copyright (c) May 25, 2004 by Sam Reid
 */
public class CircuitComponentImageGraphic extends CCKCompositePhetGraphic implements IComponentGraphic {
    private CircuitComponent component;
    private ModelViewTransform2D transform;
    private Rectangle2D.Double src;
    private SimpleObserver simpleObserver;
    private TransformListener transformListener;
    private PhetImageGraphic imageGraphic;
    private PhetShapeGraphic highlightGraphic;
    private boolean debug = false;
    private PhetTextGraphic debugText;

    public CircuitComponentImageGraphic( BufferedImage image, Component parent, CircuitComponent component, ModelViewTransform2D transform ) {
        super( parent );
        imageGraphic = new PhetImageGraphic( parent, image );
        highlightGraphic = new PhetShapeGraphic( parent, new Area(), Color.yellow );
        addGraphic( highlightGraphic );
        addGraphic( imageGraphic );

        this.component = component;
        this.transform = transform;
        simpleObserver = new SimpleObserver() {
            public void update() {
                changed();
            }
        };
        component.addObserver( simpleObserver );
        src = new Rectangle2D.Double( 0, 0, image.getWidth(), image.getHeight() );
        transformListener = new TransformListener() {
            public void transformChanged( ModelViewTransform2D mvt ) {
                changed();
            }
        };
        if( CircuitGraphic.GRAPHICAL_DEBUG ) {
            debugText = new PhetTextGraphic( getComponent(), new Font( "dialog", 0, 12 ), "", Color.black, 0, 0 );
            addGraphic( debugText );
        }
        transform.addTransformListener( transformListener );
        changed();
        setVisible( true );
    }

    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        if( highlightGraphic != null ) {
            highlightGraphic.setVisible( visible && component.isSelected() );
        }
    }

    private void changed() {
        AffineTransform at = createTransform();
        imageGraphic.setTransform( at );
        Shape shape = imageGraphic.getShape();
        shape = new BasicStroke( 6 ).createStrokedShape( shape );
        highlightGraphic.setShape( shape );
        highlightGraphic.setVisible( component.isSelected() );
        String text = "r=" + component.getResistance();
        if( component instanceof Battery ) {
            Battery batt = (Battery)component;
            text += " ir= " + batt.getInteralResistance();
        }
        if( debugText != null ) {
            debugText.setText( text );
            Point ctr = RectangleUtils.getCenter( highlightGraphic.getBounds() );
            debugText.setPosition( ctr.x, ctr.y );
        }

        super.setBoundsDirty();
    }

    private AffineTransform createTransform() {
        Point2D srcpt = transform.getAffineTransform().transform( component.getStartJunction().getPosition(), null );
        Point2D dstpt = transform.getAffineTransform().transform( component.getEndJunction().getPosition(), null );
        double dist = component.getStartJunction().getPosition().distance( component.getEndJunction().getPosition() );
        double length = component.getLength();
        double diff = Math.abs( length - dist );

        if( diff > Double.parseDouble( "1E-10" ) ) {
            throw new RuntimeException( "Components moved to a weird place, Dist between junctions=" + dist + ", Requested Length=" + length + ", diff=" + diff );
        }

        double newHeight = transform.modelToViewDifferentialY( component.getHeight() );
        AffineTransform at = createTransform( imageGraphic.getImage().getWidth(), imageGraphic.getImage().getHeight(),
                                              srcpt, dstpt, newHeight );
        return at;
    }

    public static AffineTransform createTransform( int initWidth, int initHeight,
                                                   Point2D src, Point2D dst, double newHeight ) {
        double dist = src.distance( dst );
        double newLength = dist;
        double angle = new ImmutableVector2D.Double( src, dst ).getAngle();
        //        System.out.println( "angle = " + angle );
        AffineTransform trf = new AffineTransform();
        trf.rotate( angle, src.getX(), src.getY() );
        trf.translate( 0, -newHeight / 2 );
        trf.translate( src.getX(), src.getY() );
        //        System.out.println( "newLength = " + newLength + ", newHeight=" + newHeight );
        if( newLength == 0 || newHeight == 0 ) {
            throw new RuntimeException( "Length or height is zero." );
        }
        trf.scale( newLength / initWidth, newHeight / initHeight );
        return trf;
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
    }

    public AffineTransform getTransform() {
        return imageGraphic.getTransform();
    }

}
