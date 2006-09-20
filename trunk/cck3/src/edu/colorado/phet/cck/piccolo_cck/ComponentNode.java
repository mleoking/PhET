package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.cck.model.components.CircuitComponent;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 19, 2006
 * Time: 1:06:43 AM
 * Copyright (c) Sep 19, 2006 by Sam Reid
 */

public class ComponentNode extends BranchNode {
    private CCKModel model;
    private CircuitComponent circuitComponent;
    private CircuitInteractionModel circuitInteractionModel;
    private PPath highlightNode;

    public ComponentNode( final CCKModel model, final CircuitComponent circuitComponent, Component parent ) {
        this.model = model;
        this.circuitComponent = circuitComponent;
        this.circuitInteractionModel = new CircuitInteractionModel( model.getCircuit() );

        highlightNode = new PPath();
        highlightNode.setStrokePaint( Color.yellow );
        highlightNode.setStroke( new BasicStroke( 3f ) );
        addChild( highlightNode );

        circuitComponent.addObserver( new SimpleObserver() {
            public void update() {
                ComponentNode.this.update();
            }
        } );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                circuitInteractionModel.translate( circuitComponent, event.getPositionRelativeTo( ComponentNode.this.getParent() ) );
            }

            public void mouseReleased( PInputEvent event ) {
                circuitInteractionModel.dropBranch( circuitComponent );
            }

            public void mousePressed( PInputEvent event ) {
                model.getCircuit().setSelection( circuitComponent );
            }
        } );
    }

    protected void update() {
        highlightNode.setVisible( circuitComponent.isSelected() );
    }

    public Branch getBranch() {
        return circuitComponent;
    }

    public PPath getHighlightNode() {
        return highlightNode;
    }

    public CircuitComponent getCircuitComponent() {
        return circuitComponent;
    }
}
