/** Sam Reid*/
package edu.colorado.phet.cck3.circuit;

import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;

import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: May 28, 2004
 * Time: 1:45:35 PM
 * Copyright (c) May 28, 2004 by Sam Reid
 */
public class WireMouseListener extends MouseInputAdapter {
    boolean isDragging = false;
    private ImmutableVector2D.Double toStart;
    private ImmutableVector2D.Double toEnd;
    private Junction startTarget;
    private Junction endTarget;
    private CircuitGraphic circuitGraphic;
    private BranchGraphic branchGraphic;
    private Branch branch;
    private Circuit circuit;

    public WireMouseListener( final CircuitGraphic circuitGraphic, final BranchGraphic branchGraphic ) {
        this.circuitGraphic = circuitGraphic;
        this.branchGraphic = branchGraphic;
        this.branch = branchGraphic.getBranch();
        this.circuit = circuitGraphic.getCircuit();
    }

    public void mousePressed( MouseEvent e ) {
        isDragging = false;
        startTarget = null;
        endTarget = null;
    }

    public void mouseReleased( MouseEvent e ) {
        isDragging = false;
        Branch branch = branchGraphic.getBranch();
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
            Point2D startJ = branch.getStartJunction().getPosition();
            Point2D endJ = branch.getEndJunction().getPosition();
            toStart = new ImmutableVector2D.Double( modelCoords, startJ );
            toEnd = new ImmutableVector2D.Double( modelCoords, endJ );
//                    System.out.println( "toStart = " + toStart );
//                    System.out.println( "toEnd = " + toEnd );
        }
        else {
            Point2D newStartPosition = toStart.getDestination( modelCoords );
            Point2D newEndPosition = toEnd.getDestination( modelCoords );

            Circuit circuit = circuitGraphic.getCircuit();

            Junction startMatch = circuitGraphic.getBestDragMatch( branch.getStartJunction(), newStartPosition );
            Junction endMatch = circuitGraphic.getBestDragMatch( branch.getEndJunction(), newEndPosition );
            if( startMatch == endMatch && startMatch != null ) {
//                endMatch = null;//hack so both don't grab same target
                //choose the better man.
                double distToStart = startMatch.getDistance( newStartPosition );
                double distToEnd = startMatch.getDistance( newEndPosition );
                if( distToStart < distToEnd ) {
                    endMatch = null;
                }
                else {
                    startMatch = null;
                }
            }
            if( startMatch == null && endMatch == null ) {
                //nothing to do
                startTarget = null;
                endTarget = null;
            }
            else if( startMatch == null && endMatch != null ) {
                newEndPosition = endMatch.getPosition();
                endTarget = endMatch;
                startTarget = null;
            }
            else if( startMatch != null && endMatch == null ) {
                newStartPosition = startMatch.getPosition();
                startTarget = startMatch;
                endTarget = null;
            }
            else {
                if( !circuit.areNeighbors( startMatch, endMatch ) ) {
                    newStartPosition = startMatch.getPosition();
                    startTarget = startMatch;
                    newEndPosition = endMatch.getPosition();
                    endTarget = endMatch;
                }
            }
            Translator t = new Translator( newStartPosition, newEndPosition, circuitGraphic.getModule().getModel() );
//            circuitGraphic.getModule().getModel().addModelElement( t );
            t.stepInTime( 0 );
        }
    }

    class Translator implements ModelElement {
        Point2D newStartPosition;
        Point2D newEndPosition;
        private BaseModel model;

        public Translator( Point2D newStartPosition, Point2D newEndPosition, BaseModel model ) {
            this.newStartPosition = newStartPosition;
            this.newEndPosition = newEndPosition;
            this.model = model;
        }

        public void stepInTime( double dt ) {
            doTranslation( newStartPosition, newEndPosition );
            model.removeModelElement( this );
        }

    }

    private void doTranslation( Point2D newStartPosition, Point2D newEndPosition ) {
        Point2D a0 = branch.getStartJunction().getPosition();
        ImmutableVector2D aVec = new ImmutableVector2D.Double( a0, newStartPosition );
        Branch[] rhs = circuit.getStrongConnections( branch, branch.getStartJunction() );
        BranchSet rightSet = new BranchSet( circuit, rhs );
        rightSet.addJunction( branch.getStartJunction() );
        rightSet.removeBranch( branch );
        rightSet.translate( aVec );

        Point2D b0 = branch.getEndJunction().getPosition();
        ImmutableVector2D.Double bVec = new ImmutableVector2D.Double( b0, newEndPosition );
        Branch[] lhs = circuit.getStrongConnections( branch, branch.getEndJunction() );
        BranchSet leftSet = new BranchSet( circuit, lhs );
        leftSet.addJunction( branch.getEndJunction() );
        leftSet.removeBranch( branch );
        leftSet.translate( bVec );
//            //TODO the wrong kind of loop could break this.

        branchGraphic.getBranch().notifyObservers();
    }

}
