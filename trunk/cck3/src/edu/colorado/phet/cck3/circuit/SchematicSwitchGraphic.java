/** Sam Reid*/
package edu.colorado.phet.cck3.circuit;

import edu.colorado.phet.cck3.circuit.components.CircuitComponent;
import edu.colorado.phet.cck3.circuit.components.Switch;
import edu.colorado.phet.cck3.common.LineSegment;
import edu.colorado.phet.cck3.common.primarygraphics.PrimaryShapeGraphic;
import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.fastpaint.FastPaintShapeGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jun 18, 2004
 * Time: 10:53:37 AM
 * Copyright (c) Jun 18, 2004 by Sam Reid
 */
public class SchematicSwitchGraphic extends FastPaintShapeGraphic implements IComponentGraphic, TransformListener, SimpleObserver {
    private ApparatusPanel apparatusPanel;
    private Switch aSwitch;
    private ModelViewTransform2D transform;
    private double wireThickness;
    private Shape userSpace;
    private double leverLength;
    private Point2D pivot;
    PrimaryShapeGraphic highlightRegion;

    public SchematicSwitchGraphic( ApparatusPanel apparatusPanel, Switch aSwitch, ModelViewTransform2D transform, double wireThickness ) {
        super( new Area(), Color.black, apparatusPanel );
        highlightRegion = new PrimaryShapeGraphic( apparatusPanel, new Area(), Color.yellow );
        this.apparatusPanel = apparatusPanel;
        this.aSwitch = aSwitch;
        this.transform = transform;
        this.wireThickness = wireThickness;
        transform.addTransformListener( this );
        aSwitch.addObserver( this );
        changed();
    }

    private void changed() {
        Point2D srcpt = transform.toAffineTransform().transform( aSwitch.getStartJunction().getPosition(), null );
        Point2D dstpt = transform.toAffineTransform().transform( aSwitch.getEndJunction().getPosition(), null );
//        Point2D tmp=srcpt;
//        srcpt=dstpt;
//        dstpt=srcpt;
        double viewThickness = Math.abs( transform.modelToViewDifferentialY( wireThickness ) );
        double fracToPivot = .3;
        double fracToEnd = ( 1 - fracToPivot );

        AbstractVector2D vector = new ImmutableVector2D.Double( srcpt, dstpt );
        pivot = vector.getScaledInstance( fracToPivot ).getDestination( srcpt );
        Point2D connectionPt = vector.getScaledInstance( fracToEnd ).getDestination( srcpt );
        this.leverLength = pivot.distance( connectionPt );
        Area area = new Area();
        userSpace = LineSegment.getSegment( srcpt, dstpt, viewThickness );
        area.add( new Area( LineSegment.getSegment( srcpt, pivot, viewThickness ) ) );
        area.add( new Area( LineSegment.getSegment( connectionPt, dstpt, viewThickness ) ) );
        super.setShape( area );
        Stroke highlightsStroke = new BasicStroke( 6 );
        highlightRegion.setShape( highlightsStroke.createStrokedShape( area ) );
        highlightRegion.setVisible( aSwitch.isSelected() );
    }

    public void paint( Graphics2D g ) {
        highlightRegion.paint( g );
        super.paint( g );
    }

    public ModelViewTransform2D getModelViewTransform2D() {
        return transform;
    }

    public CircuitComponent getCircuitComponent() {
        return aSwitch;
    }

    public void delete() {
        aSwitch.removeObserver( this );
        transform.removeTransformListener( this );
    }

    public boolean contains( int x, int y ) {
        return userSpace != null && userSpace.contains( x, y );
    }

    public Point2D getPivot() {
        return pivot;
    }

    public double getLeverLengthModelCoordinates() {
        return transform.viewToModelDifferentialX( leverLength );
    }

    //in view coordinates.
    public double getLeverLength() {
        return leverLength;
    }

    public Switch getSwitch() {
        return aSwitch;
    }

    public void transformChanged( ModelViewTransform2D mvt ) {
        changed();
    }

    public void update() {
        changed();
    }
}
