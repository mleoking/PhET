/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck3.phetgraphics_cck.circuit.components;

import edu.colorado.phet.cck3.common.LineSegment;
import edu.colorado.phet.cck3.model.components.CircuitComponent;
import edu.colorado.phet.cck3.phetgraphics_cck.circuit.IComponentGraphic;
import edu.colorado.phet.common_cck.math.AbstractVector2D;
import edu.colorado.phet.common_cck.math.ImmutableVector2D;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.colorado.phet.common_cck.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common_cck.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common_cck.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common_cck.view.util.DoubleGeneralPath;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: May 25, 2004
 * Time: 8:34:54 PM
 * Copyright (c) May 25, 2004 by Sam Reid
 */
public class SchematicOscillateGraphic extends PhetShapeGraphic implements IComponentGraphic {
    private CircuitComponent component;
    private ModelViewTransform2D transform;
    private double wireThickness;
    private AbstractVector2D eastDir;
    private AbstractVector2D northDir;
    private Point2D anoPoint;
    private Point2D catPoint;
    private Area mouseArea;
    private SimpleObserver simpleObserver;
    private TransformListener transformListener;
    private PhetShapeGraphic highlightRegion;

    public SchematicOscillateGraphic( Component parent, CircuitComponent component, ModelViewTransform2D transform, double wireThickness ) {
        super( parent, new Area(), Color.black );
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

    public void setVisible( boolean visible ) {
        super.setVisible( visible );
    }

    private AbstractVector2D getVector( double east, double north ) {
        AbstractVector2D e = eastDir.getScaledInstance( east );
        AbstractVector2D n = northDir.getScaledInstance( north );
        return e.getAddedInstance( n );
    }

    protected void changed() {
        Point2D srcpt = transform.getAffineTransform().transform( component.getStartJunction().getPosition(), null );
        Point2D dstpt = transform.getAffineTransform().transform( component.getEndJunction().getPosition(), null );
        ImmutableVector2D vector = new ImmutableVector2D.Double( srcpt, dstpt );
        double fracDistToCathode = .1;
        double fracDistToAnode = ( 1 - fracDistToCathode );
        catPoint = vector.getScaledInstance( fracDistToCathode ).getDestination( srcpt );
        anoPoint = vector.getScaledInstance( fracDistToAnode ).getDestination( srcpt );

        eastDir = vector.getInstanceOfMagnitude( 1 );
        northDir = eastDir.getNormalVector();
        double viewThickness = getViewThickness();
        double resistorThickness = viewThickness / 2.5;
        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( catPoint );
        double dist = catPoint.distance( anoPoint );
        double dx = 1;
        double fracDistToStartSine = getFracDistToStartSine();
        double sinDist = dist - 2 * dist * fracDistToStartSine;
//        System.out.println( "sinDist = " + sinDist );
        double omega = 2 * Math.PI / ( sinDist );
        for( double x = 0; x < dist; x += dx ) {
            double y = getY( x, dist, fracDistToStartSine, omega );
            AbstractVector2D v = getVector( x, y );
            Point2D pt = v.getDestination( catPoint );
            if( x > dist * fracDistToStartSine && x < ( dist - dist * fracDistToStartSine ) ) {
                path.lineTo( pt );
            }
            else {
                path.moveTo( pt );
            }
        }

        Shape shape = path.getGeneralPath();
        BasicStroke stroke = new BasicStroke( (float)resistorThickness );

        Shape sha = stroke.createStrokedShape( shape );
        Area area = new Area( sha );
        area.add( new Area( LineSegment.getSegment( srcpt, catPoint, viewThickness ) ) );
        area.add( new Area( LineSegment.getSegment( anoPoint, dstpt, viewThickness ) ) );
        mouseArea = new Area( area );
        mouseArea.add( new Area( LineSegment.getSegment( srcpt, dstpt, viewThickness ) ) );
        super.setShape( area );

        Stroke highlightStroke = new BasicStroke( 6 );
        highlightRegion.setShape( highlightStroke.createStrokedShape( area ) );
        highlightRegion.setVisible( component.isSelected() );
    }

    protected double getFracDistToStartSine() {
        return 0.15;
    }

    protected double getViewThickness() {
        return (double)Math.abs( transform.modelToViewDifferentialY( wireThickness ) );
    }

    protected double getY( double x, double dist, double fracDistToStartSine, double omega ) {
        return -10 * Math.sin( ( x - dist * fracDistToStartSine ) * omega );
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

    protected Point2D getAnoPoint() {
        return anoPoint;
    }

    protected Point2D getCatPoint() {
        return catPoint;
    }

    public boolean contains( int x, int y ) {
        return mouseArea.contains( x, y );
    }
}
