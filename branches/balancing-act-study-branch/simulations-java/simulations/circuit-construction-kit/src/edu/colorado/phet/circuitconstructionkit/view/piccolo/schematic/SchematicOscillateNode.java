// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.circuitconstructionkit.view.piccolo.schematic;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Point2D;

import javax.swing.JComponent;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.components.CircuitComponent;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.ComponentNode;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.LineSegment;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;

/**
 * User: Sam Reid
 * Date: May 25, 2004
 * Time: 8:34:54 PM
 */
public class SchematicOscillateNode extends ComponentNode {
    private CircuitComponent component;
    private double wireThickness;
    private Vector2D eastDir;
    private Vector2D northDir;
    private Point2D anoPoint;
    private Point2D catPoint;
    private SimpleObserver simpleObserver;
    private PhetPPath path;
    private static final double SCALE = 1.0 / 60.0;

    public SchematicOscillateNode( CCKModel parent, CircuitComponent circuitComponent, JComponent jComponent, CCKModule module, double wireThickness ) {
        super( parent, circuitComponent, jComponent, module );
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

    private Vector2D getVector( double east, double north ) {
        Vector2D e = eastDir.times( east );
        Vector2D n = northDir.times( north );
        return e.plus( n );
    }

    protected void changed() {
        super.update();
        Point2D srcpt = component.getStartJunction().getPosition();
        Point2D dstpt = component.getEndJunction().getPosition();
        Vector2D vector = new Vector2D( srcpt, dstpt );
        double fracDistToCathode = .1;
        double fracDistToAnode = ( 1 - fracDistToCathode );
        catPoint = vector.times( fracDistToCathode ).getDestination( srcpt );
        anoPoint = vector.times( fracDistToAnode ).getDestination( srcpt );

        eastDir = vector.getInstanceOfMagnitude( 1 );
        northDir = eastDir.getPerpendicularVector();
        double viewThickness = getViewThickness();
        double resistorThickness = viewThickness / 2.5;
        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( catPoint );
        double length = catPoint.distance( anoPoint );
        double dx = 1 * SCALE;
        double fracDistToStartSine = getFracDistToStartSine();
        double sinDist = length - 2 * length * fracDistToStartSine;
        double omega = 2 * Math.PI / ( sinDist );
        for ( double x = 0; x < length; x += dx ) {
            double y = getY( x, length, fracDistToStartSine, omega );
            Vector2D v = getVector( x, y );
            Point2D pt = v.getDestination( catPoint );
            if ( x > length * fracDistToStartSine && x < ( length - length * fracDistToStartSine ) ) {
                path.lineTo( pt );
            }
            else {
                path.moveTo( pt );
            }
        }

        Shape shape = path.getGeneralPath();
        BasicStroke stroke = new BasicStroke( (float) resistorThickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1 );

        Shape sha = stroke.createStrokedShape( shape );
        Area area = new Area( sha );
        area.add( new Area( LineSegment.getSegment( srcpt, catPoint, viewThickness ) ) );
        area.add( new Area( LineSegment.getSegment( anoPoint, dstpt, viewThickness ) ) );
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

    public CircuitComponent getCircuitComponent() {
        return component;
    }

    public void delete() {
        super.delete();
        component.removeObserver( simpleObserver );
    }

    protected Point2D getAnoPoint() {
        return anoPoint;
    }

    protected Point2D getCatPoint() {
        return catPoint;
    }

}
