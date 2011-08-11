// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.view;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.teetertotter.model.BalancingActModel;
import edu.colorado.phet.balanceandtorque.teetertotter.model.UserMovableModelElement;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for the Piccolo nodes that appear in the view, generally in some
 * sort of tool box, and that can be clicked on by the user in order to add
 * model elements to the model.
 *
 * @author John Blanco
 */
public abstract class ModelElementCreatorNode extends PComposite {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    private static final double CAPTION_OFFSET_FROM_SELECTION_NODE = 4;

    // Font to use for labels.
    private static final Font LABEL_FONT = new PhetFont( 16 );

    // Element in the model that is being moved by the user.  Only exists if
    // the user performed some action that caused this to be created, such as
    // clicking on this node.
    UserMovableModelElement modelElement = null;

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    protected final BalancingActModel model;
    private final PhetPCanvas canvas;
    private final ModelViewTransform mvt;
    private PNode selectionNode = null;
    private PText caption = null;
    private RestoreDefaultOnReleaseCursorHandler cursorHandler = new RestoreDefaultOnReleaseCursorHandler( Cursor.HAND_CURSOR );
    private final Vector2D positioningOffset = new Vector2D( 0, 0 ); // In view coordinate system.

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public ModelElementCreatorNode( final BalancingActModel model, final ModelViewTransform mvt, final PhetPCanvas canvas ) {

        this.model = model;
        this.mvt = mvt;
        this.canvas = canvas;

        // Set up handling of mouse events.
        addInputEventListener( cursorHandler );
        addInputEventListener( new PBasicInputEventHandler() {
            @Override
            public void mousePressed( PInputEvent event ) {
                modelElement = addElementToModel( getModelPosition( event.getCanvasPosition() ) );
            }

            @Override
            public void mouseDragged( PInputEvent event ) {
                modelElement.setPosition( getModelPosition( event.getCanvasPosition() ) );
            }

            @Override
            public void mouseReleased( PInputEvent event ) {
                // The user has released this node.
                modelElement.release();
                modelElement = null;
            }
        } );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    /**
     * Method overriden by subclasses to add the element that they represent to
     * the model.
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

    protected PNode getSelectionNode() {
        return selectionNode;
    }

    protected void setCaption( String captionString ) {
        caption = new PText( captionString );
        caption.setFont( LABEL_FONT );
        addChild( caption );
        updateLayout();
    }

    /**
     * Set an offset to use when adding the initial model element.  This is
     * useful in making sure that the newly created model object is positioned
     * well beneath the mouse.
     */
    protected void setPositioningOffset( double x, double y ) {
        positioningOffset.setComponents( x, y );
    }

    /**
     * Convert the canvas position to the corresponding location in the model.
     */
    private Point2D getModelPosition( Point2D canvasPos ) {
        Point2D worldPos = new Point2D.Double( canvasPos.getX(), canvasPos.getY() );
        canvas.getPhetRootNode().screenToWorld( worldPos );
        worldPos = new Vector2D( worldPos ).add( positioningOffset ).toPoint2D();
        Point2D modelPos = mvt.viewToModel( worldPos );
        return modelPos;
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
