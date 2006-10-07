package edu.colorado.phet.cck.piccolo_cck.lifelike;

import edu.colorado.phet.cck.CCKLookAndFeel;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.cck.model.components.Wire;
import edu.colorado.phet.cck.piccolo_cck.BranchNode;
import edu.colorado.phet.cck.piccolo_cck.CircuitInteractionModel;
import edu.colorado.phet.cck.piccolo_cck.WirePopupMenu;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.colorado.phet.piccolo.event.PopupMenuHandler;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 14, 2006
 * Time: 5:24:18 PM
 * Copyright (c) Sep 14, 2006 by Sam Reid
 */

public class WireNode extends BranchNode {
    private CCKModel cckModel;
    private Wire wire;
    private PPath wirePPath;
    private PPath wireHighlightPPath;
    private CircuitInteractionModel circuitInteractionModel;
    public static final double DEFAULT_HIGHLIGHT_STROKE_WIDTH = CCKLookAndFeel.WIRE_THICKNESS * CCKLookAndFeel.DEFAULT_SCALE * CCKLookAndFeel.HIGHLIGHT_SCALE;
    private double highlightStrokeWidth = DEFAULT_HIGHLIGHT_STROKE_WIDTH;
    private SimpleObserver wireObserver = new SimpleObserver() {
        public void update() {
            WireNode.this.update();
        }
    };

    public WireNode( final CCKModel cckModel, final Wire wire, Component component ) {
        this.cckModel = cckModel;
        this.wire = wire;
        this.circuitInteractionModel = new CircuitInteractionModel( cckModel.getCircuit() );

        wireHighlightPPath = new PPath();
        wireHighlightPPath.setPaint( CCKLookAndFeel.HIGHLIGHT_COLOR );
        wireHighlightPPath.setStroke( null );

        wirePPath = new PPath();
        setWirePaint( CCKLookAndFeel.COPPER );
        wirePPath.setStroke( null );
        addChild( wireHighlightPPath );
        addChild( wirePPath );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                circuitInteractionModel.translate( wire, event.getPositionRelativeTo( WireNode.this.getParent() ) );
            }

            public void mouseReleased( PInputEvent event ) {
                circuitInteractionModel.dropBranch( wire );
            }

            public void mousePressed( PInputEvent event ) {
                cckModel.getCircuit().setSelection( wire );
            }
        } );
        wire.addObserver( wireObserver );
//        wire.getStartJunction().addObserver( new SimpleObserver() {
//            public void update() {
//                WireNode.this.update();
//            }
//        } );
//        wire.getEndJunction().addObserver( new SimpleObserver() {
//            public void update() {
//                WireNode.this.update();
//            }
//        } );
        update();
        addInputEventListener( new PopupMenuHandler( component, new WirePopupMenu( cckModel, wire ) ) );
    }

    public void setHighlightStrokeWidth( double highlightStrokeWidth ) {
        this.highlightStrokeWidth = highlightStrokeWidth;
        update();
    }

    public void setWirePaint( Color newPaint ) {
        wirePPath.setPaint( newPaint );
    }

    private void update() {
        wireHighlightPPath.setVisible( wire.isSelected() );

        Shape highlightShape = new BasicStroke( (float)highlightStrokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER ).createStrokedShape( wire.getLine() );
        wireHighlightPPath.setPathTo( highlightShape );

        wirePPath.setPathTo( wire.getShape() );
    }

    public Branch getBranch() {
        return wire;
    }

    public void delete() {
        wire.removeObserver( wireObserver );
    }
}
