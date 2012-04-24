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
    private static final double BOX_SPACING = 10;

    // Various nodes that comprise this node.  They need to be member variables
    // so that layout can be adjusted when needed.
    private final PNode rootNode = new PNode();
    private final PNode closedPanelContents;
    private final PNode closedPanelNode;
    private final PNode controls;
    private final PNode openPanelNode;

    // Nodes used to pad the layout.
    private PNode titlePad = new PNode();
    private PNode leftContentPad = new PNode();
    private PNode rightContentPad = new PNode();

    // State var that tracks whether control panel is open or closed.
    private boolean isOpen = INITIAL_OPEN_STATE;

    // The minimum width for the control panel, can be set by client.
    private double minWidth = 0;

    /**
     * Constructor.
     *
     * @param backgroundColor
     * @param titleText
     * @param titleFont
     * @param controls
     */
    public CollapsibleControlPanel( Color backgroundColor, String titleText, Font titleFont, PNode controls ) {
        this.controls = controls;

        // Create the contents for the closed version of the panel.
        PNode expandButton = new PImage( PhetCommonResources.getMaximizeButtonImage() );
        closedPanelContents = new HBox( BOX_SPACING, expandButton, new PhetPText( titleText, titleFont ), titlePad );
        closedPanelNode = new ControlPanelNode( closedPanelContents, backgroundColor );

        // Create the contents for the open version of the panel.
        PNode collapseButton = new PImage( PhetCommonResources.getMinimizeButtonImage() );
        openPanelNode = new ControlPanelNode( new VBox( BOX_SPACING,
                                                        VBox.LEFT_ALIGNED, new HBox( collapseButton, new PhetPText( titleText, titleFont ) ),
                                                        new HBox( leftContentPad, controls, rightContentPad ) ),
                                              backgroundColor );

        // Add the main content node, which is the only direct child.
        rootNode.addChild( isOpen ? openPanelNode : closedPanelNode );
        addChild( rootNode );

        // Make the open and closed size match.
        updatePadding();

        // Add handlers that switch the contents when the buttons are pressed.
        expandButton.addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseReleased( PInputEvent event ) {
                rootNode.removeAllChildren();
                rootNode.addChild( openPanelNode );
            }
        } );
        collapseButton.addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseReleased( PInputEvent event ) {
                rootNode.removeAllChildren();
                rootNode.addChild( closedPanelNode );
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
            rootNode.removeAllChildren();
            rootNode.addChild( isOpen ? openPanelNode : closedPanelNode );
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
     * Set the minimum width of the control panel.  This is useful when trying
     * to get several of these panels to have the same width in order to look
     * good when laid out vertically.
     *
     * @param minWidth
     */
    public void setMinWidth( double minWidth ) {
        if ( minWidth != this.minWidth ) {
            this.minWidth = minWidth;
            updatePadding();
        }
    }

    /*
     * Update the padding so that the min width is met and the open and closed
     * sizes are the same.
     */
    private void updatePadding() {

        // Reset all padding.
        titlePad.removeAllChildren();
        leftContentPad.removeAllChildren();
        rightContentPad.removeAllChildren();

        // Deduce the total inset amount used by the control panel.
        double controlPanelInsets = closedPanelNode.getFullBoundsReference().width - closedPanelContents.getFullBoundsReference().width;

        // Determine the required overall width.
        double contentWidth = Math.max( Math.max( closedPanelContents.getFullBoundsReference().width, controls.getFullBoundsReference().width ),
                                        minWidth - controlPanelInsets );
        if ( contentWidth > closedPanelContents.getFullBoundsReference().width ) {
            titlePad.addChild( new HPad( contentWidth - closedPanelContents.getFullBoundsReference().width ) );
        }
        if ( contentWidth > controls.getFullBoundsReference().width ) {
            double padWidth = ( contentWidth - controls.getFullBoundsReference().width ) / 2;
            leftContentPad.addChild( new HPad( padWidth ) );
            rightContentPad.addChild( new HPad( padWidth ) );
        }
    }

    // Convenience class for horizontal padding of control panel.
    private static class HPad extends PNode {
        private static final double PAD_RECT_HEIGHT = 0.01;
        private static final Color INVISIBLE_COLOR = new Color( 0, 0, 0, 0 );

        private HPad( double width ) {
            addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, width, PAD_RECT_HEIGHT ), INVISIBLE_COLOR ) );
        }
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
