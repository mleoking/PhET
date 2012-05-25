// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
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

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    private static final boolean INITIAL_OPEN_STATE = false;
    private static final double BOX_SPACING = 10;
    private static final int CONTROL_PANEL_INSET = 9;

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Various nodes that comprise this node.  They need to be member variables
    // so that layout can be adjusted when needed.
    private final PNode controls;
    private final PNode titleBar;
    private final PNode titleBarSpacer;

    // The minimum width for the control panel, can be set by client.
    private double minWidth = 0;

    // Button that also tracks the open/closed state.
    private OpenCloseButton openCloseButton;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    /**
     * Constructor.
     *
     * @param backgroundColor
     * @param titleText
     * @param titleFont
     * @param controls
     */
    public CollapsibleControlPanel( Color backgroundColor, String titleText, Font titleFont, final PNode controls ) {
        this.controls = controls;

        // Create the title bar.
        openCloseButton = new OpenCloseButton( INITIAL_OPEN_STATE );
        titleBar = new PNode();
        titleBar.addChild( new HBox( BOX_SPACING, openCloseButton, new PhetPText( titleText, titleFont ) ) );
        titleBarSpacer = new PNode();
        titleBar.addChild( titleBarSpacer );

        // Create the container node where the controls will be added or
        // removed based on the open/closed state of the panel.
        final PNode controlsContainer = new PNode();

        // Create and add the control panel.
        ControlPanelNode controlPanel = new ControlPanelNode( new VBox( BOX_SPACING, VBox.LEFT_ALIGNED, titleBar, controlsContainer ),
                                                              backgroundColor,
                                                              new BasicStroke( 2 ),
                                                              Color.BLACK,
                                                              CONTROL_PANEL_INSET );
        addChild( controlPanel );

        // Add a listener that adds or removes the controls based on the button
        // state.  This is where the collapse or expand operations occur.
        openCloseButton.open.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean open ) {
                if ( open ) {
                    controlsContainer.addChild( controls );
                }
                else {
                    controlsContainer.removeAllChildren();
                }
            }
        } );

        updateLayout();
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    /**
     * Set the panel to be open or closed.
     *
     * @param open
     */
    public void setOpen( boolean open ) {
        openCloseButton.open.set( open );
    }

    /**
     * Get the full bounds of this panel when it is opened.  This is useful
     * for doing layout.
     *
     * @return
     */
    public PBounds getFullBoundsWhenOpen() {
        boolean wasOpen = openCloseButton.open.get();
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
            updateLayout();
        }
    }

    /*
     * Update the layout so that the min width is met and the open and closed
     * sizes are the same.
     */
    private void updateLayout() {

        // Reset padding and offsets.
        titleBarSpacer.removeAllChildren();
        controls.setOffset( 0, 0 );

        // Determine the minimum width of the content of the control panel.
        double minContentWidth = Math.max( Math.max( controls.getFullBoundsReference().width, titleBar.getFullBoundsReference().width ),
                                           minWidth - 2 * CONTROL_PANEL_INSET );

        // Pad the title bar if needed.
        if ( titleBar.getFullBoundsReference().width < minContentWidth ) {
            titleBarSpacer.addChild( new HPad( minContentWidth ) );
        }

        // Offset the controls if needed.
        if ( controls.getFullBoundsReference().width < minContentWidth ) {
            controls.setOffset( minContentWidth / 2 - controls.getFullBoundsReference().width / 2, 0 );
        }
    }

    //-------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //-------------------------------------------------------------------------

    // Convenience class for horizontal padding of control panel.
    private static class HPad extends PNode {
        private static final double PAD_RECT_HEIGHT = 0.01;
        private static final Color INVISIBLE_COLOR = new Color( 0, 0, 0, 0 );

        private HPad( double width ) {
            addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, width, PAD_RECT_HEIGHT ), INVISIBLE_COLOR ) );
        }
    }

    // PNode that is the button that is used to open/close the panel.
    private static class OpenCloseButton extends PNode {
        public final BooleanProperty open;
        private final PNode openButton = new PImage( PhetCommonResources.getMaximizeButtonImage() );
        private final PNode closeButton = new PImage( PhetCommonResources.getMinimizeButtonImage() );

        private OpenCloseButton( boolean initiallyOpen ) {
            addInputEventListener( new CursorHandler() );
            addChild( openButton );
            addChild( closeButton );
            open = new BooleanProperty( initiallyOpen );
            open.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean open ) {
                    openButton.setVisible( !open );
                    closeButton.setVisible( open );
                }
            } );

            openButton.addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mouseReleased( PInputEvent event ) {
                    open.set( true );
                }
            } );
            closeButton.addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mouseReleased( PInputEvent event ) {
                    open.set( false );
                }
            } );
        }
    }

    //-------------------------------------------------------------------------
    // Test Harness
    //-------------------------------------------------------------------------

    public static void main( String[] args ) {

        // Create the canvas.
        PhetPCanvas canvas = new PhetPCanvas();

        // Set up the canvas-screen transform.
        Dimension2D stageSize = new PDimension( 700, 500 );
        canvas.setWorldTransformStrategy( new PhetPCanvas.CenteredStage( canvas, stageSize ) );

        // Create the node(s) being tested.
        CollapsibleControlPanel controlPanel = new CollapsibleControlPanel( new Color( 176, 226, 255 ),
                                                                            "Wide Controls",
                                                                            new PhetFont( 16, true ),
                                                                            new PhetPPath( new Rectangle2D.Double( 0, 0, 200, 100 ), Color.PINK ) );
        controlPanel.setOffset( 50, 100 );
        canvas.addWorldChild( controlPanel );

        CollapsibleControlPanel controlPanel2 = new CollapsibleControlPanel( new Color( 220, 226, 226 ),
                                                                             "Narrow Controls",
                                                                             new PhetFont( 16, true ),
                                                                             new PhetPPath( new Rectangle2D.Double( 0, 0, 50, 100 ), Color.GREEN ) );
        controlPanel2.setOffset( 300, 100 );
        canvas.addWorldChild( controlPanel2 );

        // Boiler plate app stuff.
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.setSize( (int) stageSize.getWidth(), (int) stageSize.getHeight() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocationRelativeTo( null ); // Center.
        frame.setVisible( true );
    }
}
