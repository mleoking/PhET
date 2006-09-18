package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.Circuit;
import edu.colorado.phet.cck.model.CircuitListenerAdapter;
import edu.colorado.phet.cck.model.Junction;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.cck.model.components.Resistor;
import edu.colorado.phet.cck.model.components.Wire;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.PNode;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 14, 2006
 * Time: 5:12:37 PM
 * Copyright (c) Sep 14, 2006 by Sam Reid
 */

public class CircuitNode extends PhetPNode {
    private CCKModel cckModel;
    private Circuit circuit;
    private ArrayList junctionGraphics = new ArrayList();

    public CircuitNode( CCKModel cckModel, Circuit circuit ) {
        this.cckModel = cckModel;
        this.circuit = circuit;
        circuit.addCircuitListener( new CircuitListenerAdapter() {
            public void branchAdded( Branch branch ) {
                addChild( createNode( branch ) );
            }

            public void junctionAdded( Junction junction ) {
                JunctionNode node = createNode( junction );
                junctionGraphics.add( node );
                addChild( node );
            }

            public void junctionRemoved( Junction junction ) {
                for( int i = 0; i < junctionGraphics.size(); i++ ) {
                    JunctionNode junctionNode = (JunctionNode)junctionGraphics.get( i );
                    if( junctionNode.getJunction() == junction ) {
                        removeJunctionGraphic( junctionNode );
                    }
                }
            }
        } );
    }

    private void removeJunctionGraphic( JunctionNode junctionNode ) {
        junctionGraphics.remove( junctionNode );
        removeChild( junctionNode );
    }

    public JunctionNode createNode( Junction junction ) {
        return new JunctionNode( cckModel, junction, this );
    }

    public PNode createNode( Branch branch ) {
        if( branch instanceof Wire ) {
            return new WireNode( cckModel, (Wire)branch );
        }
        else if( branch instanceof Resistor ) {
            return new ResistorNode( cckModel, (Resistor)branch );
        }
        else {
            throw new RuntimeException( "Unrecognized branch type: " + branch.getClass() );
        }
    }

    public Junction getStickyTarget( Junction junction, double x, double y ) {
        double bestDist = Double.POSITIVE_INFINITY;
        double STICKY_DIST = 2;
        Junction best = null;
        for( int i = 0; i < junctionGraphics.size(); i++ ) {
            //todo see circuitGraphic line 502 for handling strong components
            JunctionNode junctionNode = (JunctionNode)junctionGraphics.get( i );
            if( junctionNode.getJunction() != junction && !getCircuit().hasBranch( junction, junctionNode.getJunction() ) && !getCircuit().wouldConnectionCauseOverlappingBranches( junction, junctionNode.getJunction() ) )
            {
                double dist = new Point2D.Double( x, y ).distance( junctionNode.getJunction().getX(), junctionNode.getJunction().getY() );
                if( dist < STICKY_DIST && dist < bestDist ) {
                    best = junctionNode.getJunction();
                    bestDist = dist;
                }
            }
        }
        return best;
    }

    private Circuit getCircuit() {
        return cckModel.getCircuit();
    }
}
