/** Sam Reid*/
package edu.colorado.phet.cck3.circuit;

import edu.colorado.phet.common.math.ImmutableVector2D;

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
    boolean isDragging = false;
    private ImmutableVector2D.Double toStart;
    private ImmutableVector2D.Double toEnd;
    private Junction startTarget;
    private Junction endTarget;
    private CircuitGraphic circuitGraphic;
    private IComponentGraphic branchGraphic;
    private Circuit circuit;

    public ComponentMouseListener( final CircuitGraphic circuitGraphic, final IComponentGraphic branchGraphic ) {
        this.circuitGraphic = circuitGraphic;
        this.branchGraphic = branchGraphic;
        this.circuit = circuitGraphic.getCircuit();
    }

    public void mousePressed( MouseEvent e ) {
        isDragging = false;
        startTarget = null;
        endTarget = null;
    }

    public void mouseReleased( MouseEvent e ) {
        isDragging = false;
        Branch branch = branchGraphic.getComponent();
        if( startTarget != null ) {
            circuitGraphic.collapseJunctions( startTarget, branch.getStartJunction() );
        }
        if( endTarget != null ) {
            circuitGraphic.collapseJunctions( endTarget, branch.getEndJunction() );
        }
    }

    public void mouseDragged( MouseEvent e ) {
        Point2D.Double modelCoords = circuitGraphic.getTransform().viewToModel( e.getPoint() );
        if( !isDragging ) {
            isDragging = true;
            Point2D startJ = branchGraphic.getComponent().getStartJunction().getPosition();
            Point2D endJ = branchGraphic.getComponent().getEndJunction().getPosition();
            toStart = new ImmutableVector2D.Double( modelCoords, startJ );
            toEnd = new ImmutableVector2D.Double( modelCoords, endJ );
//                    System.out.println( "toStart = " + toStart );
//                    System.out.println( "toEnd = " + toEnd );
        }
        else {
            Point2D newStartPosition = toStart.getDestination( modelCoords );
            Point2D newEndPosition = toEnd.getDestination( modelCoords );

            Branch b = branchGraphic.getComponent();

            Junction startMatch = circuitGraphic.getBestDragMatch( b.getStartJunction(), newStartPosition );
            Junction endMatch = circuitGraphic.getBestDragMatch( b.getEndJunction(), newEndPosition );

            if( startMatch != null && endMatch != null ) {
                double distToStartMatch = startMatch.getPosition().distance( newStartPosition );
                double distToEndMatch = endMatch.getPosition().distance( newEndPosition );
                if( distToStartMatch < distToEndMatch ) {
                    endMatch = null;
                }
                else {
                    startMatch = null;
                }
            }

            startTarget = null;
            endTarget = null;
            if( startMatch == null && endMatch == null ) {
                //nothing to do, just translate one.
                moveStart( newStartPosition, b );
                moveEnd( newEndPosition, b );
            }
            else if( startMatch == null && endMatch != null ) {
                endTarget = endMatch;
                newEndPosition = endMatch.getPosition();
                moveEnd( newEndPosition, b );
            }
            else if( startMatch != null && endMatch == null ) {
                startTarget = startMatch;
                newStartPosition = startMatch.getPosition();
                moveStart( newStartPosition, b );
            }
        }
    }

    private void moveEnd( Point2D newEndPosition, Branch b ) {
        Point2D b0 = b.getEndJunction().getPosition();
        ImmutableVector2D vec = new ImmutableVector2D.Double( b0, newEndPosition );
        Branch[] adj = circuit.getStrongConnections( b.getEndJunction() );
        BranchSet set = new BranchSet( circuit, adj );
        set.translate( vec );
    }

    private void moveStart( Point2D newStartPosition, Branch b ) {
        Point2D a0 = b.getStartJunction().getPosition();
        ImmutableVector2D vec = new ImmutableVector2D.Double( a0, newStartPosition );
        Branch[] adj = circuit.getStrongConnections( b.getStartJunction() );
        BranchSet set = new BranchSet( circuit, adj );
        set.translate( vec );
    }
}
