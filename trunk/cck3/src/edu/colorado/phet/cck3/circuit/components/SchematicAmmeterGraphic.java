/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.components;

import edu.colorado.phet.cck3.circuit.Branch;
import edu.colorado.phet.cck3.circuit.CurrentVoltListener;
import edu.colorado.phet.cck3.circuit.IComponentGraphic;
import edu.colorado.phet.cck3.common.LineSegment;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.fastpaint.FastPaintShapeGraphic;
import edu.colorado.phet.common.view.fastpaint.FastPaintTextGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: May 25, 2004
 * Time: 8:34:54 PM
 * Copyright (c) May 25, 2004 by Sam Reid
 */
public class SchematicAmmeterGraphic extends FastPaintShapeGraphic implements IComponentGraphic {
    private CircuitComponent component;
    private ModelViewTransform2D transform;
    private double wireThickness;
    DecimalFormat format = new DecimalFormat( "#0.0#" );
    FastPaintTextGraphic textGraphic;
    private SimpleObserver simpleObserver;
    private TransformListener transformListener;

    public SchematicAmmeterGraphic( Component parent, CircuitComponent component, ModelViewTransform2D transform, double wireThickness ) {
        super( new Area(), Color.black, parent );

        this.component = component;
        this.transform = transform;
        this.wireThickness = wireThickness;
        this.textGraphic = new FastPaintTextGraphic( "", new Font( "Lucida Sans", 0, 22 ), 0, 0, parent );
        textGraphic.setPaint( Color.blue );
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
        changed();
        component.addCurrentVoltListener( new CurrentVoltListener() {
            public void currentOrVoltageChanged( Branch branch ) {
                changed();
            }
        } );
    }

    private void changed() {
        Point2D srcpt = transform.toAffineTransform().transform( component.getStartJunction().getPosition(), null );
        Point2D dstpt = transform.toAffineTransform().transform( component.getEndJunction().getPosition(), null );
//        ImmutableVector2D vector = new ImmutableVector2D.Double( srcpt, dstpt );

        double viewThickness = Math.abs( transform.modelToViewDifferentialY( wireThickness ) );
        Shape shape = LineSegment.getSegment( srcpt, dstpt, viewThickness );
        super.setShape( shape );
        String text = format.format( Math.abs( component.getCurrent() ) ) + " Amps";
        Rectangle2D bounds2d = shape.getBounds2D();
        textGraphic.setLocation( (float)( bounds2d.getX() + bounds2d.getWidth() / 2 ), (float)( bounds2d.getY() - 5 ) );
        textGraphic.setText( text );
    }

    public void paint( Graphics2D g ) {
        super.paint( g );
        textGraphic.paint( g );
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
    }
}
