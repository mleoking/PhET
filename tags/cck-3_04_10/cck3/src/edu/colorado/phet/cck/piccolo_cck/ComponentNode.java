package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.common.DynamicPopupMenuHandler;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.cck.model.components.CircuitComponent;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 19, 2006
 * Time: 1:06:43 AM
 * Copyright (c) Sep 19, 2006 by Sam Reid
 */

public abstract class ComponentNode extends BranchNode {
    private CCKModel model;
    private CircuitComponent circuitComponent;
    private ICCKModule module;
    private CircuitInteractionModel circuitInteractionModel;
    private PPath highlightNode;
    private JComponent parent;
    private SimpleObserver componentObserver = new SimpleObserver() {
        public void update() {
            ComponentNode.this.update();
        }
    };

    public ComponentNode( final CCKModel model, final CircuitComponent circuitComponent, JComponent parent, ICCKModule module ) {
        this.model = model;
        this.circuitComponent = circuitComponent;
        this.module = module;
        this.circuitInteractionModel = new CircuitInteractionModel( model.getCircuit() );

        highlightNode = new PPath();
        highlightNode.setStrokePaint( Color.yellow );
        highlightNode.setStroke( new BasicStroke( 3f ) );
        addChild( highlightNode );

        circuitComponent.addObserver( componentObserver );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            public boolean dragging = false;

            public void mouseDragged( PInputEvent event ) {
                if( event.isLeftMouseButton() ) {
                    dragging = true;
                    circuitInteractionModel.translate( circuitComponent, event.getPositionRelativeTo( ComponentNode.this.getParent() ) );
                }
            }

            public void mouseReleased( PInputEvent event ) {
                if( event.isLeftMouseButton() && dragging ) {
                    circuitInteractionModel.dropBranch( circuitComponent );
                    dragging = false;
                }
            }

            public void mousePressed( PInputEvent event ) {
                if( event.isControlDown() ) {
                    circuitComponent.setSelected( !circuitComponent.isSelected() );
                }
                else {
                    model.getCircuit().setSelection( circuitComponent );
                }
            }
        } );

        addInputEventListener( new DynamicPopupMenuHandler( parent, new DynamicPopupMenuHandler.JPopupMenuFactory() {
            public JPopupMenu createPopupMenu() {
                return ComponentNode.this.createPopupMenu();
            }
        } ) );
        this.parent = parent;
    }

    public void delete() {
        circuitComponent.removeObserver( componentObserver );
    }

    protected JPopupMenu createPopupMenu() {
        return new CCKPopupMenuFactory( module ).createPopupMenu( circuitComponent );
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

    public CCKModel getCCKModel() {
        return model;
    }

}
