package edu.colorado.phet.circuitconstructionkit.view.piccolo.lifelike;

import java.awt.*;

import edu.colorado.phet.circuitconstructionkit.model.CCKDefaults;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.components.Branch;
import edu.colorado.phet.circuitconstructionkit.model.components.Wire;
import edu.colorado.phet.circuitconstructionkit.view.CCKLookAndFeel;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.BranchNode;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.CircuitInteractionModel;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.WirePopupMenu;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.PopupMenuHandler;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * User: Sam Reid
 * Date: Sep 14, 2006
 * Time: 5:24:18 PM
 */

public class WireNode extends BranchNode {
    private CCKModel cckModel;
    private Wire wire;
    private PPath wirePPath;
    private PPath wireHighlightPPath;
    private CircuitInteractionModel circuitInteractionModel;
    public static final double DEFAULT_HIGHLIGHT_STROKE_WIDTH = CCKDefaults.WIRE_THICKNESS * CCKDefaults.DEFAULT_SCALE * CCKLookAndFeel.HIGHLIGHT_SCALE;
    private double highlightStrokeWidth = DEFAULT_HIGHLIGHT_STROKE_WIDTH;
    private SimpleObserver wireObserver = new SimpleObserver() {
        public void update() {
            WireNode.this.update();
        }
    };

    public WireNode( final CCKModel cckModel, final Wire wire, Component component ) {
        this.cckModel = cckModel;
        this.wire = wire;
        this.circuitInteractionModel = new CircuitInteractionModel( cckModel );

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
                if ( event.isControlDown() ) {
                    wire.setSelected( !wire.isSelected() );
                }
                else {
                    cckModel.getCircuit().setSelection( wire );
                }
            }
        } );
        wire.addObserver( wireObserver );
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

        Shape highlightShape = new BasicStroke( (float) highlightStrokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER ).createStrokedShape( wire.getLine() );
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
