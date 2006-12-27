/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.components;

import edu.colorado.phet.cck3.circuit.IComponentGraphic;
import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.fastpaint.FastPaintShapeGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common.view.util.DoubleGeneralPath;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: May 25, 2004
 * Time: 8:34:54 PM
 * Copyright (c) May 25, 2004 by Sam Reid
 */
public class SchematicResistorGraphic extends FastPaintShapeGraphic implements IComponentGraphic {
    private CircuitComponent component;
    private ModelViewTransform2D transform;
    private double wireThickness;
    private AbstractVector2D eastDir;
    private AbstractVector2D northDir;

    public SchematicResistorGraphic( Component parent, CircuitComponent component, ModelViewTransform2D transform, double wireThickness ) {
        super( new Area(), Color.black, parent );

        this.component = component;
        this.transform = transform;
        this.wireThickness = wireThickness;
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
        changed();
    }

    private AbstractVector2D getVector( double east, double north ) {
        AbstractVector2D e = eastDir.getScaledInstance( east );
        AbstractVector2D n = northDir.getScaledInstance( north );
        AbstractVector2D sum = e.getAddedInstance( n );
        return sum;
    }

    private void changed() {
        Point2D srcpt = transform.toAffineTransform().transform( component.getStartJunction().getPosition(), null );
        Point2D dstpt = transform.toAffineTransform().transform( component.getEndJunction().getPosition(), null );
        ImmutableVector2D vector = new ImmutableVector2D.Double( srcpt, dstpt );
        eastDir = vector.getInstanceOfMagnitude( 1 );
        northDir = eastDir.getNormalVector();
        double viewThickness = Math.abs( transform.modelToViewDifferentialY( wireThickness ) );
        double resistorWidth = srcpt.distance( dstpt );
        int numPeaks = 3;
        double zigHeight = viewThickness * 1.2;
        //zig zags go here.
        int numQuarters = ( numPeaks - 1 ) * 4 + 2;
        double numWaves = numQuarters / 4.0;
        double wavelength = resistorWidth / numWaves;
        double quarterWavelength = wavelength / 4.0;
        double halfWavelength = wavelength / 2.0;
        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( srcpt );
        path.lineToRelative( getVector( quarterWavelength, zigHeight ) );
        for( int i = 0; i < numPeaks - 1; i++ ) {
            path.lineToRelative( getVector( halfWavelength, -2 * zigHeight ) );
            path.lineToRelative( getVector( halfWavelength, 2 * zigHeight ) );
        }
        path.lineToRelative( getVector( quarterWavelength, -zigHeight ) );
        path.lineTo( dstpt );
        Shape shape = path.getGeneralPath();
        BasicStroke stroke = new BasicStroke( (float)viewThickness );
        super.setShape( stroke.createStrokedShape( shape ) );

//        double fracDistToCathode = .35;
//        double fracDistToAnode = ( 1 - fracDistToCathode );
//        ImmutableVector2D vector = new ImmutableVector2D.Double( srcpt, dstpt );
//        Point2D cat = vector.getScaledInstance( fracDistToCathode ).getDestination( srcpt );
//        Point2D ano = vector.getScaledInstance( fracDistToAnode ).getDestination( srcpt );
//        AbstractVector2D eastDir = vector.getInstanceOfMagnitude( 1 );
//        AbstractVector2D northDir = eastDir.getNormalVector();
//        double catHeight = viewThickness * 3;
//        double anoHeight = viewThickness * 1.75;
//        Point2D catHat = northDir.getInstanceOfMagnitude( catHeight ).getDestination( cat );
//        Point2D cattail = northDir.getInstanceOfMagnitude( catHeight ).getScaledInstance( -1 ).getDestination( cat );
//
//        Point2D anoHat = northDir.getInstanceOfMagnitude( anoHeight ).getDestination( ano );
//        Point2D anotail = northDir.getInstanceOfMagnitude( anoHeight ).getScaledInstance( -1 ).getDestination( ano );
//
//        Area area = new Area();
//        area.add( new Area( LineSegment.getSegment( srcpt, cat, viewThickness ) ) );
//        area.add( new Area( LineSegment.getSegment( ano, dstpt, viewThickness ) ) );
//        area.add( new Area( LineSegment.getSegment( catHat, cattail, viewThickness ) ) );
//        area.add( new Area( LineSegment.getSegment( anoHat, anotail, viewThickness ) ) );
//        super.setShape( area );
    }

    public ModelViewTransform2D getModelViewTransform2D() {
        return transform;
    }

    public CircuitComponent getComponent() {
        return component;
    }
}
