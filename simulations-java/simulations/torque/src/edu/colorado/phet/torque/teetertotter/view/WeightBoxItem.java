// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.view;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.torque.teetertotter.model.TeeterTotterTorqueModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for the model elements that appear in the weight box.  This class
 * handles the common functionality, and subclasses implement the needed
 * unique behavior.
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
            ModelViewTransform.createOffsetScaleMapping( new Point2D.Double( 0, 0 ), 1 );

    private static final double CAPTION_OFFSET_FROM_SELECTION_NODE = 4;

    // Font to use for labels.
    private static final Font LABEL_FONT = new PhetFont( 20 );

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private final TeeterTotterTorqueModel model;
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
        initializeSelectionNode();
        updateLayout();

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

                addElementToModel( mouseModelPos );
            }

            @Override
            public void mouseDragged( PInputEvent event ) {
                // Figure out the correspondence between this drag event and model space.
                Point2D mouseCanvasPos = event.getCanvasPosition();
                Point2D mouseWorldPos = new Point2D.Double( mouseCanvasPos.getX(), mouseCanvasPos.getY() );
                canvas.getPhetRootNode().screenToWorld( mouseWorldPos );
                Point2D mouseModelPos = mvt.viewToModel( mouseWorldPos );

                setModelElementPosition( mouseModelPos );
            }

            @Override
            public void mouseReleased( PInputEvent event ) {
                // The user has released this node.
                releaseModelElement();
            }
        } );
    }

    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------

    protected TeeterTotterTorqueModel getModel() {
        return model;
    }

    protected PhetPCanvas getCanvas() {
        return canvas;
    }

    /**
     * Method overridden by subclasses to set up the node that users will
     * click on in order to add one to the model.
     */
    protected abstract void initializeSelectionNode();

    /**
     * Method overriden by subclasses to add the element that they represent
     * to the model.
     *
     * @param position
     */
    protected abstract void addElementToModel( Point2D position );

    /**
     * Method overridden by subclasses that sets the position of the
     * corresponding model element, if there is one.
     *
     * @param position
     */
    protected abstract void setModelElementPosition( Point2D position );

    /**
     * Method overridden by subclasses that commands them to release the model
     * element that they were presumably controlling.
     */
    protected abstract void releaseModelElement();

    protected void setSelectionNode( PNode selectionNode ) {
        assert this.selectionNode == null; // Currently doesn't support setting this multiple times.
        this.selectionNode = selectionNode;
        addChild( selectionNode );
    }

    protected void setCaption( String captionString ) {
        caption = new PText( captionString );
        caption.setFont( LABEL_FONT );
        addChild( caption );
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
