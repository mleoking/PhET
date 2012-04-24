// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Control panel that can be closed up to hide the controls it contains.
 *
 * @author John Blanco
 */
public class CollapsibleControlPanel extends PNode {

    private static final boolean INITIAL_OPEN_STATE = false;

    private final double SPACING = 10;
    private PNode contentNode = new PNode();
    private PNode openPanelNode;
    private PNode closedPanelNode;
    private boolean isOpen = INITIAL_OPEN_STATE;

    /**
     * Constructor.
     *
     * @param backgroundColor
     * @param titleText
     * @param titleFont
     * @param controls
     */
    public CollapsibleControlPanel( Color backgroundColor, String titleText, Font titleFont, PNode controls ) {

        // Create and initialize the spacer node that will be used to pad the
        // width so that it is the same when open or closed.
        PNode closedSpacer = new PNode();
        double padAmount = controls.getFullBoundsReference().width -
                           ( new PhetPText( titleText, titleFont ) ).getFullBoundsReference().width -
                           ( new PImage( PhetCommonResources.getMaximizeButtonImage() ) ).getFullBoundsReference().width -
                           SPACING * 2;
        if ( padAmount > 0 ) {
            closedSpacer.addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, padAmount, 0.001 ), new Color( 0, 0, 0, 0 ) ) );
        }

        // Create the contents for the closed version of the panel.
        PNode enlargeButton = new PImage( PhetCommonResources.getMaximizeButtonImage() );
        closedPanelNode = new ControlPanelNode( new HBox( SPACING, enlargeButton, new PhetPText( titleText, titleFont ), closedSpacer ),
                                                backgroundColor );

        // Create the contents for the open version of the panel.
        PNode contractButton = new PImage( PhetCommonResources.getMinimizeButtonImage() );
        openPanelNode = new ControlPanelNode( new VBox( SPACING, VBox.LEFT_ALIGNED, new HBox( contractButton, new PhetPText( titleText, titleFont ) ), controls ),
                                              backgroundColor );

        // Add the main content node, which is the only direct child.
        contentNode.addChild( isOpen ? openPanelNode : closedPanelNode );
        addChild( contentNode );

        // Add handlers that switch the contents when the buttons are pressed.
        enlargeButton.addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseReleased( PInputEvent event ) {
                contentNode.removeAllChildren();
                contentNode.addChild( openPanelNode );
            }
        } );
        contractButton.addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseReleased( PInputEvent event ) {
                contentNode.removeAllChildren();
                contentNode.addChild( closedPanelNode );
            }
        } );
    }

    /**
     * Set the panel to be open or closed.
     *
     * @param open
     */
    public void setOpen( boolean open ) {
        if ( open != isOpen ) {
            isOpen = open;
            contentNode.removeAllChildren();
            contentNode.addChild( isOpen ? openPanelNode : closedPanelNode );
        }
    }

    /**
     * Get the full bounds of this panel when it is opened.  This is useful
     * for doing layout.
     *
     * @return
     */
    public PBounds getFullBoundsWhenOpen() {
        boolean wasOpen = isOpen;
        if ( !wasOpen ) {
            setOpen( true );
        }
        PBounds bounds = getFullBounds();
        setOpen( wasOpen );
        return bounds;
    }

    /**
     * Test harness - constructs a PhET Piccolo canvas in a window and tests
     * out the class.
     */
    public static void main( String[] args ) {

        // Create the canvas.
        PhetPCanvas canvas = new PhetPCanvas();

        // Set up the canvas-screen transform.
        Dimension2D stageSize = new PDimension( 700, 500 );
        canvas.setWorldTransformStrategy( new PhetPCanvas.CenteredStage( canvas, stageSize ) );

        // Create the node being tested.
        CollapsibleControlPanel controlPanel = new CollapsibleControlPanel( new Color( 176, 226, 255 ),
                                                                            "Control Panel",
                                                                            new PhetFont( 16, true ),
                                                                            new PhetPPath( new Rectangle2D.Double( 0, 0, 200, 100 ), Color.PINK ) );
        controlPanel.setOffset( 100, 100 );
        canvas.addWorldChild( controlPanel );

        // Boiler plate app stuff.
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.setSize( (int) stageSize.getWidth(), (int) stageSize.getHeight() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocationRelativeTo( null ); // Center.
        frame.setVisible( true );
    }
}
