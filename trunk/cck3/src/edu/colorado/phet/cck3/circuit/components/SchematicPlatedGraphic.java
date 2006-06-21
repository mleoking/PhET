/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck3.circuit.components;

import edu.colorado.phet.cck3.circuit.IComponentGraphic;
import edu.colorado.phet.cck3.common.LineSegment;
import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: May 25, 2004
 * Time: 8:34:54 PM
 * Copyright (c) May 25, 2004 by Sam Reid
 */
public class SchematicPlatedGraphic extends PhetShapeGraphic implements IComponentGraphic {
    private CircuitComponent component;
    private ModelViewTransform2D transform;
    private double wireThickness;
    private Area mouseArea;
    private SimpleObserver simpleObserver;
    private TransformListener transformListener;
    private PhetShapeGraphic highlightRegion;
    private double fracDistToPlate;
    private double scaleHeightLeft;
    private double scaleHeightRight;

    public SchematicPlatedGraphic( Component parent, CircuitComponent component,
                                   ModelViewTransform2D transform, double wireThickness, double fracDistToPlate,
                                   double scaleHeightLeft, double scaleHeightRight ) {
        super( parent, new Area(), Color.black );
        this.fracDistToPlate = fracDistToPlate;
        this.scaleHeightLeft = scaleHeightLeft;
        this.scaleHeightRight = scaleHeightRight;
        highlightRegion = new PhetShapeGraphic( parent, new Area(), Color.yellow );
        this.component = component;
        this.transform = transform;
        this.wireThickness = wireThickness;
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
        setVisible( true );
    }


    private void changed() {
        Point2D srcpt = transform.getAffineTransform().transform( component.getStartJunction().getPosition(), null );
        Point2D dstpt = transform.getAffineTransform().transform( component.getEndJunction().getPosition(), null );
        double viewThickness = transform.modelToViewDifferentialY( wireThickness );

        double fracDistToCathode = this.fracDistToPlate;
        double fracDistToAnode = ( 1 - fracDistToCathode );
        ImmutableVector2D vector = new ImmutableVector2D.Double( srcpt, dstpt );
        Point2D cat = vector.getScaledInstance( fracDistToCathode ).getDestination( srcpt );
        Point2D ano = vector.getScaledInstance( fracDistToAnode ).getDestination( srcpt );
        AbstractVector2D east = vector.getInstanceOfMagnitude( 1 );
        AbstractVector2D north = east.getNormalVector();
        double catHeight = viewThickness * this.scaleHeightLeft;
        double anoHeight = viewThickness * this.scaleHeightRight;
        Point2D catHat = north.getInstanceOfMagnitude( catHeight ).getDestination( cat );
        Point2D cattail = north.getInstanceOfMagnitude( catHeight ).getScaledInstance( -1 ).getDestination( cat );

        Point2D anoHat = north.getInstanceOfMagnitude( anoHeight ).getDestination( ano );
        Point2D anotail = north.getInstanceOfMagnitude( anoHeight ).getScaledInstance( -1 ).getDestination( ano );

        double battThickness = viewThickness / 2;
        Area area = new Area();
        area.add( new Area( LineSegment.getSegment( srcpt, cat, viewThickness ) ) );
        area.add( new Area( LineSegment.getSegment( ano, dstpt, viewThickness ) ) );
        area.add( new Area( LineSegment.getSegment( catHat, cattail, battThickness ) ) );
        area.add( new Area( LineSegment.getSegment( anoHat, anotail, battThickness ) ) );
        mouseArea = new Area( area );
        mouseArea.add( new Area( LineSegment.getSegment( srcpt, dstpt, viewThickness ) ) );
        super.setShape( area );
        Stroke highlightStroke = new BasicStroke( 6 );
        highlightRegion.setShape( highlightStroke.createStrokedShape( area ) );
        highlightRegion.setVisible( component.isSelected() );
    }

    public void paint( Graphics2D g ) {
        highlightRegion.paint( g );
        super.paint( g );
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

    public boolean contains( int x, int y ) {
        return mouseArea.contains( x, y );
    }
}
