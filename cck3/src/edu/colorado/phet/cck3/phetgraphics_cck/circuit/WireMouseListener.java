/** Sam Reid*/
package edu.colorado.phet.cck3.phetgraphics_cck.circuit;

import edu.colorado.phet.cck3.model.BranchSet;
import edu.colorado.phet.cck3.model.Circuit;
import edu.colorado.phet.cck3.model.Junction;
import edu.colorado.phet.cck3.model.components.Branch;
import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.math.Vector2D;

import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

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
    private CircuitGraphic circuitGraphic;
    private BranchGraphic branchGraphic;
    private Branch branch;
    private Circuit circuit;
    private CircuitGraphic.DragMatch startMatch;
    private CircuitGraphic.DragMatch endMatch;

    public WireMouseListener( final CircuitGraphic circuitGraphic, final BranchGraphic branchGraphic ) {
        this.circuitGraphic = circuitGraphic;
        this.branchGraphic = branchGraphic;
        this.branch = branchGraphic.getBranch();
        this.circuit = circuitGraphic.getCircuit();
    }

    public void mousePressed( MouseEvent e ) {
        isDragging = false;
        startMatch = null;
        endMatch = null;
        if( e.isControlDown() ) {
            branch.setSelected( true );
        }
        else {
            circuitGraphic.getCircuit().setSelection( branch );
        }
    }

    public void mouseReleased( MouseEvent e ) {
        isDragging = false;
        if( startMatch != null ) {
            circuitGraphic.collapseJunctions( startMatch.getSource(), startMatch.getTarget() );
        }
        if( endMatch != null ) {
            circuitGraphic.collapseJunctions( endMatch.getSource(), endMatch.getTarget() );
        }
        circuitGraphic.bumpAway( branch );
    }

    public void mouseDragged( final MouseEvent e ) {
//        ModelElement me = new ModelElement() {
//            public void stepInTime( double dt ) {
//                domouseDragged( e );
//                CCK3Module.getModule().getModel().removeModelElement( this );
//            }
//        };
//        CCK3Module.getModule().getModel().addModelElement( me );
        domouseDragged( e );
    }

    public void domouseDragged( MouseEvent e ) {
        Point2D modelCoords = circuitGraphic.getTransform().viewToModel( e.getPoint() );
        if( !isDragging ) {
            isDragging = true;
            Point2D startJ = branch.getStartJunction().getPosition();
            Point2D endJ = branch.getEndJunction().getPosition();
            toStart = new ImmutableVector2D.Double( modelCoords, startJ );
            toEnd = new ImmutableVector2D.Double( modelCoords, endJ );
        }
        else {
            Point2D newStartPosition = toStart.getDestination( modelCoords );
            Point2D newEndPosition = toEnd.getDestination( modelCoords );
            Branch[] scStart = circuit.getStrongConnections( branch.getStartJunction() );
            Branch[] scEnd = circuit.getStrongConnections( branch.getEndJunction() );
            Vector2D startDX = new Vector2D.Double( branch.getStartJunction().getPosition(), newStartPosition );
            Vector2D endDX = new Vector2D.Double( branch.getEndJunction().getPosition(), newEndPosition );
            Junction[] startSources = getSources( scStart, branch.getStartJunction() );
            Junction[] endSources = getSources( scEnd, branch.getEndJunction() );
            //how about removing any junctions in start and end that share a branch?
            //Is this sufficient to keep from dropping wires directly on other wires?

            startMatch = circuitGraphic.getBestDragMatch( startSources, startDX );
            endMatch = circuitGraphic.getBestDragMatch( endSources, endDX );

            if( startMatch != null && endMatch != null ) {
                for( int i = 0; i < circuit.numBranches(); i++ ) {
                    Branch branch = circuit.branchAt( i );
                    if( branch.hasJunction( startMatch.getTarget() ) && branch.hasJunction( endMatch.getTarget() ) ) {
                        startMatch = null;
                        endMatch = null;
                        break;
                    }
                }
            }
            apply( scStart, startDX, branch.getStartJunction(), startMatch );
            apply( scEnd, endDX, branch.getEndJunction(), endMatch );
        }
    }

    private Junction[] getSources( Branch[] sc, Junction j ) {
        ArrayList list = new ArrayList( Arrays.asList( Circuit.getJunctions( sc ) ) );
        if( !list.contains( j ) ) {
            list.add( j );
        }
        return (Junction[])list.toArray( new Junction[0] );
    }

    private void apply( Branch[] sc, Vector2D dx, Junction junction, CircuitGraphic.DragMatch match ) {
        if( match == null ) {
            BranchSet bs = new BranchSet( circuit, sc );
            bs.addJunction( junction );
            bs.translate( dx );
        }
        else {
            BranchSet bs = new BranchSet( circuit, sc );
            AbstractVector2D vec = match.getVector();
            bs.addJunction( junction );
            bs.translate( vec );
        }
//        System.out.println( "match = " + match );
    }

}
