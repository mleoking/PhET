package edu.colorado.phet.cck.piccolo_cck.schematic;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.common.LineSegment;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.cck.model.components.CircuitComponent;
import edu.colorado.phet.cck.model.components.Resistor;
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
public class SchematicResistorNode extends ComponentNode {
    private CircuitComponent component;
    private double wireThickness;
    private AbstractVector2D eastDir;
    private AbstractVector2D northDir;
    private Point2D anoPoint;
    private Point2D catPoint;
    private Area mouseArea;
    private SimpleObserver simpleObserver;
    private PhetPPath resistorPPath;

    public SchematicResistorNode( CCKModel cckModel, Resistor resistor, Component component, ICCKModule module ) {
        super( cckModel, resistor, component );

        resistorPPath = new PhetPPath( Color.black );
        this.component = resistor;
        this.wireThickness = 0.12;
        simpleObserver = new SimpleObserver() {
            public void update() {
                changed();
            }
        };
        resistor.addObserver( simpleObserver );
        changed();
        addChild( resistorPPath );
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
        double viewThickness = wireThickness;
        double resistorThickness = viewThickness / 2.5;
        double resistorWidth = catPoint.distance( anoPoint );
        int numPeaks = 3;
        double zigHeight = viewThickness * 1.2;
        //zig zags go here.
        int numQuarters = ( numPeaks - 1 ) * 4 + 2;
        double numWaves = numQuarters / 4.0;
        double wavelength = resistorWidth / numWaves;
        double quarterWavelength = wavelength / 4.0;
        double halfWavelength = wavelength / 2.0;
        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( catPoint );
        path.lineToRelative( getVector( quarterWavelength, zigHeight ) );
        for( int i = 0; i < numPeaks - 1; i++ ) {
            path.lineToRelative( getVector( halfWavelength, -2 * zigHeight ) );
            path.lineToRelative( getVector( halfWavelength, 2 * zigHeight ) );
        }
        path.lineToRelative( getVector( quarterWavelength, -zigHeight ) );
        Shape shape = path.getGeneralPath();
        BasicStroke stroke = new BasicStroke( (float)resistorThickness );

        Shape sha = stroke.createStrokedShape( shape );
        Area area = new Area( sha );
        area.add( new Area( LineSegment.getSegment( srcpt, catPoint, viewThickness ) ) );
        area.add( new Area( LineSegment.getSegment( anoPoint, dstpt, viewThickness ) ) );
        mouseArea = new Area( area );
        mouseArea.add( new Area( LineSegment.getSegment( srcpt, dstpt, viewThickness ) ) );
        resistorPPath.setPathTo( area );

        Stroke highlightStroke = new BasicStroke( 0.1f );
        getHighlightNode().setStroke( highlightStroke );
        getHighlightNode().setPathTo( area );
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

    protected JPopupMenu createPopupMenu() {
        return null;
    }

    public Branch getBranch() {
        return component;
    }
}
