// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.intro;

import java.awt.*;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.bendinglight.view.BendingLightCanvas;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * @author Sam Reid
 */
public class Tool extends PNode {

    public static interface NodeFactory {
        DraggableNode createNode( ModelViewTransform transform, Property<Boolean> visible, Point2D location );
    }

    public Tool( Image thumbnail,
                 final Property<Boolean> showTool,
                 final ModelViewTransform transform,
                 final ToolboxNode toolbox,
                 final BendingLightCanvas canvas,
                 final NodeFactory nodeMaker,
                 final ResetModel resetModel ) {
        final PImage thumbnailIcon = new PImage( thumbnail ) {{
            showTool.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( !showTool.getValue() );
                }
            } );
            final PImage thumbRef = this;
            addInputEventListener( new PBasicInputEventHandler() {
                {
                    resetModel.addResetListener( new VoidFunction0() {
                        public void apply() {
                            reset();
                        }
                    } );
                }

                DraggableNode node = null;
                boolean intersect = false;

                @Override
                public void mousePressed( PInputEvent event ) {
                    showTool.setValue( true );
                    setVisible( false );
                    if ( node == null ) {
                        node = nodeMaker.createNode( transform, showTool, transform.viewToModel( event.getPositionRelativeTo( canvas.getRootNode() ) ) );
                        final PropertyChangeListener pcl = new PropertyChangeListener() {
                            public void propertyChange( PropertyChangeEvent evt ) {
                                intersect = toolbox.getGlobalFullBounds().contains( node.getGlobalFullBounds().getCenter2D() );
                            }
                        };
                        node.addPropertyChangeListener( PROPERTY_FULL_BOUNDS, pcl );
                        node.addInputEventListener( new PBasicInputEventHandler() {
                            public void mouseReleased( PInputEvent event ) {
                                if ( intersect ) {
                                    showTool.setValue( false );
                                    thumbRef.setVisible( true );
                                    node.removePropertyChangeListener( pcl );
                                    reset();
                                }
                            }
                        } );

                        canvas.addChild( node );
                    }
                }

                public void mouseDragged( PInputEvent event ) {
                    node.doDrag( event );
                }

                //This is when the user drags the object out of the toolbox then drops it right back in the toolbox.
                public void mouseReleased( PInputEvent event ) {
                    if ( intersect ) {
                        showTool.setValue( false );
                        thumbRef.setVisible( true );
                        reset();
                        //TODO: how to remove pcl?
                    }
                }

                private void reset() {
                    canvas.removeChild( node );
                    node = null;
                }
            } );
            addInputEventListener( new CursorHandler() );
        }};

        addChild( thumbnailIcon );
    }

}
