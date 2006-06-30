/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.components;

import edu.colorado.phet.cck3.circuit.Branch;
import edu.colorado.phet.cck3.circuit.CurrentVoltListener;
import edu.colorado.phet.cck3.circuit.IComponentGraphic;
import edu.colorado.phet.cck3.common.LineSegment;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.colorado.phet.common_cck.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common_cck.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common_cck.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common_cck.view.phetgraphics.PhetTextGraphic;

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
public class SchematicAmmeterGraphic extends PhetShapeGraphic implements IComponentGraphic {
    private CircuitComponent component;
    private ModelViewTransform2D transform;
    private double wireThickness;
    private DecimalFormat decimalFormat;
    private SimpleObserver simpleObserver;
    private TransformListener transformListener;
    private PhetTextGraphic textGraphic;
    private PhetShapeGraphic highlightRegion;

    public SchematicAmmeterGraphic( Component parent, CircuitComponent component, ModelViewTransform2D transform, double wireThickness, DecimalFormat decimalFormat ) {
        super( parent, new Area(), Color.black );
        highlightRegion = new PhetShapeGraphic( parent, new Area(), Color.yellow );
        this.component = component;
        this.transform = transform;
        this.wireThickness = wireThickness;
        this.decimalFormat = decimalFormat;
        this.textGraphic = new PhetTextGraphic( parent, new Font( "Lucida Sans", 0, 22 ), "", Color.black, 0, 0 );
        textGraphic.setColor( Color.blue );
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

        component.addCurrentVoltListener( new CurrentVoltListener() {
            public void currentOrVoltageChanged( Branch branch ) {
                changed();
            }
        } );
        changed();
        setVisible( true );
    }

    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        textGraphic.setVisible( visible );
    }

    private void changed() {
        Point2D srcpt = transform.getAffineTransform().transform( component.getStartJunction().getPosition(), null );
        Point2D dstpt = transform.getAffineTransform().transform( component.getEndJunction().getPosition(), null );
        double viewThickness = Math.abs( transform.modelToViewDifferentialY( wireThickness ) );
        Shape shape = LineSegment.getSegment( srcpt, dstpt, viewThickness );
        super.setShape( shape );
        String text = decimalFormat.format( Math.abs( component.getCurrent() ) ) + " Amps";
        Rectangle2D bounds2d = shape.getBounds2D();
        textGraphic.setPosition( (int)( bounds2d.getX() + bounds2d.getWidth() / 2 ), (int)( bounds2d.getY() - 5 ) );
        textGraphic.setText( text );
        highlightRegion.setShape( LineSegment.getSegment( srcpt, dstpt, viewThickness * 1.63 ) );
        highlightRegion.setVisible( component.isSelected() );
    }

    public void paint( Graphics2D g ) {
        highlightRegion.paint( g );
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
