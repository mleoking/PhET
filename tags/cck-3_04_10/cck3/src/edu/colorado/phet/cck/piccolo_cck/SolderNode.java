package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.Circuit;
import edu.colorado.phet.cck.model.CircuitListenerAdapter;
import edu.colorado.phet.cck.model.Junction;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.colorado.phet.piccolo.PhetPNode;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Oct 3, 2006
 * Time: 4:42:36 PM
 * Copyright (c) Oct 3, 2006 by Sam Reid
 */

public class SolderNode extends PhetPNode {
    private Circuit circuit;
    private Junction junction;
    private Color color;
    private PhetPPath path;

    public SolderNode( Circuit circuit, Junction junction, Color color ) {
        this.circuit = circuit;
        this.junction = junction;
        this.color = color;
        path = new PhetPPath( color );
        addChild( path );
        update();
        junction.addObserver( new SimpleObserver() {
            public void update() {
                SolderNode.this.update();
            }
        } );
        circuit.addCircuitListener( new CircuitListenerAdapter() {
            public void junctionsConnected( Junction a, Junction b, Junction newTarget ) {
                update();
            }

            public void branchRemoved( Branch branch ) {
                update();
            }

            public void junctionsSplit( Junction old, Junction[] j ) {
                update();
            }

            public void junctionRemoved( Junction junction ) {
                update();
            }
        } );
    }

    private void update() {
        setVisible( circuit.getNeighbors( junction ).length > 1 );
        path.setPathTo( junction.createCircle( CCKModel.JUNCTION_RADIUS * 1.6 ) );
    }

    public Junction getJunction() {
        return junction;
    }
}
