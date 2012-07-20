// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.circuitconstructionkit.view.piccolo.schematic;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Point2D;

import javax.swing.JComponent;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.components.Branch;
import edu.colorado.phet.circuitconstructionkit.model.components.CircuitComponent;
import edu.colorado.phet.circuitconstructionkit.model.components.Resistor;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.ComponentNode;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.LineSegment;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;

/**
 * User: Sam Reid
 * Date: May 25, 2004
 * Time: 8:34:54 PM
 */
public class SchematicResistorNode extends ComponentNode {
    private CircuitComponent component;
    private double wireThickness;
    private Vector2D eastDir;
    private Vector2D northDir;
    private Point2D anoPoint;
    private Point2D catPoint;
    private Area mouseArea;
    private SimpleObserver simpleObserver;
    private PhetPPath resistorPPath;

    public SchematicResistorNode( CCKModel cckModel, Resistor resistor, JComponent component, CCKModule module ) {
        super( cckModel, resistor, component, module );

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
        for ( int i = 0; i < numPeaks - 1; i++ ) {
            path.lineToRelative( getVector( halfWavelength, -2 * zigHeight ) );
            path.lineToRelative( getVector( halfWavelength, 2 * zigHeight ) );
        }
        path.lineToRelative( getVector( quarterWavelength, -zigHeight ) );
        Shape shape = path.getGeneralPath();
        BasicStroke stroke = new BasicStroke( (float) resistorThickness );

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
        super.delete();
        component.removeObserver( simpleObserver );
    }

    protected Point2D getAnoPoint() {
        return anoPoint;
    }

    protected Point2D getCatPoint() {
        return catPoint;
    }

    public Branch getBranch() {
        return component;
    }
}
