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

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: May 25, 2004
 * Time: 8:34:54 PM
 * Copyright (c) May 25, 2004 by Sam Reid
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

    public SchematicPlatedNode( CCKModel cckModel, CircuitComponent circuitComponent, JComponent component, ICCKModule module, double wireThickness, double fracDistToPlate,
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

        ImmutableVector2D vector = new ImmutableVector2D.Double( src, dst );
        Point2D cat = vector.getScaledInstance( fracDistToPlate ).getDestination( src );
        Point2D ano = vector.getScaledInstance( 1 - fracDistToPlate ).getDestination( src );
        AbstractVector2D east = vector.getInstanceOfMagnitude( 1 );
        AbstractVector2D north = east.getNormalVector();
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

        clipShape=LineSegment.getSegment( cat,ano, viewThickness*10 );
        
        mouseArea = new Area( area );
        mouseArea.add( new Area( LineSegment.getSegment( src, dst, viewThickness ) ) );
        path.setPathTo( area );
        getHighlightNode().setStroke( new BasicStroke( 0.1f ) );
        getHighlightNode().setPathTo( area );
//        notifyChanged();
    }

    protected Shape getClipShape() {
//        return path.getPathReference();
        return clipShape;
    }

//    private void notifyChanged() {
//        notifyListeners();
//    }

    public void delete() {
        super.delete();
        getBranch().removeObserver( simpleObserver );
    }

//    private ArrayList listeners = new ArrayList();
//
//    public static interface Listener {
//        void areaChanged();
//    }
//
//    public void addListener( SchematicPlatedNode.Listener listener ) {
//        listeners.add( listener );
//    }
//
//    public void notifyListeners() {
//        for( int i = 0; i < listeners.size(); i++ ) {
//            SchematicPlatedNode.Listener listener = (SchematicPlatedNode.Listener)listeners.get( i );
//            listener.areaChanged();
//        }
//    }
}
