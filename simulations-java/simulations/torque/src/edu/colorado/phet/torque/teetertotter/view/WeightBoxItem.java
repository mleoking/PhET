// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.view;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.torque.teetertotter.model.TeeterTotterTorqueModel;
import edu.colorado.phet.torque.teetertotter.model.UserMovableModelElement;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for the Piccolo nodes that appear in the weight box and that can
 * be clicked on by the user in order to add model elements to the model.
 *
 * @author John Blanco
 */
public abstract class WeightBoxItem extends PComposite {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    // Fixed transform for setting the size of the items in the tool box,
    // which may not be exactly what it is in the model.
    protected static final ModelViewTransform SCALING_MVT =
            ModelViewTransform.createOffsetScaleMapping( new Point2D.Double( 0, 0 ), 150 );

    private static final double CAPTION_OFFSET_FROM_SELECTION_NODE = 4;

    // Font to use for labels.
    private static final Font LABEL_FONT = new PhetFont( 16 );

    // Element in the model that is being moved by the user.  Only exists if
    // the user performed some action that caused this to be created, such as
    // clicking on this node.
    UserMovableModelElement modelElement = null;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    protected final TeeterTotterTorqueModel model;
    private final PhetPCanvas canvas;
    private final ModelViewTransform mvt;
    private PNode selectionNode = null;
    private PText caption = null;
    private RestoreDefaultOnReleaseCursorHandler cursorHandler = new RestoreDefaultOnReleaseCursorHandler( Cursor.HAND_CURSOR );

    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------

    public WeightBoxItem( final TeeterTotterTorqueModel model, final ModelViewTransform mvt, final PhetPCanvas canvas ) {

        this.model = model;
        this.mvt = mvt;
        this.canvas = canvas;

        // Set up handling of mouse events.
        addInputEventListener( cursorHandler );
        addInputEventListener( new PBasicInputEventHandler() {
            @Override
            public void mousePressed( PInputEvent event ) {
                // Figure out the correspondence between this press event and model space.
                Point2D mouseCanvasPos = event.getCanvasPosition();
                Point2D mouseWorldPos = new Point2D.Double( mouseCanvasPos.getX(), mouseCanvasPos.getY() );
                canvas.getPhetRootNode().screenToWorld( mouseWorldPos );
                Point2D mouseModelPos = mvt.viewToModel( mouseWorldPos );
                modelElement = addElementToModel( mouseModelPos );
            }

            @Override
            public void mouseDragged( PInputEvent event ) {
                // Figure out the correspondence between this drag event and model space.
                Point2D mouseCanvasPos = event.getCanvasPosition();
                Point2D mouseWorldPos = new Point2D.Double( mouseCanvasPos.getX(), mouseCanvasPos.getY() );
                canvas.getPhetRootNode().screenToWorld( mouseWorldPos );
                Point2D mouseModelPos = mvt.viewToModel( mouseWorldPos );
                modelElement.setPosition( mouseModelPos );
            }

            @Override
            public void mouseReleased( PInputEvent event ) {
                // The user has released this node.
                modelElement.release();
                modelElement = null;
            }
        } );
    }

    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------

    /**
     * Method overriden by subclasses to add the element that they represent
     * to the model.
     *
     * @param position
     */
    protected abstract UserMovableModelElement addElementToModel( Point2D position );

    protected void setSelectionNode( PNode selectionNode ) {
        assert this.selectionNode == null; // Currently doesn't support setting this multiple times.
        this.selectionNode = selectionNode;
        addChild( selectionNode );
        updateLayout();
    }

    protected void setCaption( String captionString ) {
        caption = new PText( captionString );
        caption.setFont( LABEL_FONT );
        addChild( caption );
        updateLayout();
    }

    private void updateLayout() {
        // This only does something if both the element node and the caption
        // are set.
        if ( caption != null && selectionNode != null ) {
            caption.setOffset( -caption.getFullBoundsReference().getWidth() / 2,
                               selectionNode.getFullBoundsReference().getMaxY() + CAPTION_OFFSET_FROM_SELECTION_NODE );
        }
    }
}
