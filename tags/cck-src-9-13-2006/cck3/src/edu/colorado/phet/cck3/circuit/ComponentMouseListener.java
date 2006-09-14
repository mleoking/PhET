/** Sam Reid*/
package edu.colorado.phet.cck3.circuit;

import edu.colorado.phet.cck3.circuit.components.CircuitComponent;
import edu.colorado.phet.common_cck.math.ImmutableVector2D;
import edu.colorado.phet.common_cck.math.Vector2D;

import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: May 28, 2004
 * Time: 1:45:35 PM
 * Copyright (c) May 28, 2004 by Sam Reid
 */
public class ComponentMouseListener extends MouseInputAdapter {
    private boolean isDragging = false;
    private ImmutableVector2D.Double toStart;
    private ImmutableVector2D.Double toEnd;
    private CircuitGraphic circuitGraphic;
    private IComponentGraphic branchGraphic;
    private Circuit circuit;
    private CircuitGraphic.DragMatch match;

    public ComponentMouseListener( final CircuitGraphic circuitGraphic, final IComponentGraphic branchGraphic ) {
        this.circuitGraphic = circuitGraphic;
        this.branchGraphic = branchGraphic;
        this.circuit = circuitGraphic.getCircuit();
    }

    public void mousePressed( MouseEvent e ) {
        isDragging = false;
        match = null;
        if( e.isControlDown() ) {
            branchGraphic.getCircuitComponent().setSelected( true );//add selection.
        }
        else {
            circuit.setSelection( branchGraphic.getCircuitComponent() );
        }
    }

    public void mouseReleased( MouseEvent e ) {
        isDragging = false;
        if( match != null ) {
            circuitGraphic.collapseJunctions( match.getSource(), match.getTarget() );
        }
        match = null;
        circuitGraphic.bumpAway( branchGraphic.getCircuitComponent() );
    }

    public void mouseDragged( MouseEvent e ) {
        Point2D modelCoords = circuitGraphic.getTransform().viewToModel( e.getPoint() );
        if( !isDragging ) {
            isDragging = true;
            Point2D startJ = branchGraphic.getCircuitComponent().getStartJunction().getPosition();
            Point2D endJ = branchGraphic.getCircuitComponent().getEndJunction().getPosition();
            toStart = new ImmutableVector2D.Double( modelCoords, startJ );
            toEnd = new ImmutableVector2D.Double( modelCoords, endJ );
        }
        else {
            Point2D newStartPosition = toStart.getDestination( modelCoords );
            CircuitComponent component = branchGraphic.getCircuitComponent();
            Vector2D dx = new Vector2D.Double( component.getStartJunction().getPosition(), newStartPosition );
            Branch[] sc = circuit.getStrongConnections( branchGraphic.getCircuitComponent().getStartJunction() );
            match = circuitGraphic.getBestDragMatch( sc, dx );
            if( match == null ) {
                BranchSet branchSet = new BranchSet( circuit, sc );
                branchSet.translate( dx );
            }
            else {
                Vector2D vector = match.getVector();
                BranchSet branchSet = new BranchSet( circuit, sc );
                branchSet.translate( vector );
            }
        }
    }

}
