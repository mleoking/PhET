package edu.colorado.phet.cck.piccolo_cck.schematic;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.common.LineSegment;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.CircuitComponent;
import edu.colorado.phet.cck.piccolo_cck.ComponentNode;
import edu.colorado.phet.cck.piccolo_cck.PhetPPath;
import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.colorado.phet.common_cck.view.util.DoubleGeneralPath;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: May 25, 2004
 * Time: 8:34:54 PM
 * Copyright (c) May 25, 2004 by Sam Reid
 */
public class SchematicOscillateNode extends ComponentNode {
    private CircuitComponent component;
    private double wireThickness;
    private AbstractVector2D eastDir;
    private AbstractVector2D northDir;
    private Point2D anoPoint;
    private Point2D catPoint;
    private Area mouseArea;
    private SimpleObserver simpleObserver;
    private PhetPPath path;
    private static final double SCALE = 1.0 / 60.0;

    public SchematicOscillateNode( CCKModel parent, CircuitComponent circuitComponent, JComponent jComponent, ICCKModule module, double wireThickness ) {
        super( parent, circuitComponent, jComponent );
        this.component = circuitComponent;
        this.wireThickness = wireThickness;
        simpleObserver = new SimpleObserver() {
            public void update() {
                changed();
            }
        };
        path = new PhetPPath( Color.black );
        addChild( path );
        component.addObserver( simpleObserver );
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
        super.update();
        Point2D srcpt = component.getStartJunction().getPosition();
        Point2D dstpt = component.getEndJunction().getPosition();
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
        double length = catPoint.distance( anoPoint );
        double dx = 1 * SCALE;
        double fracDistToStartSine = getFracDistToStartSine();
        double sinDist = length - 2 * length * fracDistToStartSine;
//        System.out.println( "sinDist = " + sinDist );
        double omega = 2 * Math.PI / ( sinDist );
        for( double x = 0; x < length; x += dx ) {
            double y = getY( x, length, fracDistToStartSine, omega );
            AbstractVector2D v = getVector( x, y );
            Point2D pt = v.getDestination( catPoint );
            if( x > length * fracDistToStartSine && x < ( length - length * fracDistToStartSine ) ) {
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
        this.path.setPathTo( area );

        getHighlightNode().setStroke( new BasicStroke( 0.1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f ) );
        getHighlightNode().setPathTo( area );
    }

    protected double getFracDistToStartSine() {
        return 0.15;
    }

    protected double getViewThickness() {
        return wireThickness;
    }

    protected double getY( double x, double dist, double fracDistToStartSine, double omega ) {
        return -10 * Math.sin( ( x - dist * fracDistToStartSine ) * omega ) * SCALE;
    }

    protected JPopupMenu createPopupMenu() {
        return null;
    }

    public CircuitComponent getCircuitComponent() {
        return component;
    }

    public void delete() {
        component.removeObserver( simpleObserver );
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
