// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.bendinglight.model.BendingLightModel;
import edu.colorado.phet.common.phetcommon.model.ResetModel;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.ToolNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * A Tool is used in a ToolboxNode for creating objects by dragging them out.
 *
 * @author Sam Reid
 */
public class ToolIconNode<T extends BendingLightModel> extends PNode {
    private final Property<Boolean> showToolInPlayArea;//Whether the tool should be shown in the play area (e.g., if it has been dragged out)
    private final ModelViewTransform transform;
    private final BendingLightCanvas<T> canvas;
    private final NodeFactory nodeMaker;
    private final ResetModel resetModel;
    private final Function0<Rectangle2D> globalToolboxBounds;//For dropping the tool back in the toolbox
    private final boolean dragMultiple;//True if multiple copies of the tool can be dragged out

    public ToolIconNode( final Image thumbnail,
                         final Property<Boolean> showToolInPlayArea,
                         final ModelViewTransform transform,
                         final BendingLightCanvas<T> canvas,
                         final NodeFactory nodeMaker,
                         final ResetModel resetModel,
                         final Function0<Rectangle2D> globalToolboxBounds ) {
        this( thumbnail, showToolInPlayArea, transform, canvas, nodeMaker, resetModel, globalToolboxBounds, false );//Only one copy of a tool by default
    }

    public ToolIconNode( final Image thumbnail,
                         final Property<Boolean> showToolInPlayArea,
                         final ModelViewTransform transform,
                         final BendingLightCanvas<T> canvas,
                         final NodeFactory nodeMaker,
                         final ResetModel resetModel,
                         final Function0<Rectangle2D> globalToolboxBounds,
                         final boolean dragMultiple ) {
        this.showToolInPlayArea = showToolInPlayArea;
        this.transform = transform;
        this.canvas = canvas;
        this.nodeMaker = nodeMaker;
        this.resetModel = resetModel;
        this.globalToolboxBounds = globalToolboxBounds;
        this.dragMultiple = dragMultiple;
        //Create the thumbnail to show in the toolbox (if the object should be shown)
        addChild( new PImage( thumbnail ) {{
            showToolInPlayArea.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( !showToolInPlayArea.getValue() );
                }
            } );

            //Add user interaction
            addInputEventListener( new ToolDragListener( this ) );
            addInputEventListener( new CursorHandler() );
        }} );
    }

    //Provide a point of abstraction for adding children to a canvas so that they may optionally be put in different layers.
    protected void addChild( BendingLightCanvas<T> canvas, ToolNode node ) {
        canvas.addChild( node );
    }

    protected void removeChild( BendingLightCanvas<T> canvas, ToolNode node ) {
        canvas.removeChild( node );
    }

    //input listener that will handle creating and dragging out the tool
    class ToolDragListener extends PBasicInputEventHandler {
        private final PImage thumbnailIcon;
        private ToolNode node = null;//The node that has been dragged out
        boolean intersect = false;//true if the node is ready to be dropped back in the toolbox
        private CanvasBoundedDragHandler dragHandler;//Used for dragging the created ToolNode, but making sure it remains in canvas bounds
        private PropertyChangeListener boundChangeListener;

        ToolDragListener( final PImage thumbnailIcon ) {
            this.thumbnailIcon = thumbnailIcon;

            //Reset this node when the model signifies a reset
            resetModel.addResetListener( new VoidFunction0() {
                public void apply() {
                    reset();
                }
            } );
        }

        // Create the node and add it to the scene
        @Override public void mousePressed( PInputEvent event ) {
            if ( !dragMultiple ) {
                showToolInPlayArea.setValue( true );
                thumbnailIcon.setVisible( false );
            }

            //If the node hasn't already been created, make it now
            if ( node == null || dragMultiple ) {
                final ToolNode nodeRef = nodeMaker.createNode( transform, showToolInPlayArea, transform.viewToModel( event.getPositionRelativeTo( canvas.getRootNode() ) ) );
                node = nodeRef;

                //Determine if the node is ready to be dropped back in the toolbox
                boundChangeListener = new PropertyChangeListener() {
                    public void propertyChange( PropertyChangeEvent evt ) {
                        boolean anyIntersection = false;
                        //It can be dropped back in if any of its components are over the toolbox
                        for ( PNode child : nodeRef.getDroppableComponents() ) {
                            PBounds bound = child.getGlobalFullBounds();
                            if ( globalToolboxBounds.apply().contains( bound.getCenterX(), bound.getCenterY() ) ) {
                                anyIntersection = true;
                            }
                        }
                        intersect = anyIntersection;
                    }
                };
                nodeRef.addPropertyChangeListener( PROPERTY_FULL_BOUNDS, boundChangeListener );

                //When the mouse is released, if the node is over the toolbox, drop it back in
                nodeRef.addInputEventListener( new PBasicInputEventHandler() {
                    public void mouseReleased( PInputEvent event ) {
                        testDropIn( nodeRef );
                    }
                } );

                //Put the created node in the canvas
                addChild( canvas, nodeRef );

                //Create a new bounded drag handler now that everything is initialized
                dragHandler = new BoundedToolDragHandler( nodeRef, event );

                //Create a closure on the nodeRef instance to make sure it gets removed when the sim is reset
                resetModel.addResetListener( new VoidFunction0() {
                    public void apply() {
                        removeChild( canvas, nodeRef );
                    }
                } );
            }
        }

        //Process events with the bounded drag handler, which ensures the node remains in the visible area
        @Override public void mouseDragged( PInputEvent event ) {
            dragHandler.mouseDragged( event );
        }

        //This is when the user drags the object out of the toolbox then drops it right back in the toolbox.
        public void mouseReleased( PInputEvent event ) {
            testDropIn( node );
        }

        //Checks to see if the node is over the toolbox and ready to be dropped in.  If so, it is dropped in and removed from the canvas.
        public void testDropIn( ToolNode node ) {
            if ( intersect ) {
                //Update the model to signify the tool is out of the play area
                showToolInPlayArea.setValue( false );

                //Show the thumbnail again so it can be dragged out again
                thumbnailIcon.setVisible( true );

                //Remove listeners to prevent memory leaks
                if ( node != null && boundChangeListener != null ) {
                    node.removePropertyChangeListener( boundChangeListener );
                }

                if ( node != null ) {
                    removeChild( canvas, node );
                }

                //Remove the tool from the play area
                reset();
            }
        }

        //Remove the created node, if any
        private void reset() {
            //The node is removed in the resetModel listener above, this part just sets a flag to indicate that a new toolnode should be created
            node = null;
        }
    }
}