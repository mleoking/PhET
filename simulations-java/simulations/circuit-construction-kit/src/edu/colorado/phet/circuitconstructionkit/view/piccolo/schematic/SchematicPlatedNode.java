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
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;

/**
 * User: Sam Reid
 * Date: May 25, 2004
 * Time: 8:34:54 PM
 */
public class SchematicPlatedNode extends ComponentNode {
    private double wireThickness;
    private Area mouseArea;
    private SimpleObserver simpleObserver;
    private double fracDistToPlate;
    private double scaleHeightLeft;
    private double scaleHeightRight;
    private PhetPPath path;
    private Shape clipShape;

    public SchematicPlatedNode( CCKModel cckModel, CircuitComponent circuitComponent, JComponent component, CCKModule module, double wireThickness, double fracDistToPlate,
                                double scaleHeightLeft, double scaleHeightRight ) {
        super( cckModel, circuitComponent, component, module );
        this.fracDistToPlate = fracDistToPlate;
        this.scaleHeightLeft = scaleHeightLeft;
        this.scaleHeightRight = scaleHeightRight;
        this.wireThickness = wireThickness;
        simpleObserver = new SimpleObserver() {
            public void update() {
                SchematicPlatedNode.this.update();
            }
        };
        path = new PhetPPath( Color.black );
        addChild( path );
        circuitComponent.addObserver( simpleObserver );
        update();
        setVisible( true );
    }

    protected void update() {
        super.update();
        Point2D src = getBranch().getStartJunction().getPosition();
        Point2D dst = getBranch().getEndJunction().getPosition();
        double viewThickness = wireThickness;

        Vector2D vector = new Vector2D( src, dst );
        Point2D cat = vector.getScaledInstance( fracDistToPlate ).getDestination( src );
        Point2D ano = vector.getScaledInstance( 1 - fracDistToPlate ).getDestination( src );
        Vector2D east = vector.getInstanceOfMagnitude( 1 );
        Vector2D north = east.getNormalVector();
        double catHeight = viewThickness * this.scaleHeightLeft;
        double anoHeight = viewThickness * this.scaleHeightRight;
        Point2D catHat = north.getInstanceOfMagnitude( catHeight ).getDestination( cat );
        Point2D cattail = north.getInstanceOfMagnitude( catHeight ).getScaledInstance( -1 ).getDestination( cat );

        Point2D anoHat = north.getInstanceOfMagnitude( anoHeight ).getDestination( ano );
        Point2D anotail = north.getInstanceOfMagnitude( anoHeight ).getScaledInstance( -1 ).getDestination( ano );

        double thickness = viewThickness / 2;
        Area area = new Area();
        area.add( new Area( LineSegment.getSegment( src, cat, viewThickness ) ) );
        area.add( new Area( LineSegment.getSegment( ano, dst, viewThickness ) ) );
        area.add( new Area( LineSegment.getSegment( catHat, cattail, thickness ) ) );
        area.add( new Area( LineSegment.getSegment( anoHat, anotail, thickness ) ) );

        clipShape = LineSegment.getSegment( cat, ano, viewThickness * 10 );

        mouseArea = new Area( area );
        mouseArea.add( new Area( LineSegment.getSegment( src, dst, viewThickness ) ) );
        path.setPathTo( area );
        getHighlightNode().setStroke( new BasicStroke( 0.1f ) );
        getHighlightNode().setPathTo( area );
    }

    protected Shape getClipShape() {
        return clipShape;
    }

    public void delete() {
        super.delete();
        getBranch().removeObserver( simpleObserver );
    }
}